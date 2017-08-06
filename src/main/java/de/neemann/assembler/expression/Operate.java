package de.neemann.assembler.expression;

/**
 * A operation
 *
 * @author hneemann
 */
public class Operate implements Expression {

    private final Expression a;
    private final Expression b;
    private final Operation op;

    /**
     * the enumeration of possible operations
     */
    public enum Operation {
        //CHECKSTYLE.OFF: JavadocVariable
        OR("|"), AND("&"), MUL("*"), ADD("+"), XOR("^"), DIV("/"), SUB("-");
        //CHECKSTYLE.ON: JavadocVariable
        private final String str;

        Operation(String str) {
            this.str = str;
        }

        /**
         * @return a string representation of the operation
         */
        public String getOpStr() {
            return str;
        }
    }

    /**
     * Creates a new operation
     *
     * @param a  first operand
     * @param op the operation
     * @param b  second operand
     */
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
            case DIV:
                return av / bv;
        }
        throw new ExpressionException("operation " + op.name() + " not supported!");
    }

    @Override
    public String toString() {
        return checkBrace(a) + op.getOpStr() + checkBrace(b);
    }

    /**
     * if the expression is an operation, braces are added to the string representation
     * Only used for toString() implementations
     *
     * @param expression the expression
     * @return the string representation
     */
    static String checkBrace(Expression expression) {
        if (expression instanceof Operate)
            return "(" + expression.toString() + ")";
        else
            return expression.toString();
    }
}
