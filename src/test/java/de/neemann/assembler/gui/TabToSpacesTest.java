package de.neemann.assembler.gui;

import junit.framework.TestCase;

/**
 * @author hneemann
 */
public class TabToSpacesTest extends TestCase {

    public void testConvert() throws Exception {
        assertEquals("      LDI R0,1\nl1:   BRK", new TabToSpaces("\tLDI R0,1\nl1:\tBRK", 6).convert());
        assertEquals("      LDI R0,1    ;test\nl1:   BRK", new TabToSpaces("\tLDI R0,1\t;test\nl1:\tBRK", 6).convert());
        assertEquals("        LDI R0,1\nl1:     BRK", new TabToSpaces("\tLDI R0,1\nl1:\tBRK", 8).convert());
    }
}