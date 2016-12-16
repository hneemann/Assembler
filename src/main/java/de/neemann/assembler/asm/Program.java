package de.neemann.assembler.asm;

import de.neemann.assembler.expression.Context;
import de.neemann.assembler.expression.ExpressionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents an entire program
 *
 * @author hneemann
 */
public class Program {

    private final ArrayList<Instruction> prog;
    private final Context context;
    private final TreeMap<Integer, ArrayList<Integer>> dataMap;
    private int ramPos = 0;
    private PendingString pendingLabel = new PendingString("label");
    private PendingString pendingMacroDescription = new PendingString("description");
    private PendingString pendingComment = new PendingString("comment");
    private int lineNumber;
    private int pendingAddr = -1;
    private HashMap<Integer, Integer> addrToLineMap;

    /**
     * Creates a new instance
     */
    public Program() {
        prog = new ArrayList<>();
        context = new Context();
        dataMap = new TreeMap<>();
        addrToLineMap = new HashMap<>();
    }

    /**
     * @param lineNumber thes the line number for the next added instruction
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * Adds an instruction to the program
     *
     * @param i the instruction to add
     * @return then for chained calls
     */
    public Program add(Instruction i) {
        i.setLabel(pendingLabel.get());
        i.setMacroDescription(pendingMacroDescription.get());
        i.setComment(pendingComment.get());

        if (pendingAddr >= 0) {
            i.setAbsAddr(pendingAddr);
            pendingAddr = -1;
        }

        i.setLineNumber(lineNumber);
        lineNumber = 0;

        prog.add(i);
        return this;
    }

    /**
     * Traverses the whole programm
     *
     * @param instructionVisitor the visitor to use
     * @return thei for chained calls
     * @throws ExpressionException ExpressionException
     */
    public Program traverse(InstructionVisitor instructionVisitor) throws ExpressionException {
        int addr = 0;
        addrToLineMap.clear();
        for (int i = 0, progSize = prog.size(); i < progSize; i++) {
            Instruction in = prog.get(i);
            try {
                final int absAddr = in.getAbsAddr();
                if (absAddr >= 0) {
                    if (absAddr < addr)
                        throw new ExpressionException(".org cannot jmp backward!");
                    addr = absAddr;
                }
                context.setInstrAddr(addr);
                context.setIdentifier(Context.NEXT_ADDR, addr + calcRelAddr(i, 1));
                context.setIdentifier(Context.SKIP_ADDR, addr + calcRelAddr(i, 2));
                context.setIdentifier(Context.SKIP2_ADDR, addr + calcRelAddr(i, 3));
                instructionVisitor.visit(in, context);
                addrToLineMap.put(addr, in.getLineNumber());
                addr += in.size();
            } catch (ExpressionException e) {
                e.setLineNumber(in.getLineNumber());
                throw e;
            }
        }
        return this;
    }

    /**
     * Returns the line of the given addr
     *
     * @param addr the address
     * @return the line number or -1 if not found
     */
    public int getLineByAddr(int addr) {
        final Integer l = addrToLineMap.get(addr);
        if (l == null)
            return -1;
        else
            return l;
    }

    private int calcRelAddr(int i, int len) {
        int a = 0;
        for (int j = 0; j < len; j++) {
            if (i + j >= prog.size())
                return a;
            a += prog.get(i + j).size();
        }
        return a;
    }


