package de.neemann.assembler.asm;

/**
 * Enumeration which represents a register
 * <p>
 * Created by neemann on 29.06.2015.
 */
public enum Register {
    //CHECKSTYLE.OFF: JavadocVariable
    R0, R1, R2, R3, R4,
    R5, R6, R7, R8, R9,
    R10, R11, R12, BP, SP,
    RA;
    //CHECKSTYLE.ON: JavadocVariable

    /**
     * Returns the register matching the given name
     *
     * @param name the name
     * @return the register
     */
    public static Register parseStr(String name) {
        for (Register r : Register.values())
            if (r.name().equalsIgnoreCase(name))
                return r;
        return null;
    }

}
