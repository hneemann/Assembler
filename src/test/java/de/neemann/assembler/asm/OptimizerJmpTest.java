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
public class OptimizerJmpTest extends TestCase {

    public void testOptimize() throws Exception {
        assertEquals(Opcode.JMPs, getFirstOpcode("jmp 255"));
        assertEquals(Opcode.JMPs, getFirstOpcode("jmp 256"));
        assertEquals(Opcode.JMP, getFirstOpcode("jmp 257"));

        assertEquals(Opcode.JMPs, getFirstOpcode("jmp -254"));
        assertEquals(Opcode.JMPs, getFirstOpcode("jmp -255"));
        assertEquals(Opcode.JMP, getFirstOpcode("jmp -256"));
    }

    private Opcode getFirstOpcode(String command) throws ExpressionException, InstructionException, IOException, ParserException {
        return new Parser(command).getProgram().optimizeAndLink().traverse(new AsmLightFormatter(System.out)).getInstruction(0).getOpcode();
    }

}