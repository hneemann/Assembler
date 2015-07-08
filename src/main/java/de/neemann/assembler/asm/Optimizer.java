package de.neemann.assembler.asm;

import de.neemann.assembler.expression.Context;
import de.neemann.assembler.expression.ExpressionException;

/**
 * @author hneemann
 */
public class Optimizer implements InstructionVisitor {
    private final Program program;

    public Optimizer(Program program) {
        this.program = program;
    }

    @Override
    public void visit(Instruction instruction, Context context) throws ExpressionException {
        switch (instruction.getOpcode()) {
            case OUT:
                int addr = instruction.getConstant().getValue(context);
                if (addr >= 0 && addr <= 31)
                    instruction.setOpcode(Opcode.OUTS);
                break;
            case CPI:
                int con = instruction.getConstant().getValue(context);
                switch (con) {
                    case 0:
                        instruction.setOpcode(Opcode.CP0);
                        break;
                    case 1:
                        instruction.setOpcode(Opcode.CP1);
                        break;
                    case 2:
                        instruction.setOpcode(Opcode.CP2);
                        break;
                }
                break;
        }
    }
}
