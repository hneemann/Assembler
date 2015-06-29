package de.neemann.assembler.parser;

/**
 * @author hneemann
 */
public class ParserException extends Exception {
    private int lineNumber;

    public ParserException(String message, int lineNumber) {
        super(message);
        this.lineNumber = lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        if (this.lineNumber == 0)
            this.lineNumber = lineNumber;
    }

    @Override
    public String getMessage() {
        if (lineNumber > 0) {
            return "line " + lineNumber + ": " + super.getMessage();
        } else
            return super.getMessage();
    }

}
