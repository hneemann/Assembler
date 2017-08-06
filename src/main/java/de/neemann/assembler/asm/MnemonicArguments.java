package de.neemann.assembler.asm;

import de.neemann.assembler.parser.Parser;
import de.neemann.assembler.parser.ParserException;

import java.io.IOException;

/**
 * Describes possible instructions
 *
 * @author hneemann
 */
public abstract class MnemonicArguments {
    /**
     * Used for instruction with no aruments at all, nop, brk
     */
    public static final MnemonicArguments NOTHING = new Nothing();
    /**
     * Used for instruction which have only a source register
     */
    public static final MnemonicArguments SOURCE = new Source();
    /**
     * Used for instruction which have only a destination register
     */
    public static final MnemonicArguments DEST = new Dest();
    /**
     * Used for instruction which have only a constant as an argument like jmp
     */
    public static final Const CONST = new Const();
    /**
     * Used for instruction which have a source and a destination register
     */
    public static final MnemonicArguments DEST_SOURCE = new Comma(DEST, SOURCE);
    /**
     * Used for instruction which have a destination register and a constant
     */
    public static final MnemonicArguments DEST_CONST = new Comma(DEST, CONST);
    /**
     * Used for instruction which have a braced destination register and a source register
     */
    public static final MnemonicArguments BDEST_SOURCE = new Comma(new Brace(DEST), SOURCE);
    /**
     * Used for instruction which have a destination register and a braced source register
     */
    public static final MnemonicArguments DEST_BSOURCE = new Comma(DEST, new Brace(SOURCE));
    /**
     * Used for instruction which have a constant and a source register
     */
    public static final MnemonicArguments CONST_SOURCE = new Comma(CONST, SOURCE);
    /**
     * Used for instruction which have a destination register, a const and a source register
     */
    public static final MnemonicArguments BDEST_BCONST_SOURCE = new Comma(new Brace(new Plus(DEST, CONST)), SOURCE);
    /**
     * Used for instruction which have a destination register, a source register and a const and
     */
    public static final MnemonicArguments DEST_BSOURCE_BCONST = new Comma(DEST, new Brace(new Plus(SOURCE, CONST)));


    private final boolean hasSource;
    private final boolean hasDest;
    private final boolean hasConst;

    private MnemonicArguments(boolean hasSource, boolean hasDest, boolean hasConst) {
        this.hasSource = hasSource;
        this.hasDest = hasDest;
        this.hasConst = hasConst;
    }

    /**
     * @return true if there is a source register
     */
    public boolean hasSource() {
        return hasSource;
    }

    /**
     * @return true if there is a destination register
     */
    public boolean hasDest() {
        return hasDest;
    }

    /**
     * @return true if there is a constant
     */
    public boolean hasConst() {
        return hasConst;
    }

    /**
     * Formats an instruction
     *
     * @param i the instruction
     * @return the representing string
     */
    public abstract String format(Instruction i);

    /**
     * Parses the arguments descriped by this {@link MnemonicArguments} instance.
     *
     * @param i the instruction builder to use
     * @param p the pareser to read the tokens from
     * @return the instruction builder for chanied calls
     * @throws IOException          IOException
     * @throws ParserException      ParserException
     * @throws InstructionException InstructionException
     */
    public abstract InstructionBuilder parse(InstructionBuilder i, Parser p) throws IOException, ParserException, InstructionException;

    private static final class Nothing extends MnemonicArguments {

        private Nothing() {
            super(false, false, false);
        }

        @Override
        public String toString() {
            return "";
        }

        @Override
        public String format(Instruction i) {
            return "";
        }

        @Override
        public InstructionBuilder parse(InstructionBuilder i, Parser p) {
            return i;
        }
    }

    private static final class Source extends MnemonicArguments {
        private Source() {
            super(true, false, false);
        }

        @Override
        public String toString() {
            return "Rs";
        }

        @Override
        public String format(Instruction i) {
            return i.getSourceReg().name();
        }

        @Override
        public InstructionBuilder parse(InstructionBuilder i, Parser p) throws IOException, ParserException, InstructionException {
            i.setSource(p.parseReg());
            return i;
        }
    }

    private static final class Dest extends MnemonicArguments {
        private Dest() {
            super(false, true, false);
        }

        @Override
        public String toString() {
            return "Rd";
        }

        @Override
        public String format(Instruction i) {
            return i.getDestReg().name();
        }

        @Override
        public InstructionBuilder parse(InstructionBuilder i, Parser p) throws IOException, ParserException, InstructionException {
            i.setDest(p.parseReg());
            return i;
        }
    }

    private static final class Const extends MnemonicArguments {
        private Const() {
            super(false, false, true);
        }

        @Override
        public String toString() {
            return "[const]";
        }

        @Override
        public String format(Instruction i) {
            return i.getConstant().toString();
        }

        @Override
        public InstructionBuilder parse(InstructionBuilder i, Parser p) throws IOException, ParserException, InstructionException {
            i.setConstant(p.parseExpression());
            return i;
        }
    }

    private static final class Brace extends MnemonicArguments {
        private final MnemonicArguments inner;

        private Brace(MnemonicArguments inner) {
            super(inner.hasSource, inner.hasDest, inner.hasConst);
            this.inner = inner;
        }

        @Override
        public String toString() {
            return "[" + inner + "]";
        }

        @Override
        public String format(Instruction i) {
            return "[" + inner.format(i) + "]";
        }

        @Override
        public InstructionBuilder parse(InstructionBuilder i, Parser p) throws IOException, ParserException, InstructionException {
            p.consume('[');
            inner.parse(i, p);
            p.consume(']');
            return i;
        }
    }

    //class can not be final, bug in checkstyle. See https://github.com/checkstyle/checkstyle/issues/4037
    //CHECKSTYLE.OFF: FinalClass
    private static class Concat extends MnemonicArguments {
        final MnemonicArguments before;
        private final char c;
        final MnemonicArguments after;

        private Concat(MnemonicArguments before, char c, MnemonicArguments after) {
            super(before.hasSource || after.hasSource,
                    before.hasDest || after.hasDest,
                    before.hasConst || after.hasConst);
            this.before = before;
            this.c = c;
            this.after = after;
        }

        @Override
        public String toString() {
            return before.toString() + c + after.toString();
        }

        @Override
        public String format(Instruction i) {
            return before.format(i) + c + after.format(i);
        }

        @Override
        public InstructionBuilder parse(InstructionBuilder i, Parser p) throws IOException, ParserException, InstructionException {
            before.parse(i, p);
            p.consume(c);
            after.parse(i, p);
            return i;
        }
    }

    private static final class Comma extends Concat {
        private Comma(MnemonicArguments before, MnemonicArguments after) {
            super(before, ',', after);
        }
    }

    private static final class Plus extends Concat {
        private Plus(MnemonicArguments before, Const after) {
            super(before, '+', after);
        }

        @Override
        public InstructionBuilder parse(InstructionBuilder i, Parser p) throws IOException, ParserException, InstructionException {
            before.parse(i, p);
            if (p.isNext('-')) {
                after.parse(i, p);
                i.negConstant();
            } else {
                p.consume('+');
                after.parse(i, p);
            }
            return i;
        }

    }
    //CHECKSTYLE.ON: FinalClass

}
