package de.neemann.assembler.asm;

import de.neemann.assembler.expression.Context;
import de.neemann.assembler.expression.Expression;
import de.neemann.assembler.expression.ExpressionException;

/**
 * @author hneemann
 */
public final class Instruction {
    private final Register destReg;
    private final Register sourceReg;
    private final Expression constant;
    private Opcode opcode;
    private String label;
    private String macroDescription;
    private String comment;
    private int lineNumber;

    Instruction(Opcode opcode, Register destReg, Register sourceReg, Expression constant) {
        this.destReg = destReg;
        this.sourceReg = sourceReg;
        this.opcode = opcode;
        this.constant = constant;
    }

    public int size() {
        if (opcode.getALUBSel() == Opcode.ALUBSel.ImReg)
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

    public String getMacroDescription() {
        return macroDescription;
    }

    public void setMacroDescription(String macroDescription) {
        this.macroDescription = macroDescription;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void createMachineCode(Context context, MachineCodeListener mc) throws ExpressionException {
        try {
            int con = 0;
            if (constant != null)
                con = constant.getValue(context);

            int constBit = 0;
            if (opcode.getALUBSel() == Opcode.ALUBSel.ImReg) {
                mc.add((con & 0x7fff) | 0x8000);
                if ((con & 0x8000) != 0)
                    constBit = 1;
            }

            switch (opcode.getALUBSel()) {
                case instrSourceAndDest:
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
        sb.append(" ");
        sb.append(opcode.getArguments().format(this));
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
