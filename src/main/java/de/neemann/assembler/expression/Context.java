package de.neemann.assembler.expression;

import java.util.HashMap;

/**
 * @author hneemann
 */
public class Context {
    private final HashMap<String, Integer> idenifier;
    private int addr;

    public Context() {
        this.idenifier = new HashMap<>();
    }

    public int get(String name) throws ExpressionException {
        Integer v = idenifier.get(name);
        if (v == null)
            throw new ExpressionException(name + " not found");
        return v;
    }

    public void addIdentifier(String name, int value) throws ExpressionException {
        Integer v = idenifier.get(name);
        if (v != null && v != value)
            throw new ExpressionException(name + " set twice");

        idenifier.put(name, value);
    }

    public void clear() {
        idenifier.clear();
    }

    public Context setAddr(int addr) {
        this.addr = addr;
        return this;
    }

    public int getAddr() {
        return addr;
    }
}
