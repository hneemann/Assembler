package de.neemann.assembler.parser;

import de.neemann.assembler.asm.Program;

import java.io.Reader;
import java.io.StringReader;

/**
 * @author hneemann
 */
public class Parser {
    public Parser(Reader in) {

    }

    public Parser(String source) {
        this(new StringReader(source));
    }

    public Program getProgram() {
        return null;
    }
}
