package de.neemann.assembler.asm;

import de.neemann.assembler.asm.formatter.AsmLightFormatter;
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
        assertEquals(Opcode.LDSs, getFirstOpcode("LDS R0,0x1f"));
        assertEquals(Opcode.LDS, getFirstOpcode("LDS R0,0x20"));

        assertEquals(Opcode.STSs, getFirstOpcode("STS R0,0"));
        assertEquals(Opcode.STSs, getFirstOpcode("STS R0,1"));
        assertEquals(Opcode.STSs, getFirstOpcode("STS R0,0x1f"));
        assertEquals(Opcode.STS, getFirstOpcode("STS R0,0x20"));
    }

    private Opcode getFirstOpcode(String command) throws ExpressionException, InstructionException, IOException, ParserException {
        return new Parser(command).parseProgram().optimizeAndLink().traverse(new AsmLightFormatter(System.out)).getInstruction(0).getOpcode();
    }

}