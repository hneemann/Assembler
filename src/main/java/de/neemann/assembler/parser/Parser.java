package de.neemann.assembler.parser;

import de.neemann.assembler.asm.*;
import de.neemann.assembler.expression.*;
import de.neemann.assembler.parser.macros.*;

import java.io.*;
import java.util.HashMap;

import static java.io.StreamTokenizer.*;

/**
 * @author hneemann
 */
public class Parser implements Closeable {

    private final StreamTokenizer tokenizer;
    private final Reader in;
    private static final HashMap<String, Macro> MACROS = new HashMap<>();
    private static final HashMap<String, Directive> DIRECTIVES = new HashMap<>();
    private final HashMap<String, Register> regsMap;
    private File baseFile;

    static {
        addMacro(new Inc());
        addMacro(new Dec());
        addMacro(new Push());
        addMacro(new Pop());
        addMacro(new SCall());
        addMacro(new Ret());
        addMacro(new Call());
        addMacro(new Enter());
        addMacro(new Leave());
        addMacro(new EnterISR());
        addMacro(new LeaveISR());

        addDirective(new DReg());
        addDirective(new DWord());
        addDirective(new DLong());
        addDirective(new DOrg());
        addDirective(new DDOrg());
        addDirective(new DData());
        addDirective(new DConst());
        addDirective(new DInclude());
    }

    /**
     * @return the available macros
     */
    public static Iterable<Macro> getMacros() {
        return MACROS.values();
    }

    /**
     * @return the available directives
     */
    public static Iterable<Directive> getDirectives() {
        return DIRECTIVES.values();
    }

    private static void addMacro(Macro m) {
        MACROS.put(m.getName().toLowerCase(), m);
    }

    private static void addDirective(Directive d) {
        DIRECTIVES.put(d.getName().toLowerCase(), d);
    }


    /**
     * Creates a new instance
     *
     * @param source the source
     */
    public Parser(String source) {
        this(new StringReader(source));
    }

    /**
     * Creates a new instance
     *
     * @param file the source file
     * @throws IOException IOException
     */
    public Parser(File file) throws IOException {
        this(new InputStreamReader(new FileInputStream(file), "utf-8"));
        baseFile = file;
    }

    /**
     * Creates a new instance
     *
     * @param in the reader to read the source code
     */
    public Parser(Reader in) {
        this.in = in;

        this.regsMap = new HashMap<>();

        tokenizer = new StreamTokenizer(in);
        tokenizer.eolIsSignificant(true);
        tokenizer.ordinaryChar('-');
        tokenizer.ordinaryChar('.');
        tokenizer.ordinaryChar('/');
        tokenizer.ordinaryChars('0', '9');
        tokenizer.wordChars('0', '9');
        tokenizer.wordChars('.', '.');
        tokenizer.wordChars('_', '_');
        tokenizer.commentChar(';');
        tokenizer.slashStarComments(true);

    }

    /**
     * Parses the program
     *
     * @return the program
     * @throws IOException         IOException
     * @throws ParserException     ParserException
     * @throws ExpressionException ExpressionException
     */
    public Program parseProgram() throws IOException, ParserException, ExpressionException {
        return parseProgram(new Program());
    }

    private Program parseProgram(Program p) throws IOException, ParserException, ExpressionException {
        try {
            WHILE:
            while (true) {
                switch (tokenizer.nextToken()) {
                    case TT_WORD:
                        String word = tokenizer.sval;
                        if (word.startsWith(".")) {
                            parseMetaCommand(p, word);
                        } else {
                            boolean isCommand = true;
                            if (Opcode.parseStr(word) == null && !MACROS.containsKey(word.toLowerCase())) {
                                p.setPendingLabel(word);
                                consume(':');
                                if (isNext(TT_WORD))
                                    word = tokenizer.sval;
                                else
                                    isCommand = false;
                            }
                            if (isCommand) {
                                p.setLineNumber(tokenizer.lineno());
                                if (MACROS.containsKey(word.toLowerCase())) {
                                    MACROS.get(word.toLowerCase()).parseMacro(p, word, this);
                                } else
                                    parseInstruction(p, word);
                            }
                        }
                        switch (tokenizer.nextToken()) {
                            case TT_EOF:
                            case TT_EOL:
                                break;
                            default:
                                throw makeParserException("unexpected token " + tokenizer);
                        }
                        break;
                    case TT_EOL:
                        break;
                    case TT_EOF:
                        break WHILE;
                    default:
                        throw makeParserException("unexpected token '" + tokenizer + "'");
                }
            }
        } catch (InstructionException e) {
            throw makeParserException(e.getMessage());
        } catch (ExpressionException e) {
            e.setLineNumber(getLineNumber());
            throw e;
        }

        return p;
    }

