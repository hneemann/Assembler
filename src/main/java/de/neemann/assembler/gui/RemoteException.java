package de.neemann.assembler.gui;

/**
 * @author hneemann
 */
public class RemoteException extends Exception {
    public RemoteException(String message) {
        super(message);
    }

    public RemoteException(String message, Exception cause) {
        super(message, cause);
    }
}
