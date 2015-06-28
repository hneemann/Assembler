package de.neemann.assembler.asm.formatter;

import de.neemann.assembler.asm.Instruction;
import de.neemann.assembler.expression.Context;
import de.neemann.assembler.expression.ExpressionException;

import java.io.PrintStream;

/**
 * @author hneemann
 */
public class HexFormatter implements Formatter {

    private final PrintStream out;

    public HexFormatter(PrintStream out) {
        this.out = out;
        out.println("v2.0 raw");
    }

    @Override
    public void format(Instruction in, Context context) throws ExpressionException {
        in.createMachineCode(context, new MachineCodeListener() {
            @Override
            public void add(int code) {
                out.println(Integer.toHexString(code));
            }
        });
    }
}
