package de.neemann.assembler.asm;

/**
 * Created by neemann on 29.06.2015.
 */
public enum Register {
    R0, R1, R2, R3, R4,
    R5, R6, R7, R8, R9,
    R10, R11, R12, BP, SP,
    RA;

    public static Register parseStr(String name) {
        for (Register r : Register.values())
            if (r.name().equalsIgnoreCase(name))
                return r;
        return null;
    }

}
