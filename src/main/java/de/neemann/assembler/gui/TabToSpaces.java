package de.neemann.assembler.gui;

/**
 * @author hneemann
 */
public class TabToSpaces {
    private final String text;
    private final int tabSize;
    private final StringBuilder sb;
    private int col;

    public TabToSpaces(String text, int tabSize) {
        this.text = text;
        this.tabSize = tabSize;
        sb = new StringBuilder();
    }

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
