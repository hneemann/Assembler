package de.neemann.assembler.parser.macros;

import de.neemann.assembler.asm.*;
import de.neemann.assembler.expression.Constant;
import de.neemann.assembler.expression.Expression;
import de.neemann.assembler.parser.Macro;
import de.neemann.assembler.parser.Parser;
import de.neemann.assembler.parser.ParserException;

import java.io.IOException;

/**
 * @author hneemann
 */
public class SCall implements Macro {
    @Override
    public String getName() {
        return "_call";
    }

    @Override
    public void parseMacro(Program p, String name, Parser parser) throws IOException, ParserException, InstructionException {
        Expression addr = parser.parseExpression();
        p.add(Instruction.make(Opcode.SUBIs, Register.SP, new Constant(1)));
        p.add(Instruction.make(Opcode.ST, Register.SP, Register.RA));
        p.add(Instruction.make(Opcode.RCALL, Register.RA, addr));
        p.add(Instruction.make(Opcode.LD, Register.RA, Register.SP));
        p.add(Instruction.make(Opcode.ADDIs, Register.SP, new Constant(1)));
    }
}
