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
public class OptimizerJmpTest extends TestCase {

    public void testOptimize() throws Exception {
        assertEquals(Opcode.JMPs, getFirstOpcode("jmp 127"));
        assertEquals(Opcode.JMPs, getFirstOpcode("jmp 128"));
        assertEquals(Opcode.JMP, getFirstOpcode("jmp 129"));

        assertEquals(Opcode.JMPs, getFirstOpcode("jmp -126"));
        assertEquals(Opcode.JMPs, getFirstOpcode("jmp -127"));
        assertEquals(Opcode.JMP, getFirstOpcode("jmp -128"));
    }

    private Opcode getFirstOpcode(String command) throws ExpressionException, InstructionException, IOException, ParserException {
        return new Parser(command).parseProgram().optimizeAndLink().traverse(new AsmFormatter(System.out)).getInstruction(0).getOpcode();
    }

}