package de.neemann.assembler.expression;

/**
 * The expression interface
 *
 * @author hneemann
 */
public interface Expression {

    /**
     * Returns the value of thes expression
     *
     * @param context the context of evaluation
     * @return the value
     * @throws ExpressionException ExpressionException
     */
    int getValue(Context context) throws ExpressionException;

}
