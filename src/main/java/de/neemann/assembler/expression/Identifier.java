package de.neemann.assembler.expression;

/**
 * @author hneemann
 */
public class Identifier implements Expression {
    private final String name;

    public Identifier(String name) {
        this.name = name;
    }

    @Override
    public int getValue(Context context) throws ExpressionException {
        return context.get(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
