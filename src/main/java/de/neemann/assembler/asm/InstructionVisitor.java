package de.neemann.assembler.asm;

import de.neemann.assembler.expression.Context;
import de.neemann.assembler.expression.ExpressionException;

/**
 * @author hneemann
 */
public interface InstructionVisitor {

    void visit(Instruction instruction, Context context) throws ExpressionException;

}
