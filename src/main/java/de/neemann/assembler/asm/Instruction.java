package de.neemann.assembler.asm;

import de.neemann.assembler.expression.Context;
import de.neemann.assembler.expression.Expression;
import de.neemann.assembler.expression.ExpressionException;

/**
 * @author hneemann
 */
public final class Instruction {

    public static Instruction make(Opcode opcode, Register dest, Register source, Expression constant) throws InstructionException {
        if ((opcode.getImmedNeeded() == Opcode.ImmedNeeded.No) && (constant != null))
            throw new InstructionException(opcode.name() + " does not need a constant");
        if ((opcode.getImmedNeeded() == Opcode.ImmedNeeded.Yes) && (constant == null))
            throw new InstructionException(opcode.name() + " needs a constant");

        return new Instruction(opcode, dest, source, constant);
    }

    public static Instruction make(Opcode opcode, Register reg) throws InstructionException {
        switch (opcode.getRegsNeeded()) {
            case source:
                return make(opcode, Register.R0, reg, null);
            case dest:
                return make(opcode, reg, Register.R0, null);
            case none:
                throw new InstructionException(opcode.name() + " does not need a register");
            default:
                throw new InstructionException(opcode.name() + " needs both registers");
        }
    }

    public static Instruction make(Opcode opcode, Register dest, Register source) throws InstructionException {
        if (opcode.getRegsNeeded() != Opcode.RegsNeeded.both)
            throw new InstructionException(opcode.name() + " needs both registers");

        return make(opcode, dest, source, null);
    }


    public static Instruction make(Opcode opcode, Register reg, Expression constant) throws InstructionException {
        switch (opcode.getRegsNeeded()) {
            case source:
                if (opcode.getImmed() == Opcode.Immed.instrSource)
                    throw new InstructionException("soucse reg and const used");
                return make(opcode, Register.R0, reg, constant);
            case dest:
                if (opcode.getImmed() == Opcode.Immed.instrDest)
                    throw new InstructionException("dest reg and const used");
                return make(opcode, reg, Register.R0, constant);
            case none:
                throw new InstructionException(opcode.name() + " does not need a register");
            default:
                throw new InstructionException(opcode.name() + " needs both registers");
        }
    }

    public static Instruction make(Opcode opcode, Expression constant) throws InstructionException {
        if (opcode.getRegsNeeded() != Opcode.RegsNeeded.none)
            throw new InstructionException(opcode.name() + " does not need a register");

        return make(opcode, Register.R0, Register.R0, constant);
    }




    private final Register destReg;
    private final Register sourceReg;
    private final Expression constant;
    private Opcode opcode;
    private String label;
    private int lineNumber;

    private Instruction(Opcode opcode, Register destReg, Register sourceReg, Expression constant) {
        this.destReg = destReg;
        this.sourceReg = sourceReg;
        this.opcode = opcode;
        this.constant = constant;
    }

    public int size() {
        if (opcode.getImmed() == Opcode.Immed.Regist)
            return 2;
        else
            return 1;
    }

    public Instruction setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void createMachineCode(Context context, MachineCodeListener mc) throws ExpressionException {
        try {
            int con = 0;
            if (constant != null)
                con = constant.getValue(context);

            int constBit = 0;
            if (opcode.getImmed() == Opcode.Immed.Regist) {
                mc.add((con & 0x7fff) | 0x8000);
                if ((con & 0x8000) != 0)
                    constBit = 1;
            }

            switch (opcode.getImmed()) {
                case instr:
                    int ofs = con - context.getInstrAddr() - 1;
                    if (ofs > 0xff || ofs < -0x100)
                        throw new ExpressionException("branch out of range");
                    mc.add(ofs & 0x1ff
                            | (opcode.ordinal() << 9));
                    break;
                case instrDest:
                    if (con < 0 || con > 31)
                        throw new ExpressionException("constant to large");
                    mc.add(sourceReg.ordinal()
                            | (con << 4)
                            | (opcode.ordinal() << 9));
                    break;
                case instrSource:
                    if (con < 0 || con > 31)
                        throw new ExpressionException("constant to large");
                    mc.add(con & 0xf
                            | (destReg.ordinal() << 4)
                            | ((con >> 4) << 8)
                            | (opcode.ordinal() << 9));
                    break;
                default:
                    mc.add(sourceReg.ordinal()
                            | (destReg.ordinal() << 4)
                            | (constBit << 8)
                            | (opcode.ordinal() << 9));
            }
        } catch (ExpressionException e) {
            e.setLineNumber(lineNumber);
            throw e;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (label != null) {
            sb.append(label).append(":");
        }

        sb.append(opcode.name());

        switch (opcode.getRegsNeeded()) {
            case source:
                sb.append(' ').append(sourceReg);
                break;
            case dest:
                sb.append(' ').append(destReg);
                break;
            case both:
                sb.append(' ').append(destReg).append(",").append(sourceReg);
                break;
            default:
        }

        if (opcode.getImmedNeeded() == Opcode.ImmedNeeded.Yes) {
            if (opcode.getRegsNeeded() != Opcode.RegsNeeded.none)
                sb.append(",");
            sb.append(' ').append(constant.toString());
        }

        return sb.toString();
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public Opcode getOpcode() {
        return opcode;
    }

    public void setOpcode(Opcode opcode) {
        this.opcode = opcode;
    }

    public Register getSourceReg() {
        return sourceReg;
    }

    public Register getDestReg() {
        return destReg;
    }

    public Expression getConstant() {
        return constant;
    }
}
