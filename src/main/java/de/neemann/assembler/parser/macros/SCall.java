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
public class SCall extends Macro {

    /**
     * Creates a new instance
     */
    public SCall() {
        super("_SCALL", MnemonicArguments.CONST, "jumps to the address given in const and stores the return address in the register RA. Before that RA ist pushed to the stack, and after the return RA is poped of the stack again.");
    }

    @Override
    public void parseMacro(Program p, String name, Parser parser) throws IOException, ParserException, InstructionException, ExpressionException {
        Expression addr = parser.parseExpression();
        p.setPendingMacroDescription(getName() + " " + addr);
        push(Register.RA, p);
        p.add(new InstructionBuilder(Opcode.RCALL).setDest(Register.RA).setConstant(addr).build());
        pop(Register.RA, p);
    }
}
