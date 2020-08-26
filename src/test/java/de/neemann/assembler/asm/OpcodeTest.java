package de.neemann.assembler.asm;

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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

        if (op.getArguments().hasSource() && op.getArguments().hasDest()) {
            assertFalse("dest reg and const used " + op.name(),
                    op.getALUBSel() == Opcode.ALUBSel.instrDest
                            || op.getALUBSel() == Opcode.ALUBSel.instrSource
                            || op.getALUBSel() == Opcode.ALUBSel.instrSourceAndDest);
        } else {
            if (op.getArguments().hasSource()) {
                assertFalse("source reg and const used " + op.name(),
                        op.getALUBSel() == Opcode.ALUBSel.instrSource
                                || op.getALUBSel() == Opcode.ALUBSel.instrSourceAndDest);
            }
            if (op.getArguments().hasDest()) {
                assertFalse("source reg and const used " + op.name(),
                        op.getALUBSel() == Opcode.ALUBSel.instrDest
                                || op.getALUBSel() == Opcode.ALUBSel.instrSourceAndDest);
            }
        }
    }

    public void checkBusAccess(Opcode op) {
        int toBusCounter = 0;
        if (op.getSrcToBus() == Opcode.SrcToBus.Yes) toBusCounter++;
        if (op.getAluToBus() == Opcode.ALUToBus.Yes) toBusCounter++;
        if (op.getReadRam() == Opcode.ReadRam.Yes) toBusCounter++;
        if (op.getReadIO() == Opcode.ReadIO.Yes) toBusCounter++;
        if (op.getStorePC() == Opcode.StorePC.Yes) toBusCounter++;
        assertFalse("more than one write access to bus! " + op.name(),
                toBusCounter > 1);
        assertFalse("value written to bus but no store! " + op.name(),
                toBusCounter == 1
                        && op.getEnRegWrite() == Opcode.EnRegWrite.No
                        && op.getWriteIO() == Opcode.WriteIO.No
                        && op.getWriteRam() == Opcode.WriteRam.No);
        assertFalse("value stored but no one writes to bus! " + op.name(),
                toBusCounter == 0 && op.getEnRegWrite() == Opcode.EnRegWrite.Yes);
    }


    public void testHex() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baos);
        Opcode.writeControlWords(out);
        out.close();

        assertEquals("v2.0 raw\n" +
                "0\n" +
                "208\n" +
                "e10\n" +
                "f10\n" +
                "e20\n" +
                "f20\n" +
                "e30\n" +
                "e40\n" +
                "e50\n" +
                "2a02\n" +
                "a05\n" +
                "2e12\n" +
                "e15\n" +
                "2f12\n" +
                "f15\n" +
                "2e22\n" +
                "e25\n" +
                "2f22\n" +
                "f25\n" +
                "a70\n" +
                "2e32\n" +
                "e35\n" +
                "2e42\n" +
                "e45\n" +
                "2e52\n" +
                "e55\n" +
                "a60\n" +
                "ed0\n" +
                "2ed2\n" +
                "ed5\n" +
                "420\n" +
                "520\n" +
                "2422\n" +
                "425\n" +
                "2522\n" +
                "525\n" +
                "e80\n" +
                "e90\n" +
                "f80\n" +
                "f90\n" +
                "ea0\n" +
                "ab0\n" +
                "ac0\n" +
                "8001b\n" +
                "60213\n" +
                "8300a\n" +
                "8000f\n" +
                "42202\n" +
                "40205\n" +
                "8001a\n" +
                "60212\n" +
                "a01\n" +
                "4006\n" +
                "8006\n" +
                "c006\n" +
                "14006\n" +
                "18006\n" +
                "1c006\n" +
                "902202\n" +
                "100000\n" +
                "102002\n" +
                "10006\n" +
                "20300a\n" +
                "20000f\n" +
                "20001b\n" +
                "422202\n" +
                "420205\n" +
                "420213\n" +
                "1000000\n" +
                "2100000\n", baos.toString());
    }
}