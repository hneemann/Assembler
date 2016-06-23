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

    private final Component frame;

    public RemoteInterface(Component frame) {
        this.frame = frame;
    }

    public boolean load(File file) {
        return sendRequest("load",file.getPath());
    }

    public boolean start() {
        return sendRequest("start",null);
    }

    public boolean run() {
        return sendRequest("run",null);
    }

    public boolean stop() {
        return sendRequest("stop",null);
    }


    public boolean step() {
        return sendRequest("step",null);
    }

    private boolean sendRequest(String command, String args) {
        try {
            Socket s = new Socket(InetAddress.getLocalHost(),41114);
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            if (args!=null)
                command=command+":"+args;
            out.writeUTF(command);
            DataInputStream in = new DataInputStream(s.getInputStream());
            String response = in.readUTF();
            if (response.equals("ok"))
                return true;

            new ErrorMessage("Error received from simulator:\n" +response).show(frame);
        } catch (IOException e) {
            new ErrorMessage("Error communicating with simulator!").addCause(e).show(frame);
        }
        return false;
    }
}
