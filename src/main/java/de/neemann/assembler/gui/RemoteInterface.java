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
 * TCP port 41114 is used to access the simulator
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
     * Starts the simulation
     *
     * @param file the file to load
     * @throws RemoteException RemoteException
     */
    public void start(File file) throws RemoteException {
        sendRequest("start", file.getPath());
    }

    /**
     * Debugs the simulation
     *
     * @param file the file to load
     * @throws RemoteException RemoteException
     */
    public void debug(File file) throws RemoteException {
        sendRequest("debug", file.getPath());
    }

    /**
     * Run to next break point
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
     * A single clock step
     *
     * @return the actual code address
     * @throws RemoteException RemoteException
     */
    public int step() throws RemoteException {
        return getAddr(sendRequest("step", null));
    }

    private int getAddr(String resp) {
        if (resp.length() <= 3)
            return -1;

        try {
            return Integer.parseInt(resp.substring(3), 16);
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
