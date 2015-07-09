package de.neemann.assembler.asm.formatter;

import de.neemann.assembler.asm.Instruction;
import de.neemann.assembler.asm.MachineCodeListener;
import de.neemann.assembler.asm.Opcode;
import de.neemann.assembler.expression.Context;
import de.neemann.assembler.expression.Expression;
import de.neemann.assembler.expression.ExpressionException;

import java.io.PrintStream;

/**
 * @author hneemann
 */
public class AsmLightFormatter extends AsmFormatter {

    public AsmLightFormatter(PrintStream out) {
        super(out);
    }

    @Override
    public void visit(Instruction i, Context context) throws ExpressionException {

        if (i.getLabel() != null) {
            tab(18);
            print(i.getLabel() + ":");
            newLine();
        }


        printHex(context.getInstrAddr());

        print(": ");

        i.createMachineCode(context, new MachineCodeListener() {
            @Override
            public void add(int instr) {
                printHex(instr);
                print(" ");
            }
        });

        tab(28);

        Opcode opcode = i.getOpcode();
        print(opcode.name());

        tab(34);

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
            tab(51);
            print("; 0x");
            print(Integer.toHexString(constant.getValue(context) & 0xffff));
        }
        newLine();
    }
}
