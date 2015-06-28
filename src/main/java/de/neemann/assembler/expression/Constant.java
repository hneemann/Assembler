package de.neemann.assembler.expression;

/**
 * @author hneemann
 */
public class Constant implements Expression {

    private final int value;

    public Constant(int value) {
        this.value = value;
    }

    @Override
    public int getValue(Context context) {
        return value;
    }

    @Override
    public String toString() {
        return "0x" + Integer.toHexString(value);
    }
}
