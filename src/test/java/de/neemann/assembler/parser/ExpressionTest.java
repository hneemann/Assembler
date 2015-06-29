package de.neemann.assembler.parser;

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

    public void testSimple2() throws IOException, ParserException, ExpressionException {
        assertEquals(10, new Parser("2*(2+3)").getExpression().getValue(null));
    }

    public void testSimple3() throws IOException, ParserException, ExpressionException {
        assertEquals(0, new Parser("2 AND (2 OR 8) AND 8").getExpression().getValue(null));
        assertEquals(10, new Parser("2 AND 2 OR 8 AND 8").getExpression().getValue(null));
        assertEquals(10, new Parser("(2 AND 2) OR (8 AND 8)").getExpression().getValue(null));
    }


}
