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
                "200080\n" +
                "4004c0\n" +
                "4044c0\n" +
                "4008c0\n" +
                "4048c0\n" +
                "400cc0\n" +
                "4010c0\n" +
                "4014c0\n" +
                "c4\n" +
                "d4\n" +
                "4004c4\n" +
                "4004d4\n" +
                "4044c4\n" +
                "4044d4\n" +
                "4008c4\n" +
                "4008d4\n" +
                "4048c4\n" +
                "4048d4\n" +
                "400cc4\n" +
                "400cd4\n" +
                "4010c4\n" +
                "4010d4\n" +
                "4014c4\n" +
                "4014d4\n" +
                "402cc0\n" +
                "402cc4\n" +
                "402cd4\n" +
                "400800\n" +
                "400804\n" +
                "400814\n" +
                "4018c0\n" +
                "401cc0\n" +
                "4058c0\n" +
                "405cc0\n" +
                "4020c0\n" +
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
                "100000\n"+
                "800020\n", baos.toString());
    }
}