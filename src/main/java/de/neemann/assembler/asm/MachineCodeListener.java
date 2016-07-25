package de.neemann.assembler.asm;

/**
 * Listener for created machine instructions
 *
 * @author hneemann
 */
public interface MachineCodeListener {
    /**
     * Adds a instruction word to the machine program.
     * Some of the instructions need two instruction words.
     *
     * @param instr the instruction word
     */
    void add(int instr);
}
