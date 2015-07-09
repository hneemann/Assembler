package de.neemann.assembler.expression;

/**
 * @author hneemann
 */
public class Neg implements Expression {
    private Expression value;

    public Neg(Expression value) {
        this.value = value;
    }

    @Override
    public int getValue(Context context) throws ExpressionException {
        return -value.getValue(context);
    }

    @Override
    public String toString() {
        return "-" + Operate.checkBrace(value);
    }
}
