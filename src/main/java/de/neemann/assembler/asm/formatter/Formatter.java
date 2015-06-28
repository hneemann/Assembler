package de.neemann.assembler.asm.formatter;

import de.neemann.assembler.asm.Instruction;
import de.neemann.assembler.expression.Context;
import de.neemann.assembler.expression.ExpressionException;

/**
 * @author hneemann
 */
public interface Formatter {

    void format(Instruction instruction, Context context) throws ExpressionException;

}
