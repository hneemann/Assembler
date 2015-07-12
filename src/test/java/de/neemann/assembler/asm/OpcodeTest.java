package de.neemann.assembler.asm;

import junit.framework.TestCase;

/**
 * @author hneemann
 */
public class OpcodeTest extends TestCase {


    public void testPlausibility() throws Exception {
        for (Opcode op : Opcode.values()) {
            checkConstAccess(op);
            checkBusAccess(op);
        }
    }

    public void checkConstAccess(Opcode op) {
        // plausibility checks!
        switch (op.getRegsNeeded()) {
            case source:
                assertFalse("source reg and const used " + op.name(),
                        op.getALUBSel() == Opcode.ALUBSel.instrSource
                                || op.getALUBSel() == Opcode.ALUBSel.instrSourceAndDest);
                break;
            case dest:
                assertFalse("source reg and const used " + op.name(),
                        op.getALUBSel() == Opcode.ALUBSel.instrDest
                                || op.getALUBSel() == Opcode.ALUBSel.instrSourceAndDest);
                break;
            case both:
                assertFalse("dest reg and const used " + op.name(),
                        op.getALUBSel() == Opcode.ALUBSel.instrDest
                                || op.getALUBSel() == Opcode.ALUBSel.instrSource
                                || op.getALUBSel() == Opcode.ALUBSel.instrSourceAndDest);
                break;
        }
    }

    public void checkBusAccess(Opcode op) {
        int toBusCounter = 0;
        if (op.getAluToBus() == Opcode.ALUToBus.Yes) toBusCounter++;
        if (op.getReadRam() == Opcode.ReadRam.Yes) toBusCounter++;
        if (op.getReadIO() == Opcode.ReadIO.Yes) toBusCounter++;
        if (op.getStorePC() == Opcode.StorePC.Yes) toBusCounter++;
        assertFalse("more than one write access to bus! " + op.name(),
                toBusCounter > 1);
        assertFalse("value written to bus but no store! " + op.name(),
                toBusCounter == 1 && op.getEnRegWrite() == Opcode.EnRegWrite.No);
        assertFalse("value stored but no one writes to bus! " + op.name(),
                toBusCounter == 0 && op.getEnRegWrite() == Opcode.EnRegWrite.Yes);
    }

}