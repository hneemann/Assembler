package de.neemann.assembler.gui;

/**
 * Listener to be notified if the actual code address has changed
 * Created by hneemann on 16.12.16.
 */
public interface AddressListener {

    /**
     * Sets the actual code address
     *
     * @param addr the code address
     */
    void setCodeAddress(int addr);

    /**
     * The code lines has changed, so the addresses may not match the line numbers.
     */
    void invalidateCode();
}
