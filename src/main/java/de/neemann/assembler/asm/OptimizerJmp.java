package de.neemann.assembler.asm;

import de.neemann.assembler.expression.Context;
import de.neemann.assembler.expression.ExpressionException;

/**
 * Tries to replace a long jmp by a short jmp if target is near enough.
 *
 * @author hneemann
 */
public class OptimizerJmp implements InstructionVisitor {

    private boolean optimized = false;

    @Override
    public void visit(InstructionInterface instructionInterface, Context context) throws ExpressionException {
        if (instructionInterface instanceof Instruction) {
            Instruction instruction = (Instruction) instructionInterface;
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
    }

    /**
     * @return true if there was an optimisation possible
     */
    public boolean wasOptimized() {
        return optimized;
    }
}
