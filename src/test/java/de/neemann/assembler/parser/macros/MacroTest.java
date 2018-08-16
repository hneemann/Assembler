package de.neemann.assembler.parser.macros;

import de.neemann.assembler.asm.Program;
import de.neemann.assembler.parser.Parser;
import junit.framework.TestCase;

/**
 * @author hneemann
 */
public class MacroTest extends TestCase {

    public void testCall() throws Exception {
        Program p = new Parser("call a").parseProgram();
        assertEquals("SUBIs SP,1\n" +
                "LDI RA,_SKIP2_ADDR_\n" +
                "ST [SP],RA\n" +
                "JMP a\n", p.toString());
    }

    public void testDec() throws Exception {
        Program p = new Parser("dec r0").parseProgram();
        assertEquals("SUBIs R0,1\n", p.toString());
    }

    public void testInc() throws Exception {
        Program p = new Parser("inc r0").parseProgram();
        assertEquals("ADDIs R0,1\n", p.toString());
    }

    public void testEnter() throws Exception {
        Program p = new Parser("enter 1").parseProgram();
        assertEquals("SUBIs SP,1\n" +
                "ST [SP],BP\n" +
                "MOV BP,SP\n" +
                "SUBI SP,1\n", p.toString());
    }

    public void testLeave() throws Exception {
        Program p = new Parser("leave").parseProgram();
        assertEquals("MOV SP,BP\n" +
                "LD BP,[SP]\n" +
                "ADDIs SP,1\n", p.toString());
    }

    public void testPop() throws Exception {
        Program p = new Parser("pop r0").parseProgram();
        assertEquals("LD R0,[SP]\n" +
                "ADDIs SP,1\n", p.toString());
    }

    public void testPush() throws Exception {
        Program p = new Parser("push r0").parseProgram();
        assertEquals("SUBIs SP,1\n" +
                "ST [SP],R0\n", p.toString());
    }

    public void testRet() throws Exception {
        Program p = new Parser("ret").parseProgram();
        assertEquals("LD RA,[SP]\n" +
                "ADDIs SP,1\n" +
                "RRET RA\n", p.toString());
        p = new Parser("ret 3").parseProgram();
        assertEquals("LD RA,[SP]\n" +
                "ADDI SP,3+1\n" +
                "RRET RA\n", p.toString());
    }

    public void testSCall() throws Exception {
        Program p = new Parser("_scall a").parseProgram();
        assertEquals("SUBIs SP,1\n" +
                "ST [SP],RA\n" +
                "RCALL RA,a\n" +
                "LD RA,[SP]\n" +
                "ADDIs SP,1\n", p.toString());
    }

    public void testEnterI() throws Exception {
        Program p = new Parser("enteri").parseProgram();
        assertEquals("STD [SP+-1],R0\n" +
                "IN R0,0\n" +
                "STD [SP+-2],R0\n" +
                "SUBIs SP,2\n", p.toString());
    }

    public void testLeaveI() throws Exception {
        Program p = new Parser("leavei").parseProgram();
        assertEquals("ADDIs SP,2\n" +
                "LDD R0,[SP+-2]\n" +
                "OUT 0,R0\n" +
                "LDD R0,[SP+-1]\n", p.toString());
    }

}