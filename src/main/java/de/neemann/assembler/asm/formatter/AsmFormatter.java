package de.neemann.assembler.asm.formatter;

import de.neemann.assembler.asm.Instruction;
import de.neemann.assembler.asm.InstructionVisitor;
import de.neemann.assembler.asm.MachineCodeListener;
import de.neemann.assembler.asm.Opcode;
import de.neemann.assembler.expression.Context;
import de.neemann.assembler.expression.Expression;
import de.neemann.assembler.expression.ExpressionException;

import java.io.PrintStream;

/**
 * @author hneemann
 */
public class AsmFormatter implements InstructionVisitor {
    private final PrintStream o;
    private int actCol;

    public AsmFormatter(PrintStream out) {
        this.o = out;
        actCol = 0;
    }

    @Override
    public void visit(Instruction i, Context context) throws ExpressionException {
        if (i.getLineNumber() > 0) {
            print(Integer.toString(i.getLineNumber()));
        }
        tab(3);
        print(" | ");

        printHex(context.getInstrAddr());

        print(": ");

        i.createMachineCode(context, new MachineCodeListener() {
            @Override
            public void add(int instr) {
                printHex(instr);
                print(" ");
            }
        });

        tab(22);

        if (i.getLabel() != null)
            print(i.getLabel() + ":");
        tab(32);

        Opcode opcode = i.getOpcode();
        print(opcode.name());

        tab(38);

        switch (opcode.getRegsNeeded()) {
            case source:
                print(i.getSourceReg().name());
                break;
            case dest:
                print(i.getDestReg().name());
                break;
            case both:
                print(i.getDestReg().name());
                print(", ");
                print(i.getSourceReg().name());
                break;
            default:
        }

        if (opcode.getImmedNeeded() == Opcode.ImmedNeeded.Yes) {
            if (opcode.getRegsNeeded() != Opcode.RegsNeeded.none)
                print(", ");
            Expression constant = i.getConstant();
            print(constant.toString());
            tab(55);
            print("; 0x");
            print(Integer.toHexString(constant.getValue(context)));
        }
        newLine();
    }

    private void printHex(int instr) {
        String s = Integer.toHexString(instr);
        while (s.length() < 4) s = '0' + s;
        print(s);
    }

    private void tab(int col) {
        while (actCol < col)
            print(" ");
    }

    private void print(String s) {
        o.print(s);
        actCol += s.length();
    }

    private void newLine() {
        o.print('\n');
        actCol = 0;
    }
}
