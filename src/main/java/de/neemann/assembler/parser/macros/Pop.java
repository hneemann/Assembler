package de.neemann.assembler.parser.macros;

import de.neemann.assembler.asm.*;
import de.neemann.assembler.expression.Constant;
import de.neemann.assembler.parser.Macro;
import de.neemann.assembler.parser.Parser;
import de.neemann.assembler.parser.ParserException;

import java.io.IOException;

/**
 * @author hneemann
 */
public class Pop implements Macro {
    @Override
    public String getName() {
        return "_pop";
    }

    @Override
    public void parseMacro(Program p, String name, Parser parser) throws IOException, ParserException, InstructionException {
        Register r = parser.parseReg();
        p.add(Instruction.make(Opcode.LD, r, Register.SP).setLineNumber(parser.getLineNumber()));
        p.add(Instruction.make(Opcode.ADDIs, Register.SP, new Constant(1)).setLineNumber(parser.getLineNumber()));
    }
}
