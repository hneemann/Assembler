package de.neemann.assembler.parser.macros;

import de.neemann.assembler.asm.*;
import de.neemann.assembler.expression.Expression;
import de.neemann.assembler.expression.ExpressionException;
import de.neemann.assembler.parser.Macro;
import de.neemann.assembler.parser.Parser;
import de.neemann.assembler.parser.ParserException;

import java.io.IOException;

import static de.neemann.assembler.parser.macros.Pop.pop;
import static de.neemann.assembler.parser.macros.Push.push;

/**
 * @author hneemann
 */
public class SCall implements Macro {
    @Override
    public String getName() {
        return "_SCALL";
    }

    @Override
    public void parseMacro(Program p, String name, Parser parser) throws IOException, ParserException, InstructionException, ExpressionException {
        Expression addr = parser.parseExpression();
        p.setPendingMacroDescription(getName() + " " + addr);
        push(Register.RA, p);
        p.add(Instruction.make(Opcode.RCALL, Register.RA, addr));
        pop(Register.RA, p);
    }
}
