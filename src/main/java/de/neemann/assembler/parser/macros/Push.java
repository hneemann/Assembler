package de.neemann.assembler.parser.macros;

import de.neemann.assembler.asm.*;
import de.neemann.assembler.parser.Macro;
import de.neemann.assembler.parser.Parser;
import de.neemann.assembler.parser.ParserException;

import java.io.IOException;

/**
 * @author hneemann
 */
public class Push implements Macro {
    @Override
    public String getName() {
        return "push";
    }

    @Override
    public void parseMacro(Program p, String name, Parser parser) throws IOException, ParserException, InstructionException {
        Register r = parser.parseReg();
        p.add(Instruction.make(Opcode.DEC, Register.SP));
        p.add(Instruction.make(Opcode.ST, Register.SP, r));
    }
}
