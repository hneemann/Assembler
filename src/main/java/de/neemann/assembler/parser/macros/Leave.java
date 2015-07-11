package de.neemann.assembler.parser.macros;

import de.neemann.assembler.asm.*;
import de.neemann.assembler.parser.Macro;
import de.neemann.assembler.parser.Parser;
import de.neemann.assembler.parser.ParserException;

import java.io.IOException;

import static de.neemann.assembler.parser.macros.Pop.pop;

/**
 * @author hneemann
 */
public class Leave implements Macro {
    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public void parseMacro(Program p, String name, Parser parser) throws IOException, ParserException, InstructionException {
        p.add(Instruction.make(Opcode.MOV, Register.SP, Register.BP));
        pop(Register.BP, p);
        pop(Register.RA, p);
    }
}
