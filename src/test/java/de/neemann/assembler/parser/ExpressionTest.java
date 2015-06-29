package de.neemann.assembler.parser;

import de.neemann.assembler.expression.Context;
import de.neemann.assembler.expression.ExpressionException;
import junit.framework.TestCase;

import java.io.IOException;

/**
 * @author hneemann
 */
public class ExpressionTest extends TestCase {

    public void testSimple() throws IOException, ParserException, ExpressionException {
        assertEquals(3, new Parser("1+2").getExpression().getValue(null));
    }

    public void testSimpleHex() throws IOException, ParserException, ExpressionException {
        assertEquals(255, new Parser("0xff").getExpression().getValue(null));
    }

    public void testSimpleBin() throws IOException, ParserException, ExpressionException {
        assertEquals(5, new Parser("0b101").getExpression().getValue(null));
    }

    public void testSimpleSub() throws IOException, ParserException, ExpressionException {
        assertEquals(1, new Parser("2-1").getExpression().getValue(null));
    }

    public void testSimple2() throws IOException, ParserException, ExpressionException {
        assertEquals(10, new Parser("2*(2+3)").getExpression().getValue(null));
    }

    public void testSimple3() throws IOException, ParserException, ExpressionException {
        assertEquals(0, new Parser("2 AND (2 OR 8) AND 8").getExpression().getValue(null));
        assertEquals(10, new Parser("2 AND 2 OR 8 AND 8").getExpression().getValue(null));
        assertEquals(10, new Parser("(2 AND 2) OR (8 AND 8)").getExpression().getValue(null));
    }

    public void testIdent() throws IOException, ParserException, ExpressionException {
        Context context = new Context().addIdentifier("A", 3);
        assertEquals(14, new Parser("2*(A+4)").getExpression().getValue(context));
    }

}
