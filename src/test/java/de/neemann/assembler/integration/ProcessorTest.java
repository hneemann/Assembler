package de.neemann.assembler.integration;

import de.neemann.assembler.asm.InstructionException;
import de.neemann.assembler.asm.Program;
import de.neemann.assembler.expression.ExpressionException;
import de.neemann.assembler.parser.Parser;
import de.neemann.assembler.parser.ParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class ProcessorTest {
    private final StringBuilder signalInit;
    private final String label;
    private final ArrayList<Check> checks;
    private ArrayList<Integer> code;
    private String source;
    private int instrAddr;
    private int cycles;

    public ProcessorTest(String label) {
        this.label = label;
        signalInit = new StringBuilder();
        checks = new ArrayList<>();
    }

    public String getLabel() {
        return label;
    }

    public ProcessorTest setRegister(String reg, int val) {
        signalInit.append("init ").append(reg).append("=").append(val).append(";\n");
        return this;
    }

    public ProcessorTest setMemory(int addr, long value) {
        return setMemory("mem", addr, value);
    }

    public ProcessorTest setMemory(String name, int addr, long value) {
        signalInit.append("memory ").append(name).append("(").append(addr).append(")=").append(value).append(";\n");
        return this;
    }

    public ProcessorTest setCarry(boolean c) {
        signalInit.append("init Carry=").append(c ? 1 : 0).append(";\n");
        return this;
    }

    public ProcessorTest run(String code) throws ParserException, IOException, ExpressionException, InstructionException {
        this.source = code;
        this.code = makeHex(code);
        setCycles(this.code.size());
        return this;
    }

    public ProcessorTest run(String code, int instructions) throws ParserException, IOException, ExpressionException, InstructionException {
        this.source = code;
        this.code = makeHex(code);
        if (this.code.size() != instructions)
            throw new RuntimeException("wrong code size: expected " + instructions + ", but was " + this.code.size());
        setCycles(instructions);
        return this;
    }

    public ProcessorTest setCycles(int cycles) {
        this.cycles = cycles;
        return this;
    }

    private ArrayList<Integer> makeHex(String code) throws InstructionException, ExpressionException, IOException, ParserException {
        Program p = new Parser(code).parseProgram().optimizeAndLink();
        ArrayList<Integer> c = new ArrayList<>();
        p.traverse((in, context) -> {
            instrAddr = context.getInstrAddr();
            in.createMachineCode(context, e -> {
                while (c.size() <= instrAddr)
                    c.add(0);
                c.set(instrAddr, e);
                instrAddr++;
            });
            return true;
        });
        return c;
    }

    public ProcessorTest checkRegister(String reg, int value) {
        check(new Check(reg, value));
        return this;
    }

    public ProcessorTest checkCarry(boolean carry) {
        check(new Check("Carry", carry ? 1 : 0));
        return this;
    }

    private void check(Check check) {
        if (checks.contains(check))
            throw new RuntimeException("double check of " + check);
        checks.add(check);
    }

    public String getCode() {
        StringBuilder c = new StringBuilder("# auto generated, do not modify\nClk");
        for (Check ch : checks)
            c.append(" ").append(ch.name);

        c.append("\n\n");
        c.append(signalInit);
        c.append("\n# ");
        c.append(source.trim().replace("\n", "\n# "));

        c.append("\nprogram(");
        for (int i = 0; i < code.size(); i++) {
            if (i > 0)
                c.append(",");
            c.append("0x").append(Integer.toHexString(code.get(i)));
        }
        c.append(")\n\n");

        if (cycles > 1)
            c.append("repeat (").append(cycles).append(") ");

        c.append("C");
        for (Check ignored : checks)
            c.append(" X");
        c.append("\n\n");

        c.append("# expects\n");
        for (Check t : checks)
            c.append("# ").append(t).append("\n");
        c.append("0");
        for (Check t : checks)
            c.append(" ").append(t.value);
        c.append("\n");

        return c.toString();
    }

    private static final class Check {
        private final String name;
        private final int value;

        public Check(String name, int value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Check check = (Check) o;
            return name.equals(check.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return name + "=" + value;
        }
    }
}
