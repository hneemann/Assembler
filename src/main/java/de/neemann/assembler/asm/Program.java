package de.neemann.assembler.asm;

import de.neemann.assembler.expression.Context;
import de.neemann.assembler.expression.ExpressionException;

import java.util.ArrayList;

/**
 * @author hneemann
 */
public class Program {

    private final ArrayList<Instruction> prog;
    private final Context context;

    public Program() {
        prog = new ArrayList<>();
        context = new Context();
    }

    public Program add(Instruction i) {
        prog.add(i);
        return this;
    }

    public Program traverse(InstructionVisitor instructionVisitor) throws ExpressionException {
        int addr = 0;
        for (Instruction in : prog) {
            context.setInstrAddr(addr);
            instructionVisitor.visit(in, context);
            addr += in.size();
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

}
