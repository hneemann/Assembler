package de.neemann.assembler.asm;

import de.neemann.assembler.asm.formatter.AsmFormatter;
import de.neemann.assembler.asm.formatter.Formatter;
import de.neemann.assembler.asm.formatter.HexFormatter;
import de.neemann.assembler.expression.*;

import java.io.IOException;
import java.io.PrintStream;
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

    public Program add(Opcode opcode, int dest, int source, Expression expr) throws InstructionException {
        if ((opcode.getImmedNeeded() == Opcode.ImmedNeeded.No) && (expr != null))
            throw new InstructionException(opcode.name() + " does not need a constant");
        if ((opcode.getImmedNeeded() == Opcode.ImmedNeeded.Yes) && (expr == null))
            throw new InstructionException(opcode.name() + " needs a constant");

        Instruction i = new Instruction(opcode, dest, source, expr);
        if (labelPending != null) {
            i.setLabel(labelPending);
            labelPending = null;
        }
        prog.add(i);
        return this;
    }

    public Program add(Opcode opcode, int reg) throws InstructionException {
        switch (opcode.getRegsNeeded()) {
            case source:
                return add(opcode, 0, reg, null);
            case dest:
                return add(opcode, reg, 0, null);
            case none:
                throw new InstructionException(opcode.name() + " does not need a register");
            default:
                throw new InstructionException(opcode.name() + " needs both registers");
        }
    }

    public Program add(Opcode opcode, int dest, int source) throws InstructionException {
        if (opcode.getRegsNeeded() != Opcode.RegsNeeded.both)
            throw new InstructionException(opcode.name() + " needs both registers");

        return add(opcode, dest, source, null);
    }


    public Program add(Opcode opcode, int reg, Expression expr) throws InstructionException {
        switch (opcode.getRegsNeeded()) {
            case source:
                return add(opcode, 0, reg, expr);
            case dest:
                return add(opcode, reg, 0, expr);
            case none:
                throw new InstructionException(opcode.name() + " does not need a register");
            default:
                throw new InstructionException(opcode.name() + " needs both registers");
        }
    }

    public Program add(Opcode opcode, Expression expr) throws InstructionException {
        if (opcode.getRegsNeeded() != Opcode.RegsNeeded.none)
            throw new InstructionException(opcode.name() + " does not need a register");

        return add(opcode, 0, 0, expr);
    }

    public Program label(String label) {
        labelPending = label;
        return this;
    }

    public Program format(Formatter formatter) throws ExpressionException {
        int addr = 0;
        for (Instruction in : prog) {
            context.setAddr(addr);
            formatter.format(in, context);
            addr += in.size();
        }
        return this;
    }

    public Program link() throws ExpressionException {
        context.clear();

        int addr = 0;
        for (Instruction i : prog) {
            if (i.getLabel() != null) {
                context.addIdentifier(i.getLabel(), addr);
            }
            addr += i.size();
        }

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
        try (PrintStream hexOut = new PrintStream("/home/hneemann/Dokumente/DHBW/Technische_Informatik_I/Vorlesung/06_Prozessoren/java/assembler3/z.asm.hex")) {
            new Program()
                    .add(Opcode.LDI, 0, new Constant(1000))
                    .add(Opcode.LJMP, new Identifier("SUB"))
                    .add(Opcode.LDI, 0, new Constant(0))
                    .label("END").add(Opcode.JMP, new Identifier("END"))
                    .label("SUB").add(Opcode.STS, 0, new Constant(1))
                    .add(Opcode.RET, 1)

                    .link()
                    .format(new HexFormatter(hexOut))
                    .format(new AsmFormatter(System.out));
        }
    }


}
