package de.neemann.assembler.parser;

import de.neemann.assembler.asm.InstructionException;
import de.neemann.assembler.asm.MnemonicArguments;
import de.neemann.assembler.asm.Program;
import de.neemann.assembler.expression.ExpressionException;

import java.io.IOException;

/**
 * @author hneemann
 */
public abstract class Macro {
    private String name;
    private final MnemonicArguments args;
    private final String description;

    public Macro(String name, MnemonicArguments args, String description) {
        this.name = name;
        this.args = args;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name + " " + args.toString() + "\n\t" + description;
    }

    abstract public void parseMacro(Program p, String name, Parser parser) throws IOException, ParserException, InstructionException, ExpressionException;
}
