package de.neemann.assembler.asm;

import de.neemann.assembler.expression.Context;
import de.neemann.assembler.expression.ExpressionException;

/**
 * Used to store a data word in program memory
 * Created by hneemann on 19.03.17.
 */
public class DataInstruction implements InstructionInterface {
    private final int value;
    private final int lineNum;
    private final String label;

    /**
     * Creates a new instance
     *
     * @param value   the value to store
     * @param lineNum the line number
     * @param label   the label of the data word
     */
    public DataInstruction(int value, int lineNum, String label) {
        this.value = value;
        this.lineNum = lineNum;
        this.label = label;
    }

    @Override
    public int getAbsAddr() {
        return -1;
    }

    @Override
    public int getLineNumber() {
        return lineNum;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void createMachineCode(Context context, MachineCodeListener machineCodeListener) throws ExpressionException {
        machineCodeListener.add(value);
    }

    @Override
    public String getMacroDescription() {
        return null;
    }

    @Override
    public String getComment() {
        return null;
    }

    @Override
    public String toString() {
        return "'" + getChar() + "', " + Integer.toString(value);
    }

    private String getChar() {
        if (value >= 32)
            return "" + (char) value;
        else
            switch (value) {
                case '\n':
                    return "\\n";
                case '\r':
                    return "\\r";
                case '\t':
                    return "\\t";
                case '\0':
                    return "\\0";
                default:
                    return "#" + value;
            }
    }
}
