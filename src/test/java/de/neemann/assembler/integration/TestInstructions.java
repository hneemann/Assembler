package de.neemann.assembler.integration;

import de.neemann.assembler.asm.InstructionException;
import de.neemann.assembler.expression.ExpressionException;
import de.neemann.assembler.parser.ParserException;
import org.jdom2.JDOMException;

import java.io.IOException;

public class TestInstructions {

    public static void main(String[] args) throws IOException, JDOMException {
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

    public void testArithmetic(Test test) throws ExpressionException, ParserException, InstructionException, IOException {
        createArithmeticTest(test, "add", 2, 3, false, 5, false);
        createArithmeticTest(test, "add", 2, 3, true, 5, false);
        createArithmeticTest(test, "add", -1, 3, false, 2, true);
        createArithmeticTest(test, "add", -1, 3, true, 2, true);

        createArithmeticTest(test, "adc", 2, 3, false, 5, false);
        createArithmeticTest(test, "adc", 2, 3, true, 6, false);
        createArithmeticTest(test, "adc", -1, 3, false, 2, true);
        createArithmeticTest(test, "adc", -1, 3, true, 3, true);

        createArithmeticTest(test, "sub", 3, 2, false, 1, false);
        createArithmeticTest(test, "sub", 3, 2, true, 1, false);
        createArithmeticTest(test, "sub", 2, 3, false, 0xffff, true);
        createArithmeticTest(test, "sub", 2, 3, true, 0xffff, true);

        createArithmeticTest(test, "sbc", 3, 2, false, 1, false);
        createArithmeticTest(test, "sbc", 3, 2, true, 0, false);
        createArithmeticTest(test, "sbc", 2, 3, false, 0xffff, true);
        createArithmeticTest(test, "sbc", 2, 3, true, 0xfffe, true);
    }

    private void createArithmeticTest(Test test, String command, int a, int b, boolean carryIn, int result, boolean carryOut) throws ExpressionException, ParserException, InstructionException, IOException {
        test.add(new ProcessorTest(command.toUpperCase() + (carryIn ? " C_in" : "") + (carryOut ? " C_out" : ""))
                .setCarry(carryIn)
                .setRegister("R1", a)
                .setRegister("R2", b)
                .run(command + " r1,r2")
                .checkCarry(carryOut)
                .checkRegister("R1", result)
                .checkRegister("R2", b));

        test.add(new ProcessorTest(command.toUpperCase() + "I" + (carryIn ? " C_in" : "") + (carryOut ? " C_out" : ""))
                .setCarry(carryIn)
                .setRegister("R1", a)
                .run(command + "i r1," + b)
                .checkCarry(carryOut)
                .checkRegister("R1", result));
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

    public void testLogic(Test test) throws ExpressionException, ParserException, InstructionException, IOException {
        createLogicTest(test, "and", 2, 3, 2);
        createLogicTest(test, "or", 2, 3, 3);
        createLogicTest(test, "eor", 2, 3, 1);
    }

    private void createLogicTest(Test test, String command, int a, int b, int result) throws ExpressionException, ParserException, InstructionException, IOException {
        test.add(new ProcessorTest(command.toUpperCase())
                .setRegister("R1", a)
                .setRegister("R2", b)
                .run(command + " r1,r2", 1)
                .checkRegister("R1", result)
                .checkRegister("R2", b));

        test.add(new ProcessorTest(command.toUpperCase() + "I short")
                .setRegister("R1", a)
                .run(command + "i r1," + b, 1)
                .checkRegister("R1", result));

        test.add(new ProcessorTest(command.toUpperCase() + "I")
                .setRegister("R1", a * 8)
                .run(command + "i r1," + b * 8, 2)
                .checkRegister("R1", result * 8));
    }

    public void testShift(Test test) throws ExpressionException, ParserException, InstructionException, IOException {
        test.add(new ProcessorTest("LSL")
                .setRegister("R1", 8)
                .run("lsl r1")
                .checkRegister("R1", 16));
        test.add(new ProcessorTest("LSR")
                .setRegister("R1", 8)
                .run("lsr r1")
                .checkRegister("R1", 4));
        test.add(new ProcessorTest("LSR")
                .setRegister("R1", 0xffff)
                .run("lsr r1")
                .checkRegister("R1", 0x7fff));
        test.add(new ProcessorTest("ASR")
                .setRegister("R1", 8)
                .run("asr r1")
                .checkRegister("R1", 4));
        test.add(new ProcessorTest("ASR")
                .setRegister("R1", 0xffff)
                .run("asr r1")
                .checkRegister("R1", 0xffff));
    }

    public void testRotate(Test test) throws ExpressionException, ParserException, InstructionException, IOException {
        test.add(new ProcessorTest("ROR")
                .setCarry(false)
                .setRegister("R1", 8)
                .run("ror r1")
                .checkRegister("R1", 4));
        test.add(new ProcessorTest("ROR")
                .setCarry(true)
                .setRegister("R1", 8)
                .run("ror r1")
                .checkRegister("R1", 0x8004));
        test.add(new ProcessorTest("ROL")
                .setCarry(false)
                .setRegister("R1", 8)
                .run("rol r1")
                .checkRegister("R1", 16));
        test.add(new ProcessorTest("ROL")
                .setCarry(true)
                .setRegister("R1", 8)
                .run("rol r1")
                .checkRegister("R1", 17));
    }


    public void testSWAP(Test test) throws Exception {
        test.add(new ProcessorTest("SWAP")
                .setRegister("R1", 0x1234)
                .run("swap r1")
                .checkRegister("R1", 0x3412));
    }

    public void testSWAPN(Test test) throws Exception {
        test.add(new ProcessorTest("SWAPN")
                .setRegister("R1", 0x1234)
                .run("swapn r1")
                .checkRegister("R1", 0x2143));
    }

    public void testMUL(Test test) throws Exception {
        test.add(new ProcessorTest("MUL")
                .setRegister("R0", 7)
                .setRegister("R1", 8)
                .run("mul r0,r1")
                .checkRegister("R1", 8)
                .checkRegister("R0", 7 * 8));
    }

    public void testMULI(Test test) throws Exception {
        test.add(new ProcessorTest("MULI short")
                .setRegister("R0", 7)
                .run("muli r0,3", 1)
                .checkRegister("R0", 7 * 3));
        test.add(new ProcessorTest("MULI long")
                .setRegister("R0", 7)
                .run("muli r0,32", 2)
                .checkRegister("R0", 7 * 32));
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
                .setMemory(0, 6)
                .setMemory(1, 7)
                .run("lds r1,0\nlds r2,1", 2)
                .checkRegister("R1", 6)
                .checkRegister("R2", 7));

        test.add(new ProcessorTest("LDS large")
                .setMemory(0x8000, 8)
                .run("lds r1,0x8000", 2)
                .checkRegister("R1", 8));
    }

    public void testSTS(Test test) throws Exception {
        test.add(new ProcessorTest("STS small")
                .setRegister("R1", 7)
                .run("sts 1,r1\nlds r2,1", 2)
                .checkRegister("R2", 7));

        test.add(new ProcessorTest("STS large")
                .setRegister("R1", 8)
                .run("sts 0x8000,r1\nlds r2,0x8000", 4)
                .checkRegister("R1", 8));
    }

    public void testLD(Test test) throws Exception {
        test.add(new ProcessorTest("LD")
                .setMemory(1, 7)
                .setMemory(0x8000, 8)
                .setRegister("R2", 1)
                .setRegister("R3", 0x8000)
                .run("ld r0,[r2]\nld r1,[r3]", 2)
                .checkRegister("R0", 7)
                .checkRegister("R1", 8));
    }

    public void testLDD(Test test) throws Exception {
        test.add(new ProcessorTest("LDD")
                .setMemory(3, 7)
                .setMemory(0x8000, 8)
                .setRegister("R2", 1)
                .setRegister("R3", 0x8002)
                .run("ldd r0,[r2+2]\nldd r1,[r3-2]", 4)
                .checkRegister("R0", 7)
                .checkRegister("R1", 8));
    }

    public void testST(Test test) throws Exception {
        test.add(new ProcessorTest("ST")
                .setRegister("R1", 1)
                .setRegister("R2", 7)
                .run("st [r1],r2\nlds r0,1", 2)
                .checkRegister("R0", 7));
    }

    public void testSTD(Test test) throws Exception {
        test.add(new ProcessorTest("STD")
                .setRegister("R2", 4)
                .setRegister("R3", 7)
                .setRegister("R4", 8)
                .run("std [r2+2],r3\nstd [r2-2],r4\nlds r0,6\nlds r1,2", 6)
                .checkRegister("R0", 7)
                .checkRegister("R1", 8));
    }

    public void testJmp(Test test) throws Exception {
        test.add(new ProcessorTest("JMP short")
                .setRegister("R0", 2)
                .run("jmp end\n ldi r0, 1\nend: nop", 3)
                .checkRegister("R0", 2));
        test.add(new ProcessorTest("JMP short back")
                .setRegister("PC", 20)
                .run("ldi r0,1\n.org 10\ncode: ldi r0,3\n.org 20\njmp code\n ldi r0, 2", 22)
                .setCycles(2)
                .checkRegister("R0", 3));
        test.add(new ProcessorTest("JMP long")
                .run("jmp 130\n ldi r0, 1\n.org 130\nldi r0,2", 131)
                .setCycles(3)
                .checkRegister("R0", 2));
    }

    public void testCall(Test test) throws Exception {
        test.add(new ProcessorTest("RCALL")
                .setRegister("R0", 3)
                .run("jmp end\n" +
                        "ldi r0, 1\n" +
                        "func: ldi r0,1\n" +
                        "rret ra\n" +
                        "ldi r0,2\n" +
                        "end: rcall ra,func", 7)
                .checkRegister("R0", 1)
                .checkRegister("RA", 7));
    }

    public void testCompare(Test test) throws ExpressionException, ParserException, InstructionException, IOException {
        createCompare(test, "cmp", 2, 1, 0, 0, 0, 0);
        createCompare(test, "cmp", 2, 1, 1, 0, 0, 0);
        createCompare(test, "cmp", 2, 2, 0, 0, 1, 0);
        createCompare(test, "cmp", 2, 2, 1, 0, 1, 0);
        createCompare(test, "cmp", 2, 3, 0, 1, 0, 1);
        createCompare(test, "cmp", 2, 3, 1, 1, 0, 1);

        createCompare(test, "cpc", 2, 1, 0, 0, 0, 0);
        createCompare(test, "cpc", 2, 0, 1, 0, 0, 0);
        createCompare(test, "cpc", 2, 2, 0, 0, 1, 0);
        createCompare(test, "cpc", 2, 1, 1, 0, 1, 0);
        createCompare(test, "cpc", 2, 3, 0, 1, 0, 1);
        createCompare(test, "cpc", 2, 2, 1, 1, 0, 1);
    }

    private void createCompare(Test test, String command, int a, int b, int carryIn, int carry, int zero, int neg) throws ExpressionException, ParserException, InstructionException, IOException {
        test.add(new ProcessorTest(command.toUpperCase() + " " + a + b + carryIn)
                .setRegister("Carry", carryIn)
                .setRegister("R0", a)
                .setRegister("R1", b)
                .run(command + " r0,r1", 1)
                .checkRegister("Carry", carry)
                .checkRegister("Zero", zero)
                .checkRegister("Neg", neg));
    }

    public void testCompareI(Test test) throws ExpressionException, ParserException, InstructionException, IOException {
        createCompareI(test, "cpi", 2, 1, 0, 0, 0, 0);
        createCompareI(test, "cpi", 2, 1, 1, 0, 0, 0);
        createCompareI(test, "cpi", 2, 2, 0, 0, 1, 0);
        createCompareI(test, "cpi", 2, 2, 1, 0, 1, 0);
        createCompareI(test, "cpi", 2, 3, 0, 1, 0, 1);
        createCompareI(test, "cpi", 2, 3, 1, 1, 0, 1);

        createCompareI(test, "cpci", 2, 1, 0, 0, 0, 0);
        createCompareI(test, "cpci", 2, 0, 1, 0, 0, 0);
        createCompareI(test, "cpci", 2, 2, 0, 0, 1, 0);
        createCompareI(test, "cpci", 2, 1, 1, 0, 1, 0);
        createCompareI(test, "cpci", 2, 3, 0, 1, 0, 1);
        createCompareI(test, "cpci", 2, 2, 1, 1, 0, 1);
    }

    private void createCompareI(Test test, String command, int a, int b, int carryIn, int carry, int zero, int neg) throws ExpressionException, ParserException, InstructionException, IOException {
        test.add(new ProcessorTest(command.toUpperCase() + " " + a + b + carryIn + "S")
                .setRegister("Carry", carryIn)
                .setRegister("R0", a)
                .run(command + " r0," + b, 1)
                .checkRegister("Carry", carry)
                .checkRegister("Zero", zero)
                .checkRegister("Neg", neg));
        test.add(new ProcessorTest(command.toUpperCase() + " " + a + b + carryIn + "L")
                .setRegister("Carry", carryIn)
                .setRegister("R0", a+20)
                .run(command + " r0," + (b+20), 2)
                .checkRegister("Carry", carry)
                .checkRegister("Zero", zero)
                .checkRegister("Neg", neg));
    }

    public void testBranches(Test test) throws Exception {
        createBranchTest(test, "brcs", "Carry", 1);
        createBranchTest(test, "brcc", "Carry", 0);
        createBranchTest(test, "brmi", "Neg", 1);
        createBranchTest(test, "brpl", "Neg", 0);
        createBranchTest(test, "breq", "Zero", 1);
        createBranchTest(test, "brne", "Zero", 0);
    }

    private void createBranchTest(Test test, String command, String flag, int branchOn) throws ParserException, IOException, ExpressionException, InstructionException {
        test.add(new ProcessorTest(command.toUpperCase() + " jmp")
                .setRegister(flag, branchOn)
                .setRegister("R0", 2)
                .run(command + " end\n ldi r0, 1\nend: nop", 3)
                .checkRegister("R0", 2));
        test.add(new ProcessorTest(command.toUpperCase() + " skip")
                .setRegister(flag, 1 - branchOn)
                .setRegister("R0", 2)
                .run(command + " end\n ldi r0, 1\nend: nop", 3)
                .checkRegister("R0", 1));
    }

    public void testOut(Test test) throws Exception {
        test.add(new ProcessorTest("OUT short")
                .setRegister("R1", 7)
                .run("out 3,R1", 1)
                .checkRegister("Reg3", 7));
        test.add(new ProcessorTest("OUT long")
                .setRegister("R1", 7)
                .run("out 32,R1", 2)
                .checkRegister("Reg32", 7));
        test.add(new ProcessorTest("OUTR")
                .setRegister("R0", 3)
                .setRegister("R1", 7)
                .run("outr [R0],R1", 1)
                .checkRegister("Reg3", 7));
    }

    public void testIn(Test test) throws Exception {
        test.add(new ProcessorTest("IN short")
                .setRegister("Reg3", 7)
                .run("in R1,3", 1)
                .checkRegister("R1", 7));
        test.add(new ProcessorTest("IN long")
                .setRegister("Reg32", 7)
                .run("in R1,32", 2)
                .checkRegister("R1", 7));
        test.add(new ProcessorTest("INR")
                .setRegister("R0", 3)
                .setRegister("Reg3", 7)
                .run("inr R1,[R0]", 1)
                .checkRegister("R1", 7));
    }

}