    private Program appendData() throws InstructionException {
        int p = 0;
        for (Map.Entry<Integer, ArrayList<Integer>> e : dataMap.entrySet()) {
            int value = e.getKey();
            prog.add(p++, new InstructionBuilder(Opcode.LDI).setDest(Register.R0).setConstant(value).build());
            for (int addr : e.getValue()) {
                prog.add(p++, new InstructionBuilder(Opcode.STS).setSource(Register.R0).setConstant(addr).build());
            }
        }
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Instruction i : prog) {
            sb.append(i.toString()).append("\n");
        }
        return sb.toString();
    }

    /**
     * @return the number of instructions
     */
    public int getInstructionCount() {
        return prog.size();
    }

    /**
     * Returns the instruction with the given number
     *
     * @param i the number of the instruction to return
     * @return the instruction
     */
    public Instruction getInstruction(int i) {
        return prog.get(i);
    }

    /**
     * Adds a named ram location to the program
     *
     * @param ident the identifier to access this piece of ram
     * @param size  the sive to allocate
     * @return the position of the piece of ram
     * @throws ExpressionException ExpressionException
     */
    public int addRam(String ident, int size) throws ExpressionException {
        int r = ramPos;
        context.addIdentifier(ident, ramPos);
        ramPos += size;
        return r;
    }

    /**
     * @return the actual context
     */
    public Context getContext() {
        return context;
    }

    /**
     * @param value adds constant data to the program
     */
    public void addData(int value) {
        ArrayList<Integer> list = dataMap.get(value);
        if (list == null) {
            list = new ArrayList<>();
            dataMap.put(value, list);
        }
        list.add(ramPos);
        ramPos++;
    }

    /**
     * Sets a pending label.
     * This label is added to the next instruction which is added to the program
     *
     * @param pendingLabel the label
     * @throws ExpressionException ExpressionException
     */
    public void setPendingLabel(String pendingLabel) throws ExpressionException {
        this.pendingLabel.set(pendingLabel);
    }

    /**
     * Sets a pending macro description.
     * The macro description is in mos cases the pseudo instruction used to create the folowing instructions.
     *
     * @param pendingMacroDescription the description
     * @throws ExpressionException ExpressionException
     */
    public void setPendingMacroDescription(String pendingMacroDescription) throws ExpressionException {
        this.pendingMacroDescription.set(pendingMacroDescription);
    }

    /**
     * Adds a pending comment
     *
     * @param comment the comment
     * @throws ExpressionException ExpressionException
     */
    public void addPendingComment(String comment) throws ExpressionException {
        this.pendingComment.add(comment);
    }

    /**
     * Sets the address for the next command
     *
     * @param addr the address to set
     */
    public void addPendingOrigin(int addr) {
        this.pendingAddr = addr;
    }


    /**
     * Performs a number of optimizations.
     *
     * @return A linked program
     * @throws InstructionException InstructionException
     * @throws ExpressionException  ExpressionException
     */
    public Program optimizeAndLink() throws InstructionException, ExpressionException {
        appendData();
        traverse(new LinkAddVisitor());
        traverse(new OptimizerShort());

        while (true) {
            traverse(new LinkSetVisitor());
            OptimizerJmp optimizerJmp = new OptimizerJmp();
            traverse(optimizerJmp);
            if (!optimizerJmp.wasOptimized())
                break;
        }

        traverse(new LinkSetVisitor());
        return this;
    }

    private static class LinkAddVisitor implements InstructionVisitor {
        @Override
        public void visit(Instruction instruction, Context context) throws ExpressionException {
            if (instruction.getLabel() != null) {
                context.addIdentifier(instruction.getLabel(), context.getInstrAddr());
            }
        }
    }

    private static class LinkSetVisitor implements InstructionVisitor {
        @Override
        public void visit(Instruction instruction, Context context) throws ExpressionException {
            if (instruction.getLabel() != null) {
                context.setIdentifier(instruction.getLabel(), context.getInstrAddr());
            }
        }
    }


    private static class PendingString {
        private String name;
        private String str;

        PendingString(String name) {
            this.name = name;
        }

        public void set(String s) throws ExpressionException {
            if (this.str != null)
                throw new ExpressionException("two " + name + " for the same command: " + str + ", " + s);
            this.str = s;
        }

        public void add(String s) throws ExpressionException {
            if (str == null)
                str = s;
            else
                str += s;
        }

        public String get() {
            String s = str;
            str = null;
            return s;
        }
    }
}
