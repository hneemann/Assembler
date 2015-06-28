package de.neemann.assembler.expression;

/**
 * @author hneemann
 */
public interface Expression {

    int getValue(Context context) throws ExpressionException;

}
