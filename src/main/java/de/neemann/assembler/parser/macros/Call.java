package de.neemann.assembler.parser.macros;

import de.neemann.assembler.asm.*;
import de.neemann.assembler.expression.Context;
import de.neemann.assembler.expression.Expression;
import de.neemann.assembler.expression.ExpressionException;
import de.neemann.assembler.expression.Identifier;
import de.neemann.assembler.parser.Macro;
import de.neemann.assembler.parser.Parser;
import de.neemann.assembler.parser.ParserException;

import java.io.IOException;

/**
 * @author hneemann
 */
public class Call implements Macro {
    @Override
    public String getName() {
        return "CALL";
    }

    @Override
    public void parseMacro(Program p, String name, Parser parser) throws IOException, ParserException, InstructionException, ExpressionException {
        Expression addr = parser.parseExpression();
        p.setPendingMacroDescription(getName() + " " + addr);
        p.add(new InstructionBuilder(Opcode.SUBIs).setDest(Register.SP).setConstant(1).build());
        p.add(new InstructionBuilder(Opcode.LDI).setDest(Register.RA).setConstant(new Identifier(Context.SKIP2_ADDR)).build());
        p.add(new InstructionBuilder(Opcode.ST).setDest(Register.SP).setSource(Register.RA).build());
        p.add(new InstructionBuilder(Opcode.JMP).setConstant(addr).build());
    }
}
