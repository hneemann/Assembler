package de.neemann.assembler.parser;

import de.neemann.assembler.asm.*;
import de.neemann.assembler.asm.formatter.AsmFormatter;
import de.neemann.assembler.asm.formatter.HexFormatter;
import de.neemann.assembler.expression.ExpressionException;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * @author hneemann
 */
public class ParserTest extends TestCase {


    public void testSimpleMov() throws IOException, ParserException, InstructionException, ExpressionException {
        Parser p = new Parser("MOV R0,R1");
        Program prog = p.parseProgram();

        assertEquals(1, prog.getInstructionCount());
        Instruction i = prog.getInstruction(0);
        assertEquals(Opcode.MOV, i.getOpcode());
        assertEquals(Register.R0, i.getDestReg());
        assertEquals(Register.R1, i.getSourceReg());
    }

    public void testSimpleMovComment() throws IOException, ParserException, InstructionException, ExpressionException {
        Parser p = new Parser("MOV R0,R1 ; Testcomment");
        Program prog = p.parseProgram();

        assertEquals(1, prog.getInstructionCount());
        Instruction i = prog.getInstruction(0);
        assertEquals(Opcode.MOV, i.getOpcode());
        assertEquals(Register.R0, i.getDestReg());
        assertEquals(Register.R1, i.getSourceReg());
    }


    public void testSimpleMovLabel() throws IOException, ParserException, InstructionException, ExpressionException {
        Parser p = new Parser("test: MOV R0,R1");
        Program prog = p.parseProgram();

        assertEquals(1, prog.getInstructionCount());
        Instruction i = prog.getInstruction(0);
        assertEquals(Opcode.MOV, i.getOpcode());
        assertEquals(Register.R0, i.getDestReg());
        assertEquals(Register.R1, i.getSourceReg());
        assertEquals("test", i.getLabel());
    }

    public void testSimpleTwoCommands() throws IOException, ParserException, InstructionException, ExpressionException {
        Parser p = new Parser("MOV R0,R1\nMOV R2,R3");
        Program prog = p.parseProgram();

        checkTwoMoves(prog);
    }

    private void checkTwoMoves(Program prog) {
        assertEquals(2, prog.getInstructionCount());
        Instruction i = prog.getInstruction(0);
        assertEquals(Opcode.MOV, i.getOpcode());
        assertEquals(Register.R0, i.getDestReg());
        assertEquals(Register.R1, i.getSourceReg());
        i = prog.getInstruction(1);
        assertEquals(Opcode.MOV, i.getOpcode());
        assertEquals(Register.R2, i.getDestReg());
        assertEquals(Register.R3, i.getSourceReg());
    }

    public void testSimpleTwoCommandsComment() throws IOException, ParserException, InstructionException, ExpressionException {
        Parser p = new Parser("MOV R0,R1;test\nMOV R2,R3");
        Program prog = p.parseProgram();

        checkTwoMoves(prog);
    }

    public void testSimpleTwoCommandsCommentLine() throws IOException, ParserException, InstructionException, ExpressionException {
        Parser p = new Parser("; test0\nMOV R0,R1;test\n ; test 2\nMOV R2,R3");
        Program prog = p.parseProgram();

        checkTwoMoves(prog);
    }

    public void testConstant() throws IOException, ParserException, InstructionException, ExpressionException {
        checkJmp(new Parser("JMP 12").parseProgram());
        checkJmp(new Parser("JMP 2*6").parseProgram());
        checkJmp(new Parser("JMP 12 ;test").parseProgram());
        checkJmp(new Parser("JMP 2*6; test").parseProgram());
    }

    private void checkJmp(Program prog) throws ExpressionException {
        assertEquals(1, prog.getInstructionCount());
        Instruction i = prog.getInstruction(0);
        assertEquals(Opcode.JMP, i.getOpcode());
        assertEquals(12, i.getConstant().getValue(null));
    }

    public void testLDI() throws IOException, ParserException, InstructionException, ExpressionException {
        Parser p = new Parser("LDI R0,5");
        Program prog = p.parseProgram();

        assertEquals(1, prog.getInstructionCount());
        Instruction i = prog.getInstruction(0);
        assertEquals(Opcode.LDI, i.getOpcode());
        assertEquals(5, i.getConstant().getValue(null));
    }

    public void testINC() throws IOException, ParserException, InstructionException, ExpressionException {
        Parser p = new Parser("INC R5");
        Program prog = p.parseProgram();

        assertEquals(1, prog.getInstructionCount());
        Instruction i = prog.getInstruction(0);
        assertEquals(Opcode.ADDIs, i.getOpcode());
        assertEquals(Register.R5, i.getDestReg());
    }

    public void testMetaWord() throws ExpressionException, ParserException, InstructionException, IOException {
        Program p = new Parser(".word A;var a\n.word b").parseProgram();
        assertEquals(0, p.getContext().get("A"));
        assertEquals(1, p.getContext().get("b"));
    }

    public void testMetaLong() throws ExpressionException, ParserException, InstructionException, IOException {
        Program p = new Parser(".long A;var a\n.long b").parseProgram();
        assertEquals(0, p.getContext().get("A"));
        assertEquals(2, p.getContext().get("b"));
    }

    public void testMetaConst() throws ExpressionException, ParserException, InstructionException, IOException {
        Program p = new Parser(".const A 2;var a\n.const b A*2+1").parseProgram();
        assertEquals(2, p.getContext().get("A"));
        assertEquals(5, p.getContext().get("b"));
    }

