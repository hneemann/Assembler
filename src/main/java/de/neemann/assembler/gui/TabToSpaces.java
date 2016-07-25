package de.neemann.assembler.gui;

/**
 * Used to replace tabs by spaces
 *
 * @author hneemann
 */
public class TabToSpaces {
    private final String text;
    private final int tabSize;
    private final StringBuilder sb;
    private int col;

    /**
     * Creates a new instance
     *
     * @param text    the text
     * @param tabSize the tab size
     */
    public TabToSpaces(String text, int tabSize) {
        this.text = text;
        this.tabSize = tabSize;
        sb = new StringBuilder();
    }

    /**
     * COncert the text
     *
     * @return the new text only containing spaces
     */
    public String convert() {
        col = 0;
        for (char c : text.toCharArray()) {
            switch (c) {
                case '\t':
                    int spaceCount = tabSize - (col % tabSize);
                    for (int i = 0; i < spaceCount; i++)
                        append(' ');
                    break;
                case '\n':
                case '\r':
                    append(c);
                    col = 0;
                    break;
                default:
                    append(c);
            }
        }
        return sb.toString();
    }

    private void append(char c) {
        sb.append(c);
        col++;
    }
}
