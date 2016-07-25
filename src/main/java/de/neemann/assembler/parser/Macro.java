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

    /**
     * Creates a new macro
     *
     * @param name        the name
     * @param args        the arbuments
     * @param description the description
     */
    public Macro(String name, MnemonicArguments args, String description) {
        this.name = name;
        this.args = args;
        this.description = description;
    }

    /**
     * @return the name of the macro
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " " + args.toString() + "\n\t" + description;
    }

    /**
     * Parses the macro and adds the generated instructions to the program
     *
     * @param p      the program
     * @param name   the name of the macro
     * @param parser the parser
     * @throws IOException          IOException
     * @throws ParserException      ParserException
     * @throws InstructionException InstructionException
     * @throws ExpressionException  ExpressionException
     */
    abstract public void parseMacro(Program p, String name, Parser parser) throws IOException, ParserException, InstructionException, ExpressionException;
}
