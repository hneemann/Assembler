package de.neemann.assembler.parser.macros;

import de.neemann.assembler.asm.*;
import de.neemann.assembler.expression.ExpressionException;
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
        return "LEAVE";
    }

    @Override
    public void parseMacro(Program p, String name, Parser parser) throws IOException, ParserException, InstructionException, ExpressionException {
        p.setPendingMacroDescription(getName());
        p.add(new InstructionBuilder(Opcode.MOV).setDest(Register.SP).setSource(Register.BP).build());
        pop(Register.BP, p);
    }
}
