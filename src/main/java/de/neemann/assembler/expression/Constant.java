package de.neemann.assembler.expression;

/**
 * @author hneemann
 */
public class Constant implements Expression {

    private final Object value;

    public Constant(int value) {
        this((Integer) value);
    }

    public Constant(char value) {
        this((Character) value);
    }

    private Constant(Object value) {
        this.value = value;
    }

    @Override
    public int getValue(Context context) {
        if (value instanceof Integer)
            return (Integer) value;
        else
            return (Character) value;
    }

    @Override
    public String toString() {
        if (value instanceof Integer)
            return Integer.toString((Integer) value);
        else
            return "'" + value + "'";
    }
}
