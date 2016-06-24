package de.neemann.assembler.gui;

import de.neemann.assembler.gui.utils.ErrorMessage;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by helmut.neemann on 23.06.2016.
 */
public class RemoteInterface {

    public void load(File file) throws RemoteException {
        sendRequest("load",file.getPath());
    }

    public void start() throws RemoteException {
        sendRequest("start",null);
    }

    public void run() throws RemoteException {
        sendRequest("run",null);
    }

    public void stop() throws RemoteException {
        sendRequest("stop",null);
    }


    public void step() throws RemoteException {
        sendRequest("step",null);
    }

    private void sendRequest(String command, String args) throws RemoteException {
        try {
            Socket s = new Socket(InetAddress.getLocalHost(),41114);
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            if (args!=null)
                command=command+":"+args;
            out.writeUTF(command);
            DataInputStream in = new DataInputStream(s.getInputStream());
            String response = in.readUTF();
            if (!response.equals("ok"))
                throw new RemoteException("Error received from simulator:\n" +response);
        } catch (IOException e) {
            throw new RemoteException("Error communicating with simulator!", e);
        }
    }
}
