package de.neemann.assembler.expression;

/**
 * @author hneemann
 */
public class ExpressionException extends Exception {

    private int lineNumber = -1;

    /**
     * Creates a new instance
     *
     * @param message the message
     */
    public ExpressionException(String message) {
        super(message);
    }

    /**
     * Sets the line number of the instruction which caused this exception
     *
     * @param lineNumber the line number
     */
    public void setLineNumber(int lineNumber) {
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
