package de.neemann.assembler.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Used to connect the simulator.
 * TCP port 41114 is used to access the simulator
 * Created by helmut.neemann on 23.06.2016.
 */
public class RemoteInterface {

    /**
     * Request to loads a file to the simulated rom
     *
     * @param file the file to load
     * @throws RemoteException RemoteException
     */
    public void load(File file) throws RemoteException {
        sendRequest("load", file.getPath());
    }

    /**
     * Starts the simulation
     *
     * @throws RemoteException RemoteException
     */
    public void start() throws RemoteException {
        sendRequest("start", null);
    }

    /**
     * Run to next break point
     *
     * @throws RemoteException RemoteException
     */
    public void run() throws RemoteException {
        sendRequest("run", null);
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
     * @throws RemoteException RemoteException
     */
    public void step() throws RemoteException {
        sendRequest("step", null);
    }

    private void sendRequest(String command, String args) throws RemoteException {
        try {
            Socket s = new Socket(InetAddress.getLocalHost(), 41114);
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            if (args != null)
                command = command + ":" + args;
            out.writeUTF(command);
            out.flush();
            DataInputStream in = new DataInputStream(s.getInputStream());
            String response = in.readUTF();
            if (!response.equals("ok"))
                throw new RemoteException("Error received from simulator:\n" + response);
        } catch (IOException e) {
            throw new RemoteException("Error communicating with simulator!", e);
        }
    }
}
