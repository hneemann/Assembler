package de.neemann.assembler.asm;

import de.neemann.assembler.expression.Constant;
import de.neemann.assembler.expression.Context;
import de.neemann.assembler.expression.ExpressionException;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author hneemann
 */
public class Program {

    private final ArrayList<Instruction> prog;
    private final Context context;
    private int ramPos = 0;
    private TreeMap<Integer, ArrayList<Integer>> dataMap;
    private String pendingLabel;

    public Program() {
        prog = new ArrayList<>();
        context = new Context();
        dataMap = new TreeMap<>();
    }

    public Program add(Instruction i) {
        if (pendingLabel != null) {
            i.setLabel(pendingLabel);
            pendingLabel = null;
        }
        prog.add(i);
        return this;
    }

    public Program traverse(InstructionVisitor instructionVisitor) throws ExpressionException {
        int addr = 0;
        for (int i = 0, progSize = prog.size(); i < progSize; i++) {
            Instruction in = prog.get(i);
            try {
                context.setInstrAddr(addr);
                context.setSkipAddr(calcSkipAddr(addr, i));
                instructionVisitor.visit(in, context);
                addr += in.size();
            } catch (ExpressionException e) {
                e.setLineNumber(in.getLineNumber());
                throw e;
            }
        }
        return this;
    }

    private int calcSkipAddr(int addr, int i) {
        if (i < prog.size() - 2)
            return addr + prog.get(i).size() + prog.get(i + 1).size();
        else
            return addr;
    }

    public Program appendData() throws InstructionException {
        int p = 0;
        for (Map.Entry<Integer, ArrayList<Integer>> e : dataMap.entrySet()) {
            int value = e.getKey();
            prog.add(p++, Instruction.make(Opcode.LDI, Register.R0, new Constant(value)));
            for (int addr : e.getValue()) {
                prog.add(p++, Instruction.make(Opcode.STS, Register.R0, new Constant(addr)));
            }
        }
        return this;
    }

    public Program link() throws ExpressionException {
        traverse(new InstructionVisitor() {
            @Override
            public void visit(Instruction instruction, Context context) throws ExpressionException {
                if (instruction.getLabel() != null) {
                    context.addIdentifier(instruction.getLabel(), context.getInstrAddr());
                }
            }
        });
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Instruction i : prog) {
            sb.append(i.toString()).append("\n");
        }
        return sb.toString();
    }

    public int getInstructionCount() {
        return prog.size();
    }

    public Instruction getInstruction(int i) {
        return prog.get(i);
    }

    public int addRam(String ident, int size) throws ExpressionException {
        int r = ramPos;
        context.addIdentifier(ident, ramPos);
        ramPos += size;
        return r;
    }

    public Context getContext() {
        return context;
    }

    public void addData(int addr, int value) {
        ArrayList<Integer> list = dataMap.get(value);
        if (list == null) {
            list = new ArrayList<>();
            dataMap.put(value, list);
        }
        list.add(addr);
    }

    public void setPendingLabel(String pendingLabel) throws ExpressionException {
        if (this.pendingLabel != null)
            throw new ExpressionException("two labels for the same command: " + pendingLabel + ", " + this.pendingLabel);
        this.pendingLabel = pendingLabel;
    }

    public Program optimize() throws ExpressionException {
        return traverse(new Optimizer(this));
    }
}
