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
    private final HashMap<String, Macro> macros;
    private final HashMap<String, Register> regsMap;
    private File baseFile;

    public Parser(String source) {
        this(new StringReader(source));
    }

    public Parser(File file) throws FileNotFoundException {
        this(new FileReader(file));
        baseFile = file;
    }

    public Parser(Reader in) {
        this.in = in;

        this.regsMap = new HashMap<>();

        this.macros = new HashMap<>();
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

    public void addMacro(Macro m) {
        macros.put(m.getName().toLowerCase(), m);
    }

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
                            if (Opcode.parseStr(word) == null && !macros.containsKey(word.toLowerCase())) {
                                p.setPendingLabel(word);
                                consume(':');
                                if (isNext(TT_WORD))
                                    word = tokenizer.sval;
                                else
                                    isCommand = false;
                            }
                            if (isCommand) {
                                p.setLineNumber(tokenizer.lineno());
                                if (macros.containsKey(word.toLowerCase())) {
                                    macros.get(word.toLowerCase()).parseMacro(p, word, this);
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
        switch (t) {
            case ".reg":
                String regName = parseWord();
                Register reg = parseReg();
                regsMap.put(regName, reg);
                break;
            case ".word":
                p.addRam(parseWord(), 1);
                break;
            case ".long":
                p.addRam(parseWord(), 2);
                break;
            case ".const":
                p.getContext().addIdentifier(parseWord(), parseExpression().getValue(p.getContext()));
                break;
            case ".data":
                String ident = parseWord();
                p.addRam(ident, 0);
                readData(p);
                while (isNext(',')) {
                    isNext(TT_EOL);
                    readData(p);
                }
                break;
            case ".include":
                if (isNext('"')) {
                    String filename = tokenizer.sval;
                    if (baseFile == null)
                        throw makeParserException("no base file name available");
                    p.setPendingComment("\n; included " + filename + "\n");
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
        } else {
            p.addData(parseExpression().getValue(p.getContext()));
        }
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

    public void consume(int c) throws IOException, ParserException {
        if (tokenizer.nextToken() != c)
            throw makeParserException("expected '" + (char) c + "', found '" + tokenizer + "'");
    }

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

    public String parseWord() throws IOException, ParserException {
        int t = tokenizer.nextToken();
        if (t != TT_WORD)
            throw makeParserException("unexpected number or EOL/EOF!");
        return tokenizer.sval;
    }

    public ParserException makeParserException(String message) {
        return new ParserException(message, tokenizer.lineno());
    }


    @Override
    public void close() throws IOException {
        in.close();
    }

    public boolean isNext(String str) throws IOException {
        int t = tokenizer.nextToken();
        if (t == TT_WORD && tokenizer.sval.equalsIgnoreCase(str))
            return true;

        tokenizer.pushBack();
        return false;
    }

    public boolean isNext(int c) throws IOException {
        int t = tokenizer.nextToken();
        if (t == c)
            return true;

        tokenizer.pushBack();
        return false;
    }

    public boolean isEOL() throws IOException {
        int t = tokenizer.nextToken();
        tokenizer.pushBack();
        return t == TT_EOL || t == TT_EOF;
    }


    public Expression getExpression() throws IOException, ParserException {
        Expression exp = parseExpression();
        if (tokenizer.nextToken() != TT_EOF)
            throw makeParserException("no EOF found, but " + tokenizer);
        return exp;
    }

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
        }
        throw makeParserException("unexpected token " + tokenizer);
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

    public int getLineNumber() {
        return tokenizer.lineno();
    }

}