    private void parseMetaCommand(Program p, String t) throws IOException, ParserException, ExpressionException {
        p.addPendingComment("\n " + t);
        Directive dir = DIRECTIVES.get(t);
        if (dir == null)
            throw makeParserException("unknown assembler directive " + t);

        dir.doWork(this, p);
    }

    private void readData(Program p) throws ExpressionException, IOException, ParserException {
        if (isNext('"')) {
            String text = tokenizer.sval;
            for (int i = 0; i < text.length(); i++)
                p.addData(text.charAt(i));
            p.addPendingComment(" \"" + escapeText(text) + "\"");
        } else {
            int value = parseExpression().getValue(p.getContext());
            p.addPendingComment(" " + value);
            p.addData(value);
        }
    }

    private String escapeText(String text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= 32)
                sb.append(c);
            else {
                switch (c) {
                    case '\n':
                        sb.append("\\n");
                        break;
                    case '\r':
                        sb.append("\\r");
                        break;
                    case '\t':
                        sb.append("\\t");
                        break;
                    default:
                        sb.append("\\u").append(Integer.toHexString(c));
                        break;
                }
            }
        }
        return sb.toString();
    }

    private void parseInstruction(Program p, String t) throws IOException, ParserException, InstructionException {
        Opcode opcode = Opcode.parseStr(t);
        if (opcode == null)
            throw makeParserException("opcode expected, found '" + t + "'");

        Instruction i = opcode.getArguments().parse(new InstructionBuilder(opcode), this).build();

        if (i == null)
            throw makeParserException("illegal state: No opcode");

        p.add(i);
    }

    /**
     * Consume the given token
     *
     * @param c the token
     * @throws IOException     IOException
     * @throws ParserException ParserException
     */
    public void consume(int c) throws IOException, ParserException {
        if (tokenizer.nextToken() != c)
            throw makeParserException("expected '" + (char) c + "', found '" + tokenizer + "'");
    }

    /**
     * Parse a register
     *
     * @return the register found
     * @throws IOException     IOException
     * @throws ParserException ParserException
     */
    public Register parseReg() throws IOException, ParserException {
        String r = parseWord();
        Register reg = Register.parseStr(r);
        if (reg != null)
            return reg;

        reg = regsMap.get(r);
        if (reg != null)
            return reg;

        throw makeParserException("expected a register, found '" + r + "'");
    }

    /**
     * Parse a word ore identifier
     *
     * @return the identifier
     * @throws IOException     IOException
     * @throws ParserException ParserException
     */
    public String parseWord() throws IOException, ParserException {
        int t = tokenizer.nextToken();
        if (t != TT_WORD)
            throw makeParserException("unexpected number or EOL/EOF!");
        return tokenizer.sval;
    }

    /**
     * Creates a parser exception
     *
     * @param message the message
     * @return the exception
     */
    public ParserException makeParserException(String message) {
        return new ParserException(message, tokenizer.lineno());
    }


    @Override
    public void close() throws IOException {
        in.close();
    }

    /**
     * Checks if the next word is str
     *
     * @param str the word to check
     * @return true if str is found
     * @throws IOException IOException
     */
    public boolean isNext(String str) throws IOException {
        int t = tokenizer.nextToken();
        if (t == TT_WORD && tokenizer.sval.equalsIgnoreCase(str))
            return true;

        tokenizer.pushBack();
        return false;
    }

    /**
     * Checks if the next token is c
     *
     * @param c the expected token
     * @return true if token is found
     * @throws IOException IOException
     */
    public boolean isNext(int c) throws IOException {
        int t = tokenizer.nextToken();
        if (t == c)
            return true;

        tokenizer.pushBack();
        return false;
    }

    /**
     * @return true is next token is EOL
     * @throws IOException IOException
     */
    public boolean isEOL() throws IOException {
        int t = tokenizer.nextToken();
        tokenizer.pushBack();
        return t == TT_EOL || t == TT_EOF;
    }

    /**
     * Parses a single expression. An EOF is expected!
     *
     * @return the expression
     * @throws IOException     IOException
     * @throws ParserException ParserException
     */
    public Expression getExpression() throws IOException, ParserException {
        Expression exp = parseExpression();
        if (tokenizer.nextToken() != TT_EOF)
            throw makeParserException("no EOF found, but " + tokenizer);
        return exp;
    }

    /**
     * Parses a single expression
     *
     * @return the expression
     * @throws IOException     IOException
     * @throws ParserException ParserException
     */
    public Expression parseExpression() throws IOException, ParserException {
        Expression ex = parseAnd();
        while (isNext("or")) {
            ex = new Operate(ex, Operate.Operation.OR, parseAnd());
        }
        return ex;
    }


    private Expression parseAnd() throws IOException, ParserException {
        Expression ex = parseXOR();
        while (isNext("and")) {
            ex = new Operate(ex, Operate.Operation.AND, parseXOR());
        }
        return ex;
    }

    private Expression parseXOR() throws IOException, ParserException {
        Expression ex = parseADD();
        while (isNext("xor")) {
            ex = new Operate(ex, Operate.Operation.XOR, parseADD());
        }
        return ex;
    }

    private Expression parseADD() throws IOException, ParserException {
        Expression ex = parseSUB();
        while (isNext('+')) {
            ex = new Operate(ex, Operate.Operation.ADD, parseSUB());
        }
        return ex;
    }

    private Expression parseSUB() throws IOException, ParserException {
        Expression ex = parseMUL();
        while (isNext('-')) {
            ex = new Operate(ex, Operate.Operation.SUB, parseMUL());
        }
        return ex;
    }

    private Expression parseMUL() throws IOException, ParserException {
        Expression ex = parseDIV();
        while (isNext('*')) {
            ex = new Operate(ex, Operate.Operation.MUL, parseDIV());
        }
        return ex;
    }

    private Expression parseDIV() throws IOException, ParserException {
        Expression ex = parseValue();
        while (isNext('/')) {
            ex = new Operate(ex, Operate.Operation.DIV, parseValue());
        }
        return ex;
    }

    private Expression parseValue() throws IOException, ParserException {
        switch (tokenizer.nextToken()) {
            case TT_WORD:
                String s = tokenizer.sval;
                char c = s.charAt(0);
                if (c >= '0' && c <= '9') {
                    return new Constant(parseInteger(s.toLowerCase()));
                } else
                    return new Identifier(tokenizer.sval);
            case '\'':
                String str = tokenizer.sval;
                if (str.length() != 1)
                    throw makeParserException("only a single char allowed, not '" + str + "'");
                return new Constant(str.charAt(0));
            case '(':
                Expression ex = parseExpression();
                consume(')');
                return ex;
            case '-':
                return new Neg(parseExpression());
            case '~':
                return new Not(parseExpression());
            default:
                throw makeParserException("unexpected token " + tokenizer);
        }
    }

    private int parseInteger(String s) throws ParserException {
        try {
            if (s.startsWith("0b"))
                return Integer.parseInt(s.substring(2), 2);
            else if (s.startsWith("0x"))
                return Integer.parseInt(s.substring(2), 16);
            else return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw makeParserException(s + " is not a number");
        }
    }

    /**
     * @return the actual line number
     */
    public int getLineNumber() {
        return tokenizer.lineno();
    }

    /**
     * Represents a assembler directive
     */
    public static abstract class Directive {
        private final String name;
        private final String args;
        private final String description;

        private Directive(String name, String args, String description) {
            this.name = name;
            this.args = args;
            this.description = description;
        }

        /**
         * @return the name of the directive
         */
        public String getName() {
            return name;
        }

        /**
         * @return the arguments of the directive
         */
        public String getArgs() {
            return args;
        }

        /**
         * @return the description
         */
        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return name + " " + args + "\n\t" + description;
        }

        abstract void doWork(Parser parser, Program program) throws IOException, ParserException, ExpressionException;
    }

    private static final class DReg extends Directive {
        private DReg() {
            super(".reg", "alias Rs", "Sets an alias name for a register.");
        }

        @Override
        protected void doWork(Parser parser, Program program) throws IOException, ParserException, ExpressionException {
            String regName = parser.parseWord();
            program.addPendingComment(" " + regName);
            Register reg = parser.parseReg();
            program.addPendingComment(" " + reg);
            parser.regsMap.put(regName, reg);
        }
    }

    private static final class DWord extends Directive {
        private DWord() {
            super(".word", "addr", "Reserves a single word in the RAM. Its address is stored in addr.");
        }

        @Override
        protected void doWork(Parser parser, Program program) throws IOException, ParserException, ExpressionException {
            String word = parser.parseWord();
            program.addPendingComment(" " + word);
            program.addRam(word, 1);
        }
    }

    private static final class DLong extends Directive {
        private DLong() {
            super(".long", "addr", "Reserves two words in the RAM. Its address is stored in addr.");
        }

        @Override
        protected void doWork(Parser parser, Program program) throws IOException, ParserException, ExpressionException {
            String word = parser.parseWord();
            program.addPendingComment(" " + word);
            program.addRam(word, 2);
        }
    }

    private static final class DOrg extends Directive {
        private DOrg() {
            super(".org", "addr", "Sets the actual code address. Is used to place code segments to fixed addresses.");
        }

        @Override
        protected void doWork(Parser parser, Program program) throws IOException, ParserException, ExpressionException {
            int addr = parser.parseExpression().getValue(program.getContext());
            program.addPendingOrigin(addr);
            program.addPendingComment(" 0x" + Integer.toHexString(addr));
        }
    }

    private static final class DDOrg extends Directive {
        private DDOrg() {
            super(".dorg", "addr", "Sets the actual data address. If used, assembler is switched to von Neumann mode.");
        }

        @Override
        protected void doWork(Parser parser, Program program) throws IOException, ParserException, ExpressionException {
            int addr = parser.parseExpression().getValue(program.getContext());
            program.setRamStart(addr);
        }
    }

    private static final class DData extends Directive {
        private DData() {
            super(".data", "addr value(,value)*", "Copies the given values to the RAM. The address of the values is stored in addr.");
        }

        @Override
        protected void doWork(Parser parser, Program program) throws IOException, ParserException, ExpressionException {
            String ident = parser.parseWord();
            program.addPendingComment(" " + ident);
            program.addDataLabel(ident);
            parser.readData(program);
            while (parser.isNext(',')) {
                parser.isNext(TT_EOL);
                program.addPendingComment(", ");
                parser.readData(program);
            }
        }
    }

    private static final class DConst extends Directive {
        private DConst() {
            super(".const", "ident const", "Creates the given constant.");
        }

        @Override
        protected void doWork(Parser parser, Program program) throws IOException, ParserException, ExpressionException {
            String word = parser.parseWord();
            int value = parser.parseExpression().getValue(program.getContext());
            program.addPendingComment(" " + word + " " + value);
            program.getContext().addIdentifier(word, value);
        }
    }

    private static final class DInclude extends Directive {
        private DInclude() {
            super(".include", "\"filename\"", "Includes the given file.");
        }

        @Override
        protected void doWork(Parser parser, Program program) throws IOException, ParserException, ExpressionException {
            if (parser.isNext('"')) {
                String filename = parser.tokenizer.sval;
                if (parser.baseFile == null)
                    throw parser.makeParserException("no base file name available");
                program.addPendingComment("\n; included " + filename + "\n");
                Parser inc = new Parser(new File(parser.baseFile.getParentFile(), filename));
                inc.parseProgram(program);
            } else
                throw parser.makeParserException("no filename found");
        }
    }
}
