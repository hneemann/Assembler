package de.neemann.assembler.expression;

/**
 * Expression which negates an expression
 *
 * @author hneemann
 */
public class Neg implements Expression {
    private Expression value;

    /**
     * Creates an expression which negates the given expression
     *
     * @param value the expression to negate
     */
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
