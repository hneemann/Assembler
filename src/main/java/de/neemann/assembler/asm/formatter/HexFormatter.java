package de.neemann.assembler.asm.formatter;

import de.neemann.assembler.asm.InstructionInterface;
import de.neemann.assembler.asm.InstructionVisitor;
import de.neemann.assembler.asm.MachineCodeListener;
import de.neemann.assembler.expression.Context;
import de.neemann.assembler.expression.ExpressionException;

import java.io.PrintStream;

/**
 * @author hneemann
 */
public class HexFormatter implements InstructionVisitor {

    private final PrintStream out;
    private int addr = 0;

    /**
     * Creates a new instance
     *
     * @param out the stream to write to
     */
    public HexFormatter(PrintStream out) {
        this.out = out;
        out.print("v2.0 raw");
        out.print('\n');
    }

    @Override
    public void visit(InstructionInterface in, Context context) throws ExpressionException {
        final int instrAddr = context.getInstrAddr();
        if (instrAddr < addr)
            throw new ExpressionException("invalid hex addr!");
        while (instrAddr > addr) {
            out.print("0");
            out.print('\n');
            addr++;
        }

        in.createMachineCode(context, new MachineCodeListener() {
            @Override
            public void add(int code) {
                out.print(Integer.toHexString(code));
                out.print('\n');
                addr++;
            }
        });
    }
}
