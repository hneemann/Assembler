package de.neemann.assembler.parser.macros;

import de.neemann.assembler.asm.*;
import de.neemann.assembler.expression.ExpressionException;
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
        return "PUSH";
    }

    @Override
    public void parseMacro(Program p, String name, Parser parser) throws IOException, ParserException, InstructionException, ExpressionException {
        Register r = parser.parseReg();
        p.setPendingMacroDescription(getName() + " " + r.name());
        push(r, p);
    }

    public static void push(Register r, Program p) throws InstructionException {
        p.add(new InstructionBuilder(Opcode.SUBIs).setDest(Register.SP).setConstant(1).build());
        p.add(new InstructionBuilder(Opcode.ST).setDest(Register.SP).setSource(r).build());
    }
}
