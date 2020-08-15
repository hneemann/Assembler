package de.neemann.assembler.parser;

import de.neemann.assembler.asm.InstructionException;
import de.neemann.assembler.asm.Program;
import de.neemann.assembler.asm.formatter.HexFormatter;
import de.neemann.assembler.expression.ExpressionException;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by hneemann on 19.03.17.
 */
public class VonNeumannTest extends TestCase {

    public void testHarvard() throws IOException, ParserException, InstructionException, ExpressionException {
        Parser p = new Parser(".data text \"AA\",0\n.word test\nLDI R0,text\nLDI R0,test");
        assertEquals("v2.0 raw\n" +
                "a00\n" +
                "2c20\n" +
                "8041\n" +
                "900\n" +
                "2c00\n" +
                "2c10\n" +
                "a00\n" +
                "a03\n", getHex(p));
    }

    public void testVonNeumann() throws IOException, ParserException, InstructionException, ExpressionException {
        Parser p = new Parser(".dorg 0x8000\n.data text \"AA\",0\n.word test\nLDI R0,text\nLDI R0,test");
        assertEquals("v2.0 raw\n" +
                "41\n" +
                "41\n" +
                "0\n" +
                "a00\n" +
                "8000\n" +
                "901\n", getHex(p));
    }

    public void testVonNeumannError() throws IOException, ParserException, InstructionException, ExpressionException {
        Parser p = new Parser(".data text \"AA\",0\n.word test\n.dorg 0x8000\nLDI R0,text\nLDI R0,test");
        try {
            getHex(p);
            fail();
        } catch (ExpressionException e) {

        }
    }

    private String getHex(Parser p) throws InstructionException, ExpressionException, IOException, ParserException {
        Program prog = p.parseProgram().optimizeAndLink();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final HexFormatter hexCollector = new HexFormatter(new PrintStream(baos));
        prog.traverse(hexCollector);
        return baos.toString();
    }

}
