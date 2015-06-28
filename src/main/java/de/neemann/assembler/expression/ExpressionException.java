package de.neemann.assembler.expression;

/**
 * @author hneemann
 */
public class ExpressionException extends Exception {

    private int lineNumber = -1;

    public ExpressionException(String message) {
        super(message);
    }

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
