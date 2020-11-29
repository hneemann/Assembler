package de.neemann.assembler.integration;

import java.io.IOException;

public class TestInstructions {

    public static void main(String[] args) throws IOException {
        new TestGenerator(new TestInstructions())
                .write("/home/hneemann/Dokumente/Java/digital/src/main/dig/processor/ProcessorTest.dig");
    }

    public void testMOV(Test test) throws Exception {
        test.add(new ProcessorTest("MOV")
                .setRegister("R1", 3)
                .setRegister("R2", 4)
                .run("mov r2,r1")
                .checkRegister("R1", 3)
                .checkRegister("R2", 3));
    }

    public void testADD(Test test) throws Exception {
        test.add(new ProcessorTest("ADD no carry")
                .setRegister("R1", 3)
                .setRegister("R2", 4)
                .run("add r2,r1")
                .checkCarry(false)
                .checkRegister("R2", 7));
        test.add(new ProcessorTest("ADD carry")
                .setRegister("R1", -1)
                .setRegister("R2", -1)
                .run("add r2,r1")
                .checkCarry(true)
                .checkRegister("R2", 0xfffe));
    }

    public void testADC(Test test) throws Exception {
        test.add(new ProcessorTest("ADC no carry")
                .setRegister("R1", 3)
                .setRegister("R2", 4)
                .run("adc r2,r1")
                .checkCarry(false)
                .checkRegister("R2", 7));
        test.add(new ProcessorTest("ADC carry")
                .setRegister("R1", 3)
                .setRegister("R2", 4)
                .setCarry(true)
                .run("adc r2,r1")
                .checkCarry(false)
                .checkRegister("R2", 8));
        test.add(new ProcessorTest("ADC carry out")
                .setRegister("R1", -1)
                .setRegister("R2", -1)
                .run("adc r2,r1")
                .checkCarry(true)
                .checkRegister("R2", 0xfffe));
    }

    public void testADDI(Test test) throws Exception {
        test.add(new ProcessorTest("ADDI small")
                .setRegister("R1", 3)
                .run("addi r1,4")
                .checkRegister("R1", 7));
        test.add(new ProcessorTest("ADDI large")
                .setRegister("R1", 3)
                .run("addi r1,20")
                .checkRegister("R1", 23));
    }

    public void testADCI(Test test) throws Exception {
        test.add(new ProcessorTest("ADCI small")
                .setRegister("R1", 3)
                .run("adci r1,4")
                .checkRegister("R1", 7));
        test.add(new ProcessorTest("ADCI large")
                .setRegister("R1", 3)
                .run("adci r1,20")
                .checkRegister("R1", 23));

        test.add(new ProcessorTest("ADCI small, carry")
                .setRegister("R1", 3)
                .setCarry(true)
                .run("adci r1,4")
                .checkRegister("R1", 8));
        test.add(new ProcessorTest("ADCI large, carry")
                .setRegister("R1", 3)
                .setCarry(true)
                .run("adci r1,20")
                .checkRegister("R1", 24));
    }

    public void testSUB(Test test) throws Exception {
        test.add(new ProcessorTest("SUB")
                .setRegister("R1", 3)
                .setRegister("R2", 4)
                .run("sub r2,r1")
                .checkCarry(false)
                .checkRegister("R2", 1));
        test.add(new ProcessorTest("SUB carry out")
                .setRegister("R1", 4)
                .setRegister("R2", 3)
                .run("sub r2,r1")
                .checkCarry(true)
                .checkRegister("R2", 0xffff));
    }

    public void testSBC(Test test) throws Exception {
        test.add(new ProcessorTest("SBC")
                .setRegister("R1", 3)
                .setRegister("R2", 4)
                .run("sbc r2,r1")
                .checkCarry(false)
                .checkRegister("R2", 1));
        test.add(new ProcessorTest("SBC carry in")
                .setRegister("R1", 3)
                .setRegister("R2", 5)
                .setCarry(true)
                .run("sbc r2,r1")
                .checkCarry(false)
                .checkRegister("R2", 1));
    }

    public void testSUBI(Test test) throws Exception {
        test.add(new ProcessorTest("SUBI small")
                .setRegister("R1", 4)
                .run("subi r1,2")
                .checkRegister("R1", 2));
        test.add(new ProcessorTest("SUBI large")
                .setRegister("R1", 30)
                .run("subi r1,20")
                .checkRegister("R1", 10));
    }

