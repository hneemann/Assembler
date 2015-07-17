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
public class Pop implements Macro {
    @Override
    public String getName() {
        return "POP";
    }

    @Override
    public void parseMacro(Program p, String name, Parser parser) throws IOException, ParserException, InstructionException, ExpressionException {
        Register r = parser.parseReg();
        p.setPendingMacroDescription(getName() + " " + r.name());
        pop(r, p);
    }

    public static void pop(Register r, Program p) throws InstructionException {
        p.add(new InstructionBuilder(Opcode.LD).setDest(r).setSource(Register.SP).build());
        p.add(new InstructionBuilder(Opcode.ADDIs).setDest(Register.SP).setConstant(new Constant(1)).build());
    }
}
