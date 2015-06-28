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
        if (opcode.getImmed() == Opcode.Immed.Regist)
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
        int con = 0;
        if (constant != null) {
            con = constant.getValue(context);
        }

        int constBit = 0;
        if (opcode.getImmed() == Opcode.Immed.Regist) {
            mc.add((con & 0x7fff) | 0x8000);
            if ((con & 0x8000) != 0)
                constBit = 1;
        }

        if (opcode.getImmed() == Opcode.Immed.instr) {
            int ofs = con - context.getAddr() - 1;
            mc.add(ofs & 0x1ff
                    | (opcode.ordinal() << 9));

        } else {
            mc.add(sourceReg
                    | (destReg << 4)
                    | (constBit << 8)
                    | (opcode.ordinal() << 9));
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (label != null) {
            sb.append(label).append(':');
        }

        sb.append('\t').append(opcode.name());

        switch (opcode.getRegsNeeded()) {
            case source:
                sb.append(" R").append(sourceReg);
                break;
            case dest:
                sb.append(" R").append(destReg);
                break;
            case both:
                sb.append(" R").append(destReg).append(", R").append(sourceReg);
                break;
        }

        if (opcode.getImmedNeeded() == Opcode.ImmedNeeded.Yes) {
            if (opcode.getRegsNeeded() != Opcode.RegsNeeded.none)
                sb.append(",");
            sb.append(' ').append(constant.toString());
        }

        return sb.toString();
    }
}
