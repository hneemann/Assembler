package de.neemann.assembler.gui;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

/**
 * Dialog to show the formatted listing
 *
 * @author hneemann
 */
public class ListDialog extends JDialog implements AdressListener {

    private final HashMap<Integer, Integer> addrToLine;
    private final JTextArea source;

    /**
     * Creates a new instance
     *
     * @param main    the parent frame
     * @param listing the listing to show
     */
    public ListDialog(Main main, String listing) {
        this(main, "Listing", listing, (HashMap<Integer, Integer>) null);
    }

    /**
     * Creates a new instance
     *
     * @param main    the parent frame
     * @param title   the dialogs title
     * @param listing the listing to show
     */
    public ListDialog(Main main, String title, String listing, HashMap<Integer, Integer> addrToLine) {
        this(main, title, listing, new Font(Font.MONOSPACED, Font.PLAIN, 12), addrToLine);
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
        this(main, title, listing, font, null);
    }

    /**
     * Creates a new instance
     *
     * @param main       the parent frame
     * @param title      the dialogs title
     * @param listing    the listing to show
     * @param font       the font to use
     * @param addrToLine the addr to line map
     */
    public ListDialog(final Main main, String title, String listing, Font font, final HashMap<Integer, Integer> addrToLine) {
        super(main, title);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.addrToLine = addrToLine;
        if (addrToLine != null)
            main.addAddrListener(this);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent windowEvent) {
                main.removeAddrListener(ListDialog.this);
            }
        });


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

    @Override
    public void setCodeAddress(int addr) {
        if (addrToLine != null) {
            Integer l = addrToLine.get(addr);
            if (l != null && l > 0) {
                int line = l - 1;
                try {
                    int lineStart = source.getLineStartOffset(line);
                    int lineEnd = source.getLineEndOffset(line) - 1;
                    source.getHighlighter().removeAllHighlights();
                    source.getHighlighter().addHighlight(lineStart, lineEnd, Main.HIGHLIGHT_PAINTER);
                    source.setCaretPosition(lineStart);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
