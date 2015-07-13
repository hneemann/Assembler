package de.neemann.assembler.parser.macros;

import de.neemann.assembler.asm.*;
import de.neemann.assembler.expression.Constant;
import de.neemann.assembler.expression.Expression;
import de.neemann.assembler.expression.ExpressionException;
import de.neemann.assembler.expression.Operate;
import de.neemann.assembler.parser.Macro;
import de.neemann.assembler.parser.Parser;
import de.neemann.assembler.parser.ParserException;

import java.io.IOException;

import static de.neemann.assembler.parser.macros.Pop.pop;

/**
 * @author hneemann
 */
public class Ret implements Macro {
    @Override
    public String getName() {
        return "RET";
    }

    @Override
    public void parseMacro(Program p, String name, Parser parser) throws IOException, ParserException, InstructionException, ExpressionException {
        if (parser.isEOL()) {
            p.setPendingMacroDescription(getName());
            pop(Register.RA, p);
        } else {
            Expression size = parser.parseExpression();
            p.setPendingMacroDescription(getName() + " " + size);
            p.add(Instruction.make(Opcode.LD, Register.RA, Register.SP));
            p.add(Instruction.make(Opcode.ADDI, Register.SP, new Operate(size, Operate.Operation.ADD, new Constant(1))));
        }
        p.add(Instruction.make(Opcode.RRET, Register.RA));
    }
}
