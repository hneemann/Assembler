package de.neemann.assembler.gui;

/**
 * Brakes a long string in several lines
 *
 * @author hneemann
 */
public class TextFormatter {
    private int lineLen;
    private StringBuilder sb;
    private StringBuilder word;
    private int col;

    /**
     * Creates a new instace
     *
     * @param lineLen the line len
     */
    public TextFormatter(int lineLen) {
        init(lineLen);
    }

    /**
     * Creates a new instace
     *
     * @param lineLen the line len
     * @param text    the text to break into lines
     */
    public TextFormatter(int lineLen, String text) {
        init(lineLen);
        append(text);
    }

    private void init(int lineLen) {
        this.lineLen = lineLen;
        sb = new StringBuilder();
        word = new StringBuilder();
    }

    /**
     * Append some text
     *
     * @param text the text
     * @return this for chained calls
     */
    public TextFormatter append(String text) {
        for (char c : text.toCharArray())
            append(c);
        return this;
    }

    private TextFormatter append(char c) {

        switch (c) {
            case ' ':
                addLastWord();
                sb.append(' ');
                col++;
                break;
            case '\t':
                addLastWord();
                col = ((col / 8) + 1) * 8;
                word.append(c);
                break;
            case '\n':
            case '\r':
                addLastWord();
                col = 0;
            default:
                word.append(c);
        }

        return this;
    }

    private void addLastWord() {
        if (col + word.length() > lineLen) {
            sb.append("\n\t");
            col = 8;
        }
        sb.append(word);
        col += word.length();
        word.setLength(0);
    }

    /**
     * @return the created new string
     */
    public String toString() {
        addLastWord();
        return sb.toString();
    }

}
