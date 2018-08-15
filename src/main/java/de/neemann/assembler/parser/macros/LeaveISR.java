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
public class LeaveISR extends Macro {

    /**
     * Creates a new instance
     */
    public LeaveISR() {
        super("LEAVEI", MnemonicArguments.NOTHING, "pops R0 and the flags from the stack");
    }

    @Override
    public void parseMacro(Program p, String name, Parser parser) throws IOException, ParserException, InstructionException, ExpressionException {
        p.setPendingMacroDescription(getName());
        p.add(new InstructionBuilder(Opcode.ADDIs).setDest(Register.SP).setConstant(2).build());
        p.add(new InstructionBuilder(Opcode.LDD).setDest(Register.R0).setSource(Register.SP).setConstant(-2).build());
        p.add(new InstructionBuilder(Opcode.OUT).setSource(Register.R0).setConstant(0).build());
        p.add(new InstructionBuilder(Opcode.LDD).setDest(Register.R0).setSource(Register.SP).setConstant(-1).build());
    }
}
