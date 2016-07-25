package de.neemann.assembler.gui;

/**
 * Remote exception
 *
 * @author hneemann
 */
public class RemoteException extends Exception {
    /**
     * Creates a new instance
     *
     * @param message the message
     */
    public RemoteException(String message) {
        super(message);
    }

    /**
     * Creates a new instance
     *
     * @param message the message
     * @param cause   the cause
     */
    public RemoteException(String message, Exception cause) {
        super(message, cause);
    }
}
