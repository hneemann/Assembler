package de.neemann.assembler.parser;

import de.neemann.assembler.asm.InstructionException;
import de.neemann.assembler.asm.Opcode;
import de.neemann.assembler.asm.Program;
import de.neemann.assembler.asm.Register;
import de.neemann.assembler.expression.*;

import java.io.*;

import static java.io.StreamTokenizer.*;

/**
 * @author hneemann
 */
public class Parser implements Closeable {
    private final StreamTokenizer tokens;
    private final Reader in;

    public Parser(String source) {
        this(new StringReader(source));
    }

    public Parser(Reader in) {
        this.in = in;
        tokens = new StreamTokenizer(in);
        tokens.eolIsSignificant(true);
    }

    public Program getProgram() throws IOException, ParserException, InstructionException {
        Program p = new Program();

        WHILE:
        while (true) {
            switch (tokens.nextToken()) {
                case TT_WORD:
                    String t = tokens.sval;
                    if (t.startsWith(".")) {
                        parseMetaCommand(t);
                    } else {
                        parseInstruction(p, t);
                    }

                    switch (tokens.nextToken()) {
                        case ';':
                            skipLine();
                            break;
                        case TT_EOF:
                        case StreamTokenizer.TT_EOL:
                            break;
                        default:
                            throw makeParserException("unexpected token " + tokens);
                    }
                    ;

                    break;
                case ';':
                    skipLine();
                    break;
                case TT_EOF:
                    break WHILE;
                default:
                    throw makeParserException("unexpected token '" + tokens + "'");
            }
        }

        return p;
    }

    private void skipLine() throws IOException {
        int to;
        do {
            to = tokens.nextToken();
        } while (to != TT_EOF && to != StreamTokenizer.TT_EOL);
    }

    private void parseMetaCommand(String t) {

    }

    private void parseInstruction(Program p, String t) throws IOException, ParserException, InstructionException {
        Opcode opcode = Opcode.parseStr(t);

        if (opcode == null) {
            p.label(t);
            consume(':');
            t = parseWord();
            opcode = Opcode.parseStr(t);
            if (opcode == null)
                throw makeParserException("opcode expected, found '" + t + "'");
        }


        Register dest = null;
        Register source = null;
        switch (opcode.getRegsNeeded()) {
            case both:
                dest = parseReg();
                consume(',');
                source = parseReg();
                break;
            case none:
                break;
            default:
                dest = parseReg();
                break;
        }

        Expression constant = null;
        if (opcode.getImmedNeeded() == Opcode.ImmedNeeded.Yes) {
            constant = parseExpression();
        }

        switch (opcode.getRegsNeeded()) {
            case both:
                if (constant != null)
                    p.add(opcode, dest, source, constant);
                else
                    p.add(opcode, dest, source);
                break;
            case none:
                p.add(opcode, constant);
                break;
            default:
                if (constant != null)
                    p.add(opcode, dest, constant);
                else
                    p.add(opcode, dest);
                break;
        }
    }

    private void consume(char c) throws IOException, ParserException {
        if (tokens.nextToken() != c)
            throw makeParserException("expected '" + c + "', found '" + tokens + "'");
    }

    private Register parseReg() throws IOException, ParserException {
        String r = parseWord();
        Register reg = Register.parseStr(r);
        if (reg == null)
            throw makeParserException("expected a register, found '" + r + "'");
        return reg;
    }

    private String parseWord() throws IOException, ParserException {
        int t = tokens.nextToken();
        if (t != TT_WORD)
            throw makeParserException("unexpected number or EOL/EOF!");
        return tokens.sval;
    }

    private ParserException makeParserException(String message) {
        return new ParserException(message, tokens.lineno());
    }


    @Override
    public void close() throws IOException {
        in.close();
    }

    private boolean isNext(String str) throws IOException {
        int t = tokens.nextToken();
        if (t == TT_WORD && tokens.sval.equalsIgnoreCase(str))
            return true;

        tokens.pushBack();
        return false;
    }

    private boolean isNext(char c) throws IOException {
        int t = tokens.nextToken();
        if (t == c)
            return true;

        tokens.pushBack();
        return false;
    }

    public Expression getExpression() throws IOException, ParserException {
        Expression exp = parseExpression();
        if (tokens.nextToken() != TT_EOF)
            throw makeParserException("no EOF found, but " + tokens);
        return exp;
    }

    private Expression parseExpression() throws IOException, ParserException {
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
        Expression ex = parseValue();
        while (isNext('*')) {
            ex = new Operate(ex, Operate.Operation.MUL, parseValue());
        }
        return ex;
    }

    private Expression parseValue() throws IOException, ParserException {
        switch (tokens.nextToken()) {
            case TT_WORD:
                return new Identifier(tokens.sval);
            case TT_NUMBER:
                return new Constant((int) tokens.nval);
            case '(':
                Expression ex = parseExpression();
                consume(')');
                return ex;
            case '-':
                return new Neg(parseExpression());
            case '~':
                return new Not(parseExpression());
        }
        throw makeParserException("unexpected token " + tokens);
    }


}
