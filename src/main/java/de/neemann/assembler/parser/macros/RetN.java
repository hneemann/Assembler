package de.neemann.assembler.parser.macros;

import de.neemann.assembler.asm.*;
import de.neemann.assembler.expression.Expression;
import de.neemann.assembler.expression.ExpressionException;
import de.neemann.assembler.parser.Macro;
import de.neemann.assembler.parser.Parser;
import de.neemann.assembler.parser.ParserException;

import java.io.IOException;

/**
 * @author hneemann
 */
public class RetN implements Macro {
    @Override
    public String getName() {
        return "_retn";
    }

    @Override
    public void parseMacro(Program p, String name, Parser parser) throws IOException, ParserException, InstructionException, ExpressionException {
        Expression size = parser.parseExpression();
        p.setPendingMacroDescription(getName() + " " + size);
        p.add(Instruction.make(Opcode.ADDI, Register.SP, size));
        p.add(Instruction.make(Opcode.RRET, Register.RA));
    }
}
