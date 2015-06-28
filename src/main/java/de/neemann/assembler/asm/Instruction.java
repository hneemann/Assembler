package de.neemann.assembler.asm;

import de.neemann.assembler.expression.Context;
import de.neemann.assembler.expression.Expression;
import de.neemann.assembler.expression.ExpressionException;

/**
 * @author hneemann
 */
public class Instruction {

    private final int destReg;
    private final int sourceReg;
    private final Opcode opcode;
    private final Expression constant;
    private String label;

    public Instruction(Opcode opcode, int destReg, int sourceReg) {
        this(opcode, destReg, sourceReg, null);
    }

    public Instruction(Opcode opcode, int destReg, int sourceReg, Expression constant) {
        this.destReg = destReg & 0xf;
        this.sourceReg = sourceReg & 0xf;
        this.opcode = opcode;
        this.constant = constant;
    }

    public int size() {
        if (constant != null)
            return 2;
        else
            return 1;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void createMachineCode(Context context, MachineCodeListener mc) throws ExpressionException {
        int constBit = 0;
        if (constant != null) {
            int con = constant.getValue(context);
            mc.add((con & 0x7fff) | 0x8000);
            if ((con & 0x8000) != 0)
                constBit = 1;
        }

        mc.add(sourceReg
                | (destReg << 4)
                | (constBit << 8)
                | (opcode.ordinal() << 9));
    }
}
