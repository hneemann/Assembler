package de.neemann.assembler.expression;

import junit.framework.TestCase;

/**
 * @author hneemann
 */
public class ConstantTest extends TestCase {

    public void testGetValue() throws Exception {
        assertEquals(0x41, new Constant('A').getValue(null));
        assertEquals(0x41, new Constant((int) 'A').getValue(null));
    }

    public void testToString() throws Exception {
        assertEquals("'A'", new Constant('A').toString());
        assertEquals("65", new Constant((int) 'A').toString());
        assertEquals("'\\n'", new Constant('\n').toString());
        assertEquals("'\\r'", new Constant('\r').toString());
        assertEquals("'\\t'", new Constant('\t').toString());
        assertEquals("'\\b'", new Constant('\b').toString());
        assertEquals("'\"'", new Constant('"').toString());
        assertEquals("'\\'", new Constant('\\').toString());
        assertEquals("'\\\''", new Constant('\'').toString());
    }
}