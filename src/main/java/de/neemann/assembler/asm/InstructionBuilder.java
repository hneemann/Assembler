package de.neemann.assembler.asm;

import de.neemann.assembler.expression.Constant;
import de.neemann.assembler.expression.Expression;
import de.neemann.assembler.expression.Neg;

/**
 * @author hneemann
 */
public class InstructionBuilder {

    private final Opcode opcode;
    private Register source;
    private Register dest;
    private Expression constant;

    public InstructionBuilder(Opcode opcode) {
        this.opcode = opcode;
    }

    public InstructionBuilder setSource(Register source) throws InstructionException {
        if (!opcode.getArguments().hasSource())
            throw new InstructionException(opcode.name() + " needs no source register!");
        if (this.source != null)
            throw new InstructionException(opcode.name() + " source set twice!");

        this.source = source;
        return this;
    }

    public InstructionBuilder setDest(Register dest) throws InstructionException {
        if (!opcode.getArguments().hasDest())
            throw new InstructionException(opcode.name() + " needs no designation register!");
        if (this.dest != null)
            throw new InstructionException(opcode.name() + " destignation set twice!");

        this.dest = dest;
        return this;
    }

    public InstructionBuilder setConstant(int value) throws InstructionException {
        return setConstant(new Constant(value));
    }

    public InstructionBuilder setConstant(Expression constant) throws InstructionException {
        if (!opcode.getArguments().hasConst())
            throw new InstructionException(opcode.name() + " needs no constant!");
        if (this.constant != null)
            throw new InstructionException(opcode.name() + " constant set twice!");

        this.constant = constant;
        return this;
    }

    public InstructionBuilder negConstant() {
        constant = new Neg(constant);
        return this;
    }

    public Instruction build() throws InstructionException {
        if (opcode.getArguments().hasSource() && source == null)
            throw new InstructionException(opcode.name() + " needs a source register!");
        if (opcode.getArguments().hasDest() && dest == null)
            throw new InstructionException(opcode.name() + " needs a designation register!");
        if (opcode.getArguments().hasConst() && constant == null)
            throw new InstructionException(opcode.name() + " needs a constant!");

        if (dest == null) dest = Register.R0;
        if (source == null) source = Register.R0;

        return new Instruction(opcode, dest, source, constant);
    }

}