    public void testLabelCase() throws ExpressionException, ParserException, InstructionException, IOException {
        // make sure this does not throw an exception
        new Parser(
                "L1: mov r0,r1\n" +
                        "l1: mov r0,r1").parseProgram().optimizeAndLink();
    }

    public void testJmp() throws ExpressionException, ParserException, InstructionException, IOException {
        checkSelfJmp(new Parser("end: jmp end").parseProgram().optimizeAndLink());
    }

    private void checkSelfJmp(Program p) throws ExpressionException {
        assertEquals(1, p.getInstructionCount());
        Instruction i = p.getInstruction(0);
        assertEquals(Opcode.JMPs, i.getOpcode());
        assertEquals(0, i.getConstant().getValue(p.getContext()));
    }

    public void testJmp2() throws ExpressionException, ParserException, InstructionException, IOException {
        Program p = new Parser(".data test \"Test\";\nend: jmp end").parseProgram().optimizeAndLink();
        assertEquals(9, p.getInstructionCount());
        Instruction i = p.getInstruction(8);
        assertEquals(Opcode.JMPs, i.getOpcode());
        assertEquals(12, i.getConstant().getValue(p.getContext()));
    }

    public void testEmptyLabel() throws ExpressionException, ParserException, InstructionException, IOException {
        checkSelfJmp(new Parser("test:\njmp test").parseProgram().optimizeAndLink());
        checkSelfJmp(new Parser("test:  \njmp test").parseProgram().optimizeAndLink());
        checkSelfJmp(new Parser("test:  \n\njmp test").parseProgram().optimizeAndLink());
        checkSelfJmp(new Parser("test:  ;comment\njmp test").parseProgram().optimizeAndLink());
        checkSelfJmp(new Parser("test:  ;comment\n\njmp test").parseProgram().optimizeAndLink());
        try {
            checkSelfJmp(new Parser("hallo:\ntest:  ;comment\n\njmp test").parseProgram().optimizeAndLink());
            assertTrue(false);
        } catch (ExpressionException e) {
            assertTrue(true);
        }
    }

    public void testDataAddr() throws ExpressionException, ParserException, InstructionException, IOException {
        Program p = new Parser(".data test \"Test\",0\n" +
                ".data test2 \"Test\",0\n" +
                "jmp _ADDR_").parseProgram().optimizeAndLink();

        assertEquals(0, p.getContext().get("test"));
        assertEquals(5, p.getContext().get("test2"));
    }

    public void testRet() throws ExpressionException, ParserException, InstructionException, IOException {
        checkRet(3, 1, new Parser("RET").parseProgram().optimizeAndLink());
        checkRet(4, 1, new Parser("RET\nLDI R0,0").parseProgram().optimizeAndLink());
        checkRet(4, 1, new Parser("RET ; return\nLDI R0,0").parseProgram().optimizeAndLink());

        checkRet(3, 3, new Parser("RET 2").parseProgram().optimizeAndLink());
        checkRet(4, 3, new Parser("RET 2\nLDI R0,0").parseProgram().optimizeAndLink());
        checkRet(4, 3, new Parser("RET 2; return\nLDI R0,0").parseProgram().optimizeAndLink());
    }

    private void checkRet(int i, int pop, Program ret) throws ExpressionException {
        assertEquals(i, ret.getInstructionCount());
        Instruction instr = ret.getInstruction(1);
        assertEquals(Opcode.ADDIs, instr.getOpcode());
        assertEquals(pop, instr.getConstant().getValue(null));
    }

    public void testEOL() throws ExpressionException, ParserException, InstructionException, IOException {
        checkEOLException("\tLDI R0,5 R1");
        checkEOLException("\tLDI R0,5 R1\n");
        checkEOLException("\tLDI R0,5 A\n\tLDI R0,5");
        checkEOLException("\tBRK A");
        checkEOLException("\tBRK A\n");
        checkEOLException("\tBRK A\n\tLDI R0,5");
    }

    private void checkEOLException(String code) {
        try {
            new Parser(code).parseProgram();
            assertTrue(false);
        } catch (IOException | ParserException | ExpressionException e) {
            assertTrue(true);
        }
    }

    public void testPlus() throws ExpressionException, ParserException, InstructionException, IOException {
        assertEquals(5, new Parser("LDD R0,[R1+5]").parseProgram().optimizeAndLink().getInstruction(0).getConstant().getValue(null));
        assertEquals(-5, new Parser("LDD R0,[R1-5]").parseProgram().optimizeAndLink().getInstruction(0).getConstant().getValue(null));
    }

    public void testOrg() throws ParserException, IOException, ExpressionException, InstructionException {
        Program p = new Parser("LDI R0,0\n JMP A1\n.org 8\nA1: JMP A1").parseProgram().optimizeAndLink();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream ps = new PrintStream(baos);
        p.traverse(new HexFormatter(ps));
        ps.close();

        assertEquals("v2.0 raw\n"+
                "a00\n"+
                "3a06\n"+
                "0\n"+
                "0\n"+
                "0\n"+
                "0\n"+
                "0\n"+
                "0\n"+
                "3aff\n",baos.toString());
    }

    public void testOrgExc() throws ParserException, IOException, ExpressionException, InstructionException {
        try {
            new Parser("LDI R0,0\n JMP A1\n.org 0x0\nA1: JMP A1").parseProgram().optimizeAndLink();
            fail();
        } catch (ExpressionException e) {
            assertTrue(true);
        }
    }
}