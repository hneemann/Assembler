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
public class Ret extends Macro {
    public Ret() {
        super("RET", MnemonicArguments.CONST, "jumps to the address which is stored on top of the stack. decreases the stack pointer by 1+const. const is optional");
    }

    @Override
    public void parseMacro(Program p, String name, Parser parser) throws IOException, ParserException, InstructionException, ExpressionException {
        if (parser.isEOL()) {
            p.setPendingMacroDescription(getName());
            pop(Register.RA, p);
        } else {
            Expression size = parser.parseExpression();
            p.setPendingMacroDescription(getName() + " " + size);
            p.add(new InstructionBuilder(Opcode.LD).setDest(Register.RA).setSource(Register.SP).build());
            p.add(new InstructionBuilder(Opcode.ADDI).setDest(Register.SP).setConstant(new Operate(size, Operate.Operation.ADD, new Constant(1))).build());
        }
        p.add(new InstructionBuilder(Opcode.RRET).setSource(Register.RA).build());
    }
}
