package de.neemann.assembler.parser;

import de.neemann.assembler.asm.InstructionException;
import de.neemann.assembler.asm.Program;
import de.neemann.assembler.expression.ExpressionException;

import java.io.IOException;

/**
 * @author hneemann
 */
public interface Macro {
    String getName();

    void parseMacro(Program p, String name, Parser parser) throws IOException, ParserException, InstructionException, ExpressionException;
}
