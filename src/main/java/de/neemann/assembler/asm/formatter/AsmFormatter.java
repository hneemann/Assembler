package de.neemann.assembler.asm.formatter;

import de.neemann.assembler.asm.Instruction;
import de.neemann.assembler.asm.Opcode;
import de.neemann.assembler.expression.Context;
import de.neemann.assembler.expression.ExpressionException;

import java.io.PrintStream;

/**
 * @author hneemann
 */
public class AsmFormatter implements Formatter {
    private final PrintStream out;

    public AsmFormatter(PrintStream out) {
        this.out = out;
    }

    @Override
    public void format(Instruction i, Context context) throws ExpressionException {
        if (i.getLineNumber() > 0) {
            form(Integer.toString(i.getLineNumber()), 3);
        } else {
            form("", 3);
        }
        out.print(" | ");

        outHex(context.getAddr());

        out.print(": ");

        i.createMachineCode(context, new MachineCodeListener() {
            @Override
            public void add(int instr) {
                outHex(instr);
                out.print(" ");
            }
        });
        if (i.size() == 1)
            out.print("     ");

        if (i.getLabel() == null)
            form("", 12);
        else
            form(i.getLabel() + ":", 12);

        Opcode opcode = i.getOpcode();
        out.print(opcode.name());

        switch (opcode.getRegsNeeded()) {
            case source:
                out.print(" R");
                out.print((i.getSourceReg()));
                break;
            case dest:
                out.print(" R");
                out.print((i.getDestReg()));
                break;
            case both:
                out.print(" R");
                out.print((i.getDestReg()));
                out.print(", R");
                out.print((i.getSourceReg()));
                break;
        }

        if (opcode.getImmedNeeded() == Opcode.ImmedNeeded.Yes) {
            if (opcode.getRegsNeeded() != Opcode.RegsNeeded.none)
                out.print(",");
            out.append(' ').append(i.getConstant().toString());
        }
        out.print('\n');
    }

    private void outHex(int instr) {
        String s = Integer.toHexString(instr);
        while (s.length() < 4) s = '0' + s;
        out.print(s);
    }

    private void form(String text, int cols) {
        out.print(text);
        for (int i = text.length(); i < cols; i++)
            out.print(' ');
    }
}
