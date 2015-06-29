package de.neemann.assembler.gui;

import javax.swing.*;
import java.awt.*;

/**
 * @author hneemann
 */
public class ListDialog extends JDialog {
    public ListDialog(Main main, String listing) {
        super(main, "Listing");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JTextArea source = new JTextArea(listing);
        source.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        getContentPane().add(new JScrollPane(source));

        pack();
        setLocationRelativeTo(main);
    }
}