    public void testSBCI(Test test) throws Exception {
        test.add(new ProcessorTest("SBCI no carry, small")
                .setRegister("R1", 6)
                .run("sbci r1,4")
                .checkRegister("R1", 2));
        test.add(new ProcessorTest("SBCI no carry, large")
                .setRegister("R1", 30)
                .run("sbci r1,20")
                .checkRegister("R1", 10));

        test.add(new ProcessorTest("SBCI carry in, small")
                .setRegister("R1", 6)
                .setCarry(true)
                .run("sbci r1,4")
                .checkRegister("R1", 1));
        test.add(new ProcessorTest("SBCI carry in, large")
                .setRegister("R1", 30)
                .setCarry(true)
                .run("sbci r1,20")
                .checkRegister("R1", 9));
    }

    public void testNOT(Test test) throws Exception {
        test.add(new ProcessorTest("NOT")
                .setRegister("R1", 0)
                .run("not r1")
                .checkRegister("R1", 0xffff));
    }

    public void testNEG(Test test) throws Exception {
        test.add(new ProcessorTest("NEG")
                .setRegister("R1", 1)
                .run("neg r1")
                .checkRegister("R1", 0xffff));
    }

    public void testSWAP(Test test) throws Exception {
        test.add(new ProcessorTest("SWAP")
                .setRegister("R1", 0x1234)
                .run("swap r1\nbrk")
                .checkRegister("R1", 0x3412));
    }

    public void testSWAPN(Test test) throws Exception {
        test.add(new ProcessorTest("SWAPN")
                .setRegister("R1", 0x1234)
                .run("swapn r1")
                .checkRegister("R1", 0x2143));
    }

    public void testLDI(Test test) throws Exception {
        test.add(new ProcessorTest("LDI small")
                .run("ldi r1,5", 1)
                .checkRegister("R1", 5));

        test.add(new ProcessorTest("LDI small")
                .run("ldi r1,15", 1)
                .checkRegister("R1", 15));

        test.add(new ProcessorTest("LDI large")
                .run("ldi r1,16", 2)
                .checkRegister("R1", 16));

        test.add(new ProcessorTest("LDI large")
                .run("ldi r1,0x8000", 2)
                .checkRegister("R1", 0x8000));
    }

    public void testLDS(Test test) throws Exception {
        test.add(new ProcessorTest("LDS small")
                .setMemory(0,6)
                .setMemory(1,7)
                .run("lds r1,0\nlds r2,1", 2)
                .checkRegister("R1", 6)
                .checkRegister("R2", 7));

        test.add(new ProcessorTest("LDS large")
                .setMemory(0x8000,8)
                .run("lds r1,0x8000", 2)
                .checkRegister("R1", 8));
    }

    public void testSTS(Test test) throws Exception {
        test.add(new ProcessorTest("STS small")
                .setRegister("R1",7)
                .run("sts 1,r1\nlds r2,1", 2)
                .checkRegister("R2", 7));

        test.add(new ProcessorTest("STS large")
                .setRegister("R1",8)
                .run("sts 0x8000,r1\nlds r2,0x8000", 4)
                .checkRegister("R1", 8));
    }

    public void testLD(Test test) throws Exception {
        test.add(new ProcessorTest("LD")
                .setMemory(1,7)
                .setMemory(0x8000,8)
                .setRegister("R2",1)
                .setRegister("R3",0x8000)
                .run("ld r0,[r2]\nld r1,[r3]", 2)
                .checkRegister("R0", 7)
                .checkRegister("R1", 8));
    }

    public void testLDD(Test test) throws Exception {
        test.add(new ProcessorTest("LDD")
                .setMemory(3,7)
                .setMemory(0x8000,8)
                .setRegister("R2",1)
                .setRegister("R3",0x8002)
                .run("ldd r0,[r2+2]\nldd r1,[r3-2]", 4)
                .checkRegister("R0", 7)
                .checkRegister("R1", 8));
    }

    public void testST(Test test) throws Exception {
        test.add(new ProcessorTest("ST")
                .setRegister("R1",1)
                .setRegister("R2",7)
                .run("st [r1],r2\nlds r0,1", 2)
                .checkRegister("R0", 7));
    }

    public void testSTD(Test test) throws Exception {
        test.add(new ProcessorTest("STD")
                .setRegister("R2",4)
                .setRegister("R3",7)
                .setRegister("R4",8)
                .run("std [r2+2],r3\nstd [r2-2],r4\nlds r0,6\nlds r1,2", 6)
                .checkRegister("R0", 7)
                .checkRegister("R1", 8));
    }

}
