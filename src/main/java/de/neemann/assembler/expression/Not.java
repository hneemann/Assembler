package de.neemann.assembler.expression;

/**
 * Performs a bitwise not an an expression
 *
 * @author hneemann
 */
public class Not implements Expression {
    private Expression value;

    /**
     * Creates a bitwise not operation
     *
     * @param value the value to invert
     */
    public Not(Expression value) {
        this.value = value;
    }

    @Override
    public int getValue(Context context) throws ExpressionException {
        return ~value.getValue(context);
    }

    @Override
    public String toString() {
        return "~" + Operate.checkBrace(value);
    }

}
