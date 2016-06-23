package de.neemann.assembler.gui;

import de.neemann.assembler.gui.utils.ErrorMessage;

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

    public void load(File file) {
        sendRequest("load",file.getPath());
    }

    public void start() {
        sendRequest("start",null);
    }

    public void run() {
        sendRequest("run",null);
    }

    public void step() {
        sendRequest("step",null);
    }

    private void sendRequest(String command, String args) {
        try {
            Socket s = new Socket(InetAddress.getLocalHost(),41114);
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            if (args!=null)
                command=command+":"+args;
            out.writeUTF(command);
            DataInputStream in = new DataInputStream(s.getInputStream());
            String response = in.readUTF();
            if (!response.equals("ok"))
                new ErrorMessage(response).show();
        } catch (IOException e) {
            new ErrorMessage("Error communicating with simulator").addCause(e).show();
        }
    }
}
