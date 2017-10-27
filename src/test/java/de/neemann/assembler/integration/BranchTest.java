package de.neemann.assembler.integration;

import de.neemann.assembler.asm.InstructionException;
import de.neemann.assembler.asm.Program;
import de.neemann.assembler.asm.formatter.HexFormatter;
import de.neemann.assembler.expression.ExpressionException;
import de.neemann.assembler.parser.Parser;
import de.neemann.assembler.parser.ParserException;
import junit.framework.TestCase;

import java.io.*;

public class BranchTest extends TestCase {

    public void testBranchOutOfRangeWithLineNumber() throws ExpressionException, ParserException, InstructionException, IOException {
        OutputStream baos = new ByteArrayOutputStream();
        try {
            new Parser("\n\n.data test \"Test\",0\n" +
                    "breq 0xfff").parseProgram().optimizeAndLink().traverse(new HexFormatter(new PrintStream(baos)));
            fail();
        } catch (ExpressionException e) {
            assertTrue(e.getMessage().contains("line 4"));
        }
    }
}
