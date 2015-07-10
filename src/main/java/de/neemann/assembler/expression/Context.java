package de.neemann.assembler.expression;

import java.util.HashMap;

/**
 * @author hneemann
 */
public class Context {
    private static final String SKIP_ADDR = "_SKIP_ADDR_";
    private static final String ADDR = "_ADDR_";
    private final HashMap<String, Integer> idenifier;
    private int instrAddr;

    public Context() {
        this.idenifier = new HashMap<>();
    }

    public int get(String name) throws ExpressionException {
        Integer v = idenifier.get(name);
        if (v == null)
            throw new ExpressionException("'" + name + "' not found");
        return v;
    }

    public Context addIdentifier(String name, int value) throws ExpressionException {
        Integer v = idenifier.get(name);
        if (v != null && v != value)
            throw new ExpressionException(name + " set twice");

        return setIdentifier(name, value);
    }

    public Context setIdentifier(String name, int value) {
        idenifier.put(name, value);
        return this;
    }

    public Context setInstrAddr(int instrAddr) {
        this.instrAddr = instrAddr;
        return setIdentifier(ADDR, instrAddr);
    }

    public Context setSkipAddr(int addr) {
        return setIdentifier(SKIP_ADDR, addr);
    }

    public int getInstrAddr() {
        return instrAddr;
    }
}
