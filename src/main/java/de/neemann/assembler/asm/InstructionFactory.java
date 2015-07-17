package de.neemann.assembler.asm;

import de.neemann.assembler.expression.Expression;

/**
 * @author hneemann
 */
public class InstructionFactory {

    private final Opcode opcode;
    private Register source = Register.R0;
    private Register dest = Register.R0;
    private Expression constant;

    public InstructionFactory(Opcode opcode) {
        this.opcode = opcode;
    }

    public void setSource(Register source) {
        this.source = source;
    }

    public void setDest(Register dest) {
        this.dest = dest;
    }

    public void setConstant(Expression constant) {
        this.constant = constant;
    }

    public Instruction make() throws InstructionException {
        return Instruction.make(opcode, dest, source, constant);
    }
}
