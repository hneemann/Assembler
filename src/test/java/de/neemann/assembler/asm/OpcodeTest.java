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


    public void testHex() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baos);
        Opcode.writeControlWords(out);
        out.close();

        assertEquals("v2.0 raw\n" +
                "0\n" +
                "c0\n" +
                "4c0\n" +
                "44c0\n" +
                "8c0\n" +
                "48c0\n" +
                "cc0\n" +
                "10c0\n" +
                "14c0\n" +
                "c4\n" +
                "d4\n" +
                "4c4\n" +
                "4d4\n" +
                "44c4\n" +
                "44d4\n" +
                "8c4\n" +
                "8d4\n" +
                "48c4\n" +
                "48d4\n" +
                "cc4\n" +
                "cd4\n" +
                "10c4\n" +
                "10d4\n" +
                "14c4\n" +
                "14d4\n" +
                "2cc0\n" +
                "2cc4\n" +
                "2cd4\n" +
                "800\n" +
                "804\n" +
                "814\n" +
                "18c0\n" +
                "1cc0\n" +
                "58c0\n" +
                "5cc0\n" +
                "20c0\n" +
                "24c0\n" +
                "28c0\n" +
                "20040a\n" +
                "689\n" +
                "200006\n" +
                "20001e\n" +
                "85\n" +
                "95\n" +
                "200406\n" +
                "685\n" +
                "8018\n" +
                "10018\n" +
                "18018\n" +
                "28018\n" +
                "30018\n" +
                "38018\n" +
                "1a4\n" +
                "20\n" +
                "24\n" +
                "20018\n" +
                "240004\n" +
                "24001c\n" +
                "240408\n" +
                "80284\n" +
                "80294\n" +
                "80688\n" +
                "100000\n", baos.toString());
    }
}