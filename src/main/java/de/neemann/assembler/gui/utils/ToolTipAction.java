package de.neemann.assembler.gui.utils;

import javax.swing.*;
import java.awt.*;

/**
 * Action which can handle a tool tip
 * Created by hneemann on 06.03.15.
 */
public abstract class ToolTipAction extends AbstractAction {
    private Icon icon;
    private String toolTipText;

    /**
     * Creates a new instance
     *
     * @param name the actions name
     */
    public ToolTipAction(String name) {
        super(name);
    }

    /**
     * Creates a new instance
     *
     * @param name the actions name
     * @param icon the icon to use
     */
    public ToolTipAction(String name, Icon icon) {
        super(name, icon);
        this.icon = icon;
    }

    /**
     * Sets the tool tip text
     *
     * @param text the text
     * @return this for chained calls
     */
    public ToolTipAction setToolTip(String text) {
        this.toolTipText = text;
        return this;
    }

    /**
     * @return a button triggering this action
     */
    public JButton createJButton() {
        JButton b = new JButton(this);
        if (toolTipText != null) {
            b.setToolTipText(toolTipText);
        }
        return b;
    }

    /**
     * @return a button triggering this action, button shows no text but only the icon
     */
    public JButton createJButtonNoText() {
        JButton b = new JButton(this);
        if (toolTipText != null) {
            b.setToolTipText(toolTipText);
        } else {
            b.setToolTipText(b.getText());
        }
        b.setText(null);
        return b;
    }

    /**
     * @return a button triggering this action, button shows no text but only the icon
     */
    public JButton createJButtonNoTextSmall() {
        JButton b = createJButtonNoText();
        b.setPreferredSize(new Dimension(icon.getIconWidth() + 4, icon.getIconHeight() + 4));
        return b;
    }

    /**
     * @return a menu item triggering this action
     */
    public JMenuItem createJMenuItem() {
        JMenuItem i = new JMenuItem(this);
        if (toolTipText != null) {
            i.setToolTipText(toolTipText);
        }
        return i;
    }

    /**
     * @return a menu item triggering this action, item shows no icon
     */
    public JMenuItem createJMenuItemNoIcon() {
        JMenuItem i = createJMenuItem();
        i.setIcon(null);
        return i;
    }

}
