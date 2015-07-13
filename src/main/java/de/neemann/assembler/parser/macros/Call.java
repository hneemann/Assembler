package de.neemann.assembler.parser.macros;

import de.neemann.assembler.asm.*;
import de.neemann.assembler.expression.*;
import de.neemann.assembler.parser.Macro;
import de.neemann.assembler.parser.Parser;
import de.neemann.assembler.parser.ParserException;

import java.io.IOException;

/**
 * @author hneemann
 */
public class Call implements Macro {
    @Override
    public String getName() {
        return "CALL";
    }

    @Override
    public void parseMacro(Program p, String name, Parser parser) throws IOException, ParserException, InstructionException, ExpressionException {
        Expression addr = parser.parseExpression();
        p.setPendingMacroDescription(getName() + " " + addr);
        p.add(Instruction.make(Opcode.SUBIs, Register.SP, new Constant(1)));
        p.add(Instruction.make(Opcode.LDI, Register.RA, new Identifier(Context.SKIP2_ADDR)));
        p.add(Instruction.make(Opcode.ST, Register.SP, Register.RA));
        p.add(Instruction.make(Opcode.JMP, addr));
    }
}
