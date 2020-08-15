package de.neemann.assembler.asm;

import de.neemann.assembler.asm.formatter.AsmFormatter;
import de.neemann.assembler.expression.ExpressionException;
import de.neemann.assembler.parser.Parser;
import de.neemann.assembler.parser.ParserException;
import junit.framework.TestCase;

import java.io.IOException;

/**
 * @author hneemann
 */
public class OptimizerShortTest extends TestCase {

    public void testOptimize() throws Exception {
        assertEquals(Opcode.LDSs, getFirstOpcode("LDS R0,0"));
        assertEquals(Opcode.LDSs, getFirstOpcode("LDS R0,1"));
        assertEquals(Opcode.LDSs, getFirstOpcode("LDS R0,0xf"));
        assertEquals(Opcode.LDS, getFirstOpcode("LDS R0,0x10"));

        assertEquals(Opcode.STSs, getFirstOpcode("STS 0,R0"));
        assertEquals(Opcode.STSs, getFirstOpcode("STS 1,R0"));
        assertEquals(Opcode.STSs, getFirstOpcode("STS 0xf,R0"));
        assertEquals(Opcode.STS, getFirstOpcode("STS 0x10,R0"));
    }

    private Opcode getFirstOpcode(String command) throws ExpressionException, InstructionException, IOException, ParserException {
        return new Parser(command).parseProgram().optimizeAndLink().traverse(new AsmFormatter(System.out)).getInstruction(0).getOpcode();
    }

}