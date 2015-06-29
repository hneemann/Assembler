package de.neemann.assembler.parser;

import de.neemann.assembler.asm.*;
import de.neemann.assembler.expression.ExpressionException;
import junit.framework.TestCase;

import java.io.IOException;

/**
 * @author hneemann
 */
public class ParserTest extends TestCase {


    public void testSimpleMov() throws IOException, ParserException, InstructionException, ExpressionException {
        Parser p = new Parser("MOV R0,R1");
        Program prog = p.getProgram();

        assertEquals(1, prog.getInstructionCount());
        Instruction i = prog.getInstruction(0);
        assertEquals(Opcode.MOV, i.getOpcode());
        assertEquals(Register.R0, i.getDestReg());
        assertEquals(Register.R1, i.getSourceReg());
    }

    public void testSimpleMovComment() throws IOException, ParserException, InstructionException, ExpressionException {
        Parser p = new Parser("MOV R0,R1 ; Testcomment");
        Program prog = p.getProgram();

        assertEquals(1, prog.getInstructionCount());
        Instruction i = prog.getInstruction(0);
        assertEquals(Opcode.MOV, i.getOpcode());
        assertEquals(Register.R0, i.getDestReg());
        assertEquals(Register.R1, i.getSourceReg());
    }


    public void testSimpleMovLabel() throws IOException, ParserException, InstructionException, ExpressionException {
        Parser p = new Parser("test: MOV R0,R1");
        Program prog = p.getProgram();

        assertEquals(1, prog.getInstructionCount());
        Instruction i = prog.getInstruction(0);
        assertEquals(Opcode.MOV, i.getOpcode());
        assertEquals(Register.R0, i.getDestReg());
        assertEquals(Register.R1, i.getSourceReg());
        assertEquals("test", i.getLabel());
    }

    public void testSimpleTwoCommands() throws IOException, ParserException, InstructionException, ExpressionException {
        Parser p = new Parser("MOV R0,R1\nMOV R2,R3");
        Program prog = p.getProgram();

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
        Program prog = p.getProgram();

        checkTwoMoves(prog);
    }

    public void testSimpleTwoCommandsCommentLine() throws IOException, ParserException, InstructionException, ExpressionException {
        Parser p = new Parser("; test0\nMOV R0,R1;test\n ; test 2\nMOV R2,R3");
        Program prog = p.getProgram();

        checkTwoMoves(prog);
    }

    public void testConstant() throws IOException, ParserException, InstructionException, ExpressionException {
        checkJmp(new Parser("JMP 12").getProgram());
        checkJmp(new Parser("JMP 2*6").getProgram());
        checkJmp(new Parser("JMP 12 ;test").getProgram());
        checkJmp(new Parser("JMP 2*6; test").getProgram());
    }

    private void checkJmp(Program prog) throws ExpressionException {
        assertEquals(1, prog.getInstructionCount());
        Instruction i = prog.getInstruction(0);
        assertEquals(Opcode.JMP, i.getOpcode());
        assertEquals(12, i.getConstant().getValue(null));
    }

    public void testLDI() throws IOException, ParserException, InstructionException, ExpressionException {
        Parser p = new Parser("LDI R0,5");
        Program prog = p.getProgram();

        assertEquals(1, prog.getInstructionCount());
        Instruction i = prog.getInstruction(0);
        assertEquals(Opcode.LDI, i.getOpcode());
        assertEquals(5, i.getConstant().getValue(null));
    }

    public void testINC() throws IOException, ParserException, InstructionException, ExpressionException {
        Parser p = new Parser("INC R5");
        Program prog = p.getProgram();

        assertEquals(1, prog.getInstructionCount());
        Instruction i = prog.getInstruction(0);
        assertEquals(Opcode.INC, i.getOpcode());
        assertEquals(Register.R5, i.getDestReg());
    }

    public void testMetaWord() throws ExpressionException, ParserException, InstructionException, IOException {
        Program p = new Parser(".word A;var a\n.word b").getProgram();
        assertEquals(0, p.getContext().get("A"));
        assertEquals(1, p.getContext().get("b"));
    }

    public void testMetaLong() throws ExpressionException, ParserException, InstructionException, IOException {
        Program p = new Parser(".long A;var a\n.long b").getProgram();
        assertEquals(0, p.getContext().get("A"));
        assertEquals(2, p.getContext().get("b"));
    }

    public void testMetaConst() throws ExpressionException, ParserException, InstructionException, IOException {
        Program p = new Parser(".const A 2;var a\n.const b A*2+1").getProgram();
        assertEquals(2, p.getContext().get("A"));
        assertEquals(5, p.getContext().get("b"));
    }

}