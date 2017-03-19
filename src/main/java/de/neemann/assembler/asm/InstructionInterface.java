package de.neemann.assembler.asm;

import de.neemann.assembler.expression.Context;
import de.neemann.assembler.expression.ExpressionException;

/**
 * Interface to access an instruction.
 * Created by hneemann on 19.03.17.
 */
public interface InstructionInterface {
    /**
     * Returns an absolute instruction address.
     *
     * @return the address or -1 if no abs address is set
     */
    int getAbsAddr();

    /**
     * @return the line number of this instruction
     */
    int getLineNumber();

    /**
     * @return the size of this instruction
     */
    int size();

    /**
     * @return the label to address this instruction
     */
    String getLabel();

    /**
     * Emits the generated code to the given listener
     *
     * @param context             the context to obtain or set labels
     * @param machineCodeListener the listener to put the code to
     * @throws ExpressionException ExpressionException
     */
    void createMachineCode(Context context, MachineCodeListener machineCodeListener) throws ExpressionException;

    /**
     * @return a macro description
     */
    String getMacroDescription();

    /**
     * @return the comment of this instruction
     */
    String getComment();

}
