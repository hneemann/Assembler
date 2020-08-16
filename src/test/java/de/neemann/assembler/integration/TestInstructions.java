package de.neemann.assembler.integration;

import junit.framework.TestCase;

public class TestInstructions extends TestCase {
    private static final String PROCESSOR = "/home/hneemann/Dokumente/Java/digital/src/main/dig/processor/Processor.dig";

    public void testMOV() throws Exception {
        new ProcessorTester(PROCESSOR)
                .setRegister("R1", 3)
                .setRegister("R2", 4)
                .runToBrk("mov r2,r1\nbrk")
                .checkRegister("R1", 3)
                .checkRegister("R2", 3);
    }

    public void testADD() throws Exception {
        new ProcessorTester(PROCESSOR)
                .setRegister("R1", 3)
                .setRegister("R2", 4)
                .runToBrk("add r2,r1\nbrk")
                .checkRegister("R2", 7);
    }

    public void testADC() throws Exception {
        new ProcessorTester(PROCESSOR)
                .setRegister("R1", 3)
                .setRegister("R2", 4)
                .runToBrk("adc r2,r1\nbrk")
                .checkRegister("R2", 7);
        new ProcessorTester(PROCESSOR)
                .setRegister("R1", 3)
                .setRegister("R2", 4)
                .runToBrk("subi R0,1\nadc r2,r1\nbrk")
                .checkRegister("R2", 8);
    }

    public void testADDI() throws Exception {
        new ProcessorTester(PROCESSOR)
                .setRegister("R1", 3)
                .runToBrk("addi r1,4\nbrk")
                .checkRegister("R1", 7);
        new ProcessorTester(PROCESSOR)
                .setRegister("R1", 3)
                .runToBrk("addi r1,20\nbrk")
                .checkRegister("R1", 23);
    }

    public void testADCI() throws Exception {
        new ProcessorTester(PROCESSOR)
                .setRegister("R1", 3)
                .runToBrk("adci r1,4\nbrk")
                .checkRegister("R1", 7);
        new ProcessorTester(PROCESSOR)
                .setRegister("R1", 3)
                .runToBrk("adci r1,20\nbrk")
                .checkRegister("R1", 23);

        new ProcessorTester(PROCESSOR)
                .setRegister("R1", 3)
                .runToBrk("subi R0,1\nadci r1,4\nbrk")
                .checkRegister("R1", 8);
        new ProcessorTester(PROCESSOR)
                .setRegister("R1", 3)
                .runToBrk("subi R0,1\nadci r1,20\nbrk")
                .checkRegister("R1", 24);
    }

    public void testSUB() throws Exception {
        new ProcessorTester(PROCESSOR)
                .setRegister("R1", 3)
                .setRegister("R2", 4)
                .runToBrk("sub r2,r1\nbrk")
                .checkRegister("R2", 1);
    }

    public void testSBC() throws Exception {
        new ProcessorTester(PROCESSOR)
                .setRegister("R1", 3)
                .setRegister("R2", 4)
                .runToBrk("sbc r2,r1\nbrk")
                .checkRegister("R2", 1);
        new ProcessorTester(PROCESSOR)
                .setRegister("R1", 3)
                .setRegister("R2", 5)
                .runToBrk("subi R0,1\nsbc r2,r1\nbrk")
                .checkRegister("R2", 1);
    }

    public void testSUBI() throws Exception {
        new ProcessorTester(PROCESSOR)
                .setRegister("R1", 4)
                .runToBrk("subi r1,2\nbrk")
                .checkRegister("R1", 2);
        new ProcessorTester(PROCESSOR)
                .setRegister("R1", 30)
                .runToBrk("subi r1,20\nbrk")
                .checkRegister("R1", 10);
    }

    public void testSBCI() throws Exception {
        new ProcessorTester(PROCESSOR)
                .setRegister("R1", 6)
                .runToBrk("sbci r1,4\nbrk")
                .checkRegister("R1", 2);
        new ProcessorTester(PROCESSOR)
                .setRegister("R1", 30)
                .runToBrk("sbci r1,20\nbrk")
                .checkRegister("R1", 10);

        new ProcessorTester(PROCESSOR)
                .setRegister("R1", 6)
                .runToBrk("subi R0,1\nsbci r1,4\nbrk")
                .checkRegister("R1", 1);
        new ProcessorTester(PROCESSOR)
                .setRegister("R1", 30)
                .runToBrk("subi R0,1\nsbci r1,20\nbrk")
                .checkRegister("R1", 9);
    }

    public void testNOT() throws Exception {
        new ProcessorTester(PROCESSOR)
                .setRegister("R1", 0)
                .runToBrk("not r1\nbrk")
                .checkRegister("R1", 0xffff);
    }

    public void testNEG() throws Exception {
        new ProcessorTester(PROCESSOR)
                .setRegister("R1", 1)
                .runToBrk("neg r1\nbrk")
                .checkRegister("R1", 0xffff);
    }

    public void testSWAP() throws Exception {
        new ProcessorTester(PROCESSOR)
                .setRegister("R1", 0x1234)
                .runToBrk("swap r1\nbrk")
                .checkRegister("R1", 0x3412);
    }

    public void testSWAPN() throws Exception {
        new ProcessorTester(PROCESSOR)
                .setRegister("R1", 0x1234)
                .runToBrk("swapn r1\nbrk")
                .checkRegister("R1", 0x2143);
    }

    public void testLDI() throws Exception {
        new ProcessorTester(PROCESSOR)
                .runToBrk("ldi r1,5\nbrk", 2)
                .checkRegister("R1", 5);

        new ProcessorTester(PROCESSOR)
                .runToBrk("ldi r1,15\nbrk", 2)
                .checkRegister("R1", 15);

        new ProcessorTester(PROCESSOR)
                .runToBrk("ldi r1,16\nbrk", 3)
                .checkRegister("R1", 16);

        new ProcessorTester(PROCESSOR)
                .runToBrk("ldi r1,0x8000\nbrk", 3)
                .checkRegister("R1", 0x8000);
    }

}
