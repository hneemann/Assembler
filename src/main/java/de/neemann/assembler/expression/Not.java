package de.neemann.assembler.expression;

/**
 * @author hneemann
 */
public class Not implements Expression {
    private Expression value;

    public Not(Expression value) {
        this.value = value;
    }

    @Override
    public int getValue(Context context) throws ExpressionException {
        return ~value.getValue(context);
    }
}
