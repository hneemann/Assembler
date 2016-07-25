package de.neemann.assembler.expression;

/**
 * @author hneemann
 */
public class Constant implements Expression {

    private final Object value;

    /**
     * Creates a new instance
     *
     * @param value the constant value
     */
    public Constant(int value) {
        this((Integer) value);
    }

    /**
     * Creates a new instance
     *
     * @param value the constant value
     */
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
            return "'" + escape(value.toString()) + "'";
    }

    private String escape(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\'':
                    sb.append("\\'");
                    break;
                case '\"':
                    sb.append("\"");
                    break;
                case '\\':
                    sb.append("\\");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }
}
