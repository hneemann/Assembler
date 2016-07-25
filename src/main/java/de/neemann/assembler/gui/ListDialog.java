package de.neemann.assembler.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog to show the formatted listing
 *
 * @author hneemann
 */
public class ListDialog extends JDialog {

    /**
     * Creates a new instance
     *
     * @param main    the parent frame
     * @param listing the listing to show
     */
    public ListDialog(Main main, String listing) {
        this(main, "Listing", listing);
    }

    /**
     * Creates a new instance
     *
     * @param main    the parent frame
     * @param title   the dialogs title
     * @param listing the listing to show
     */
    public ListDialog(Main main, String title, String listing) {
        this(main, title, listing, new Font(Font.MONOSPACED, Font.PLAIN, 12));
    }

    /**
     * Creates a new instance
     *
     * @param main    the parent frame
     * @param title   the dialogs title
     * @param listing the listing to show
     * @param font    the font to use
     */
    public ListDialog(Main main, String title, String listing, Font font) {
        super(main, title);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JTextArea source;
        if (font != null) {
            source = new JTextArea(listing, 45, 70);
            source.setFont(font);
        } else {
            source = new JTextArea(listing);
        }

        source.setEditable(false);
        getContentPane().add(new JScrollPane(source));

        pack();
        setLocationRelativeTo(main);
    }
}
