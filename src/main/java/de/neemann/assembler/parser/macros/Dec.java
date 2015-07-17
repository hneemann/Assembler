package de.neemann.assembler.parser.macros;

import de.neemann.assembler.asm.*;
import de.neemann.assembler.expression.Constant;
import de.neemann.assembler.expression.ExpressionException;
import de.neemann.assembler.parser.Macro;
import de.neemann.assembler.parser.Parser;
import de.neemann.assembler.parser.ParserException;

import java.io.IOException;

/**
 * @author hneemann
 */
public class Dec implements Macro {
    @Override
    public String getName() {
        return "DEC";
    }

    @Override
    public void parseMacro(Program p, String name, Parser parser) throws IOException, ParserException, InstructionException, ExpressionException {
        Register r = parser.parseReg();
        p.setPendingMacroDescription(getName() + " " + r.name());
        p.add(Instruction.make(Opcode.SUBIs, r, new Constant(1)));
    }
}
