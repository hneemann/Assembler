package de.neemann.assembler.expression;

/**
 * @author hneemann
 */
public class Operate implements Expression {

    private final Expression a;
    private final Expression b;
    private final Operation op;

    public enum Operation {OR, AND, MUL, ADD, XOR, SUB}

    public Operate(Expression a, Operation op, Expression b) {
        this.a = a;
        this.op = op;
        this.b = b;
    }

    @Override
    public int getValue(Context context) throws ExpressionException {
        int av = a.getValue(context);
        int bv = b.getValue(context);
        switch (op) {
            case OR:
                return av | bv;
            case AND:
                return av & bv;
            case XOR:
                return av ^ bv;
            case ADD:
                return av + bv;
            case SUB:
                return av - bv;
            case MUL:
                return av * bv;
        }
        throw new ExpressionException("operation " + op.name() + " not supported!");
    }

}
