package de.neemann.assembler.parser.macros;

import de.neemann.assembler.asm.*;
import de.neemann.assembler.expression.Constant;
import de.neemann.assembler.expression.Expression;
import de.neemann.assembler.parser.Macro;
import de.neemann.assembler.parser.Parser;
import de.neemann.assembler.parser.ParserException;

import java.io.IOException;

import static de.neemann.assembler.parser.macros.Push.push;

/**
 * @author hneemann
 */
public class Enter implements Macro {
    @Override
    public String getName() {
        return "_enter";
    }

    @Override
    public void parseMacro(Program p, String name, Parser parser) throws IOException, ParserException, InstructionException {
        Expression size = parser.parseExpression();
        push(Register.RA, p);
        push(Register.BP, p);
        p.add(Instruction.make(Opcode.MOV, Register.BP, Register.SP));


        boolean skipStackFrame = (size instanceof Constant && ((Constant) size).getValue(null) == 0);
        if (!skipStackFrame)
            p.add(Instruction.make(Opcode.SUBI, Register.SP, size));
    }
}
