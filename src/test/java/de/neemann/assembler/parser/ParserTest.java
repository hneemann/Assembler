package de.neemann.assembler.parser;

import de.neemann.assembler.asm.*;
import junit.framework.TestCase;

import java.io.IOException;

/**
 * @author hneemann
 */
public class ParserTest extends TestCase {


    public void testSimpleMov() throws IOException, ParserException, InstructionException {
        Parser p = new Parser("MOV R0,R1");
        Program prog = p.getProgram();

        assertEquals(1, prog.getInstructionCount());
        Instruction i = prog.getInstruction(0);
        assertEquals(Opcode.MOV, i.getOpcode());
        assertEquals(Register.R0, i.getDestReg());
        assertEquals(Register.R1, i.getSourceReg());
    }

    public void testSimpleMovComment() throws IOException, ParserException, InstructionException {
        Parser p = new Parser("MOV R0,R1 ; Testcomment");
        Program prog = p.getProgram();

        assertEquals(1, prog.getInstructionCount());
        Instruction i = prog.getInstruction(0);
        assertEquals(Opcode.MOV, i.getOpcode());
        assertEquals(Register.R0, i.getDestReg());
        assertEquals(Register.R1, i.getSourceReg());
    }


    public void testSimpleMovLabel() throws IOException, ParserException, InstructionException {
        Parser p = new Parser("test: MOV R0,R1");
        Program prog = p.getProgram();

        assertEquals(1, prog.getInstructionCount());
        Instruction i = prog.getInstruction(0);
        assertEquals(Opcode.MOV, i.getOpcode());
        assertEquals(Register.R0, i.getDestReg());
        assertEquals(Register.R1, i.getSourceReg());
        assertEquals("test", i.getLabel());
    }

    public void testSimpleTwoCommands() throws IOException, ParserException, InstructionException {
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

    public void testSimpleTwoCommandsComment() throws IOException, ParserException, InstructionException {
        Parser p = new Parser("MOV R0,R1;test\nMOV R2,R3");
        Program prog = p.getProgram();

        checkTwoMoves(prog);
    }

    public void testSimpleTwoCommandsCommentLine() throws IOException, ParserException, InstructionException {
        Parser p = new Parser("; test0\nMOV R0,R1;test\n ; test 2\nMOV R2,R3");
        Program prog = p.getProgram();

        checkTwoMoves(prog);
    }


}