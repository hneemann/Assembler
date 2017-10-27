package de.neemann.assembler.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Used to connect the simulator.
 * TCP port 41114 is used to access the simulator.
 * Created by helmut.neemann on 23.06.2016.
 */
public class RemoteInterface {

    private final InetAddress localHost;

    /**
     * Creates a new instance
     *
     * @throws UnknownHostException UnknownHostException
     */
    public RemoteInterface() throws UnknownHostException {
        localHost = InetAddress.getLocalHost();
    }

    /**
     * Starts the simulation.
     * The clock element in the simulation is configured to start
     * the real time clocking.
     *
     * @param file the hex file to load to program memory
     * @throws RemoteException RemoteException
     */
    public void start(File file) throws RemoteException {
        sendRequest("start", file.getPath());
    }

    /**
     * Debugs the simulation.
     * The clock element in the simulation is configured not to
     * start the real time clock.
     *
     * @param file the hex file to load to program memory
     * @throws RemoteException RemoteException
     */
    public void debug(File file) throws RemoteException {
        sendRequest("debug", file.getPath());
    }

    /**
     * Run to next break point.
     * Runs the simulation until a brk signal is detected.
     *
     * @return the actual code address
     * @throws RemoteException RemoteException
     */
    public int run() throws RemoteException {
        return getAddr(sendRequest("run", null));
    }

    /**
     * Stops the simulation
     *
     * @throws RemoteException RemoteException
     */
    public void stop() throws RemoteException {
        sendRequest("stop", null);
    }

    /**
     * A single clock step.
     *
     * @return the actual code address
     * @throws RemoteException RemoteException
     */
    public int step() throws RemoteException {
        return getAddr(sendRequest("step", null));
    }

    private int getAddr(String addrString) {
        if (addrString.length() <= 3)
            return -1;

        try {
            return Integer.parseInt(addrString.substring(3), 16);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String sendRequest(String command, String args) throws RemoteException {
        try {
            Socket s = new Socket(localHost, 41114);
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            if (args != null)
                command = command + ":" + args;
            out.writeUTF(command);
            // writeUTF writes at first the length of the string as a two byte value (high byte first) to
            // the stream, followed by the utf-8 encoded string. Length means the number of bytes needed to
            // store the UTF-8 encoded string, not the number of characters.
            out.flush();
            DataInputStream in = new DataInputStream(s.getInputStream());
            String response = in.readUTF();
            if (!(response.equals("ok") || response.startsWith("ok:")))
                throw new RemoteException("Error received from simulator:\n" + response);
            return response;
        } catch (IOException e) {
            throw new RemoteException("Error communicating with simulator!", e);
        }
    }
}
