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
        return "_POP";
    }

    @Override
    public void parseMacro(Program p, String name, Parser parser) throws IOException, ParserException, InstructionException, ExpressionException {
        Register r = parser.parseReg();
        p.setPendingMacroDescription(getName() + " " + r.name());
        pop(r, p);
    }

    public static void pop(Register r, Program p) throws InstructionException {
        p.add(Instruction.make(Opcode.LD, r, Register.SP));
        p.add(Instruction.make(Opcode.ADDIs, Register.SP, new Constant(1)));
    }
}
