package de.neemann.assembler.asm;

import de.neemann.assembler.expression.Context;
import de.neemann.assembler.expression.Expression;
import de.neemann.assembler.expression.ExpressionException;

/**
 * @author hneemann
 */
public final class Instruction implements InstructionInterface {
    private final Register destReg;
    private final Register sourceReg;
    private final Expression constant;
    private Opcode opcode;
    private String label;
    private String macroDescription;
    private String comment;
    private int lineNumber;
    private int absAddr = -1;

    Instruction(Opcode opcode, Register destReg, Register sourceReg, Expression constant) {
        this.destReg = destReg;
        this.sourceReg = sourceReg;
        this.opcode = opcode;
        this.constant = constant;
    }

    /**
     * @return the size of the instruction in words
     */
    public int size() {
        if (opcode.getALUBSel() == Opcode.ALUBSel.ImReg)
            return 2;
        else
            return 1;
    }

    /**
     * Sets the line number of this instruction
     *
     * @param lineNumber the line number
     * @return this for chained calls
     */
    public Instruction setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
        return this;
    }

    /**
     * @return the label of this instruction
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label of this instruction.
     * A instruction can only have a single label
     *
     * @param label the label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the description of the macro
     */
    public String getMacroDescription() {
        return macroDescription;
    }

    /**
     * Sets the macroDescription
     *
     * @param macroDescription the macro description
     */
    public void setMacroDescription(String macroDescription) {
        this.macroDescription = macroDescription;
    }

    /**
     * @return the comment of this instruction
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the comment of this instruction
     *
     * @param comment the comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Creates the machine code for this instruction
     *
     * @param context the context used to resolve labels
     * @param mc      the listener to sen the machine code words to
     * @throws ExpressionException ExpressionException
     */
    public void createMachineCode(Context context, MachineCodeListener mc) throws ExpressionException {
        try {
            int con = 0;
            if (constant != null)
                con = constant.getValue(context);

            int mcode = sourceReg.ordinal() | (destReg.ordinal() << 4);

            switch (opcode.getALUBSel()) {
                case instrSourceAndDest:
                    int ofs = con - context.getInstrAddr() - 1;
                    if (constInvalid(ofs, 8, true))
                        throw new ExpressionException("branch out of range in line " + lineNumber);
                    mcode = ofs & 0xff;
                    break;
                case instrDest:
                    if (constInvalid(con, 4, false))
                        throw new ExpressionException("constant to large in line " + lineNumber);
                    mcode = sourceReg.ordinal()
                            | ((con & 0xf) << 4);
                    break;
                case instrSource:
                    if (constInvalid(con, 4, false))
                        throw new ExpressionException("constant to large in line " + lineNumber);
                    mcode = (con & 0xf)
                            | (destReg.ordinal() << 4);
                    break;
                case ImReg:
                    int constBit = 0;
                    mc.add((con & 0x7fff) | 0x8000);
                    if ((con & 0x8000) != 0)
                        constBit = 1;
                    switch (opcode.getImmExtMode()) {
                        case extend:
                            if (constInvalid(con, 15, true))
                                throw new ExpressionException("displacement to large in line " + lineNumber);
                            break;
                        case src0:
                            mcode = constBit
                                    | (destReg.ordinal() << 4);
                            break;
                        case dest0:
                            mcode = sourceReg.ordinal()
                                    | (constBit << 4);
                            break;
                    }
            }
            mcode |= (opcode.ordinal() << 8);
            mc.add(mcode);
        } catch (ExpressionException e) {
            e.setLineNumber(lineNumber);
            throw e;
        }
    }

    private boolean constInvalid(int value, int bits, boolean signed) {
        if (signed) {
            return value < -(1 << (bits - 1)) || value >= (1 << (bits - 1));
        } else {
            return value < 0 || value >= (1 << bits);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (label != null) {
            sb.append(label).append(":");
        }
        sb.append(opcode.name());
        sb.append(" ");
        sb.append(opcode.getArguments().format(this));
        return sb.toString();
    }

    /**
     * @return the line number of this instruction
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * @return the represented opcode
     */
    public Opcode getOpcode() {
        return opcode;
    }

    /**
     * Sets the Opcode of this instruction
     *
     * @param opcode the opcode
     */
    public void setOpcode(Opcode opcode) {
        this.opcode = opcode;
    }

    /**
     * @return the source register
     */
    public Register getSourceReg() {
        return sourceReg;
    }

    /**
     * @return the designation register
     */
    public Register getDestReg() {
        return destReg;
    }

    /**
     * @return the constant value
     */
    public Expression getConstant() {
        return constant;
    }

    /**
     * Sets a abs addr for this instruction
     *
     * @param absAddr the address to set
     */
    public void setAbsAddr(int absAddr) {
        this.absAddr = absAddr;
    }

    /**
     * The absolute address is set by the .org directive
     * If this directive is not used, the value is -1;
     *
     * @return the absolute address of this instruction
     */
    public int getAbsAddr() {
        return absAddr;
    }
}
