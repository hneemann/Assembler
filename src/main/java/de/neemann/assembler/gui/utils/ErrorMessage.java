package de.neemann.assembler.gui.utils;

import javax.swing.*;
import java.awt.*;

/**
 * Created by hneemann on 09.02.14.
 */
public class ErrorMessage {

    private final StringBuilder message;

    public ErrorMessage() {
        this("");
    }

    public ErrorMessage(String message) {
        this.message = new StringBuilder(message);
    }

    public ErrorMessage addCause(Throwable e) {
        e.printStackTrace();

        while (e!=null) {
            if (message.length() > 0)
                message.append('\n');
            message.append(e.getClass().getSimpleName() + "; " + e.getMessage());
            e=e.getCause();
        }
        return this;
    }


    public ErrorMessage show() {
        return show(null);
    }

    public ErrorMessage show(Component parent) {
        JOptionPane.showMessageDialog(parent, message.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        return this;
    }

}
