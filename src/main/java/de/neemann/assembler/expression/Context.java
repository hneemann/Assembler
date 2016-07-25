package de.neemann.assembler.expression;

import java.util.HashMap;

/**
 * The context needed to evaluate an expression
 *
 * @author hneemann
 */
public class Context {
    /**
     * address of instruction + 2, skips one expression
     */
    public static final String SKIP_ADDR = "_SKIP_ADDR_";
    /**
     * address of instruction + 1, the next expression
     */
    public static final String NEXT_ADDR = "_NEXT_ADDR_";
    /**
     * address of instruction + 3, skips two expressions
     */
    public static final String SKIP2_ADDR = "_SKIP2_ADDR_";
    private static final String ADDR = "_ADDR_";
    private final HashMap<String, Integer> idenifier;
    private int instrAddr;

    /**
     * Creates a new context
     */
    public Context() {
        this.idenifier = new HashMap<>();
    }

    /**
     * Returns the named value
     *
     * @param name the name
     * @return the value
     * @throws ExpressionException ExpressionException
     */
    public int get(String name) throws ExpressionException {
        Integer v = idenifier.get(name);
        if (v == null)
            throw new ExpressionException("'" + name + "' not found");
        return v;
    }

    /**
     * Adds an identifier to the context.
     * You cannot overwrite an existing value.
     *
     * @param name  the name
     * @param value the value
     * @return this for chained calls
     * @throws ExpressionException ExpressionException
     */
    public Context addIdentifier(String name, int value) throws ExpressionException {
        Integer v = idenifier.get(name);
        if (v != null && v != value)
            throw new ExpressionException(name + " set twice");

        return setIdentifier(name, value);
    }

    /**
     * Sets a named value
     * Alloes you to overwrite an existing value
     *
     * @param name  name
     * @param value value
     * @return this for chained calls
     */
    public Context setIdentifier(String name, int value) {
        idenifier.put(name, value);
        return this;
    }

    /**
     * Sets the address of the actual instruction
     *
     * @param instrAddr the address
     * @return this for chained calls
     */
    public Context setInstrAddr(int instrAddr) {
        this.instrAddr = instrAddr;
        return setIdentifier(ADDR, instrAddr);
    }

    /**
     * @return the address of the actual instruction
     */
    public int getInstrAddr() {
        return instrAddr;
    }
}
