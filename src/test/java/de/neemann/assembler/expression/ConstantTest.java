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
    }
}