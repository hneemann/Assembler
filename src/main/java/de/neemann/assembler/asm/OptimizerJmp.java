package de.neemann.assembler.asm;

import de.neemann.assembler.expression.Context;
import de.neemann.assembler.expression.ExpressionException;

/**
 * @author hneemann
 */
public class OptimizerJmp implements InstructionVisitor {

    private boolean optimized = false;

    @Override
    public void visit(Instruction instruction, Context context) throws ExpressionException {
        Opcode op = instruction.getOpcode();
        if (op.equals(Opcode.JMP)) {
            int con = instruction.getConstant().getValue(context);
            int ofs = con - context.getInstrAddr() - 1;
            if (ofs <= 255 && ofs >= -256) {
                instruction.setOpcode(Opcode.JMPs);
                optimized = true;
            }
        }
    }

    public boolean wasOptimized() {
        return optimized;
    }
}
