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
    /**
     * the help message for the directives
     */
    public static final String HELP =
            "Assembler commands\n\n"
                    + ".reg alias Rs\n\tSets an alias name for a register\n\n"
                    + ".word addr\n\tReserves a single word in the RAM. Its address is stored in addr\n\n"
                    + ".long addr\n\tReserves two words in the RAM. Its address is stored in addr\n\n"
                    + ".const ident const\n\tcreates the given constant\n\n"
                    + ".dorg addr\n\tSets the actual data address. If used, assembler is switched to von Neumann mode.\n\n"
                    + ".org addr\n\tSets the actual code address. Is used to place code segments to fixed addresses.\n\n"
                    + ".data addr value(,value)*\n\tcopies the given values to the RAM. The address of the values is stored in addr.\n\n"
                    + ".include \"filename\"\n\tincludes the given file";
    private final StreamTokenizer tokenizer;
    private final Reader in;
    private static final HashMap<String, Macro> MACROS = new HashMap<>();
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
    }

    /**
     * @return the available macros
     */
    public static Iterable<Macro> getMacros() {
        return MACROS.values();
    }

    private static void addMacro(Macro m) {
        MACROS.put(m.getName().toLowerCase(), m);
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
        switch (t) {
            case ".reg":
                String regName = parseWord();
                p.addPendingComment(" " + regName);
                Register reg = parseReg();
                p.addPendingComment(" " + reg);
                regsMap.put(regName, reg);
                break;
            case ".word":
                String word = parseWord();
                p.addPendingComment(" " + word);
                p.addRam(word, 1);
                break;
            case ".long":
                word = parseWord();
                p.addPendingComment(" " + word);
                p.addRam(word, 2);
                break;
            case ".org":
                int addr = parseExpression().getValue(p.getContext());
                p.addPendingOrigin(addr);
                p.addPendingComment(" 0x" + Integer.toHexString(addr));
                break;
            case ".dorg":
                addr = parseExpression().getValue(p.getContext());
                p.setRamStart(addr);
                break;
            case ".const":
                word = parseWord();
                int value = parseExpression().getValue(p.getContext());
                p.addPendingComment(" " + word + " " + value);
                p.getContext().addIdentifier(word, value);
                break;
            case ".data":
                String ident = parseWord();
                p.addPendingComment(" " + ident);
                p.addDataLabel(ident);
                readData(p);
                while (isNext(',')) {
                    isNext(TT_EOL);
                    p.addPendingComment(", ");
                    readData(p);
                }
                break;
            case ".include":
                if (isNext('"')) {
                    String filename = tokenizer.sval;
                    if (baseFile == null)
                        throw makeParserException("no base file name available");
                    p.addPendingComment("\n; included " + filename + "\n");
                    Parser inc = new Parser(new File(baseFile.getParentFile(), filename));
                    inc.parseProgram(p);
                } else
                    throw makeParserException("no filename found");
                break;
            default:
                throw makeParserException("unknown meta command " + t);
        }
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

}
