package de.neemann.assembler.parser;

import de.neemann.assembler.asm.Program;
import de.neemann.assembler.docu.TestDocu;
import de.neemann.assembler.expression.ExpressionException;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;

public class IncludeTest extends TestCase {

    public void testInclude() throws IOException, ExpressionException, ParserException {
        File file = new File(TestDocu.getMavenRoot(), "src/test/resources/include/inc.asm");
        Program p = new Parser(file).parseProgram();
        assertEquals(3, p.getInstructionCount());
        assertEquals("LDI R0,0\n" +
                "LDI R1,1\n" +
                "LDI R2,2\n", p.toString());
    }

}
