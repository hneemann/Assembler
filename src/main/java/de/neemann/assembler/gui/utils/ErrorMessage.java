package de.neemann.assembler.gui.utils;

import javax.swing.*;
import java.awt.*;

/**
 * Helper class to show an error message
 * <p>
 * Created by hneemann on 09.02.14.
 */
public class ErrorMessage {

    private final StringBuilder message;

    /**
     * Creates a new error message
     */
    public ErrorMessage() {
        this("");
    }

    /**
     * Creates a new error message
     *
     * @param message the message
     */
    public ErrorMessage(String message) {
        this.message = new StringBuilder(message);
    }

    /**
     * Adds a cause to the error message
     *
     * @param e the cause
     * @return this for chained calls
     */
    public ErrorMessage addCause(Throwable e) {
        e.printStackTrace();

        while (e != null) {
            if (message.length() > 0)
                message.append('\n');
            message.append(e.getClass().getSimpleName());
            if (e.getMessage() != null)
                message.append("; ").append(e.getMessage());
            e = e.getCause();
        }
        return this;
    }


    /**
     * Show the error message
     *
     * @return this for chained calls
     */
    public ErrorMessage show() {
        return show(null);
    }

    /**
     * Show the error message
     *
     * @param parent the gui parent
     * @return this for chained calls
     */
    public ErrorMessage show(Component parent) {
        JOptionPane.showMessageDialog(parent, message.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        return this;
    }

}
