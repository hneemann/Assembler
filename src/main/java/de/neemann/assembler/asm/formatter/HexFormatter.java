package de.neemann.assembler.asm.formatter;

import de.neemann.assembler.asm.Instruction;
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

    /**
     * Creates a new instance
     *
     * @param out the stream to write to
     */
    public HexFormatter(PrintStream out) {
        this.out = out;
        out.println("v2.0 raw");
    }

    @Override
    public void visit(Instruction in, Context context) throws ExpressionException {
        in.createMachineCode(context, new MachineCodeListener() {
            @Override
            public void add(int code) {
                out.println(Integer.toHexString(code));
            }
        });
    }
}
