package de.neemann.assembler.asm;

import de.neemann.assembler.asm.formatter.AsmFormatter;
import de.neemann.assembler.expression.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author hneemann
 */
public class Program {

    private final ArrayList<Instruction> prog;
    private final Context context;
    private String labelPending;

    public Program() {
        prog = new ArrayList<>();
        context = new Context();
    }

    public Program add(Opcode opcode, Register dest, Register source, Expression constant) throws InstructionException {
        if ((opcode.getImmedNeeded() == Opcode.ImmedNeeded.No) && (constant != null))
            throw new InstructionException(opcode.name() + " does not need a constant");
        if ((opcode.getImmedNeeded() == Opcode.ImmedNeeded.Yes) && (constant == null))
            throw new InstructionException(opcode.name() + " needs a constant");

        Instruction i = new Instruction(opcode, dest, source, constant);
        if (labelPending != null) {
            i.setLabel(labelPending);
            labelPending = null;
        }
        prog.add(i);
        return this;
    }

    public Program add(Opcode opcode, Register reg) throws InstructionException {
        switch (opcode.getRegsNeeded()) {
            case source:
                return add(opcode, Register.R0, reg, null);
            case dest:
                return add(opcode, reg, Register.R0, null);
            case none:
                throw new InstructionException(opcode.name() + " does not need a register");
            default:
                throw new InstructionException(opcode.name() + " needs both registers");
        }
    }

    public Program add(Opcode opcode, Register dest, Register source) throws InstructionException {
        if (opcode.getRegsNeeded() != Opcode.RegsNeeded.both)
            throw new InstructionException(opcode.name() + " needs both registers");

        return add(opcode, dest, source, null);
    }


    public Program add(Opcode opcode, Register reg, Expression constant) throws InstructionException {
        switch (opcode.getRegsNeeded()) {
            case source:
                return add(opcode, Register.R0, reg, constant);
            case dest:
                return add(opcode, reg, Register.R0, constant);
            case none:
                throw new InstructionException(opcode.name() + " does not need a register");
            default:
                throw new InstructionException(opcode.name() + " needs both registers");
        }
    }

    public Program add(Opcode opcode, Expression expr) throws InstructionException {
        if (opcode.getRegsNeeded() != Opcode.RegsNeeded.none)
            throw new InstructionException(opcode.name() + " does not need a register");

        return add(opcode, Register.R0, Register.R0, expr);
    }

    public Program label(String label) {
        labelPending = label;
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

    public static void main(String[] args) throws IOException, ExpressionException, InstructionException {
//        try (PrintStream hexOut = new PrintStream("/home/hneemann/Dokumente/DHBW/Technische_Informatik_I/Vorlesung/06_Prozessoren/java/assembler3/z.asm.hex")) {
            new Program()
                    .add(Opcode.LDI, Register.R0, new Constant(1000))
                    .add(Opcode.CALL, Register.R1, new Identifier("SUB"))
                    .add(Opcode.LDI, Register.R0, new Constant(0))
                    .label("END").add(Opcode.JMP, new Identifier("END"))
                    .label("SUB").add(Opcode.STS, Register.R0, new Constant(1))
                    .add(Opcode.RET, Register.R1)

                    .link()
//                    .format(new HexFormatter(hexOut))
                    .traverse(new AsmFormatter(System.out));
//        }
    }


}
