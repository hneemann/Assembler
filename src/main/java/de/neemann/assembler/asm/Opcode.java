package de.neemann.assembler.asm;

import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * Defines the opcodes and creates the table for the control unit from the opcode description.
 *
 * @author hneemann
 */
public enum Opcode {
    NOP("does nothing", RegsNeeded.none, ImmedNeeded.No,
            ReadRam.No, WriteRam.No, Branch.No, Immed.No, StoreSel.RAM, ALU.Nothing, EnRegWrite.No),
    MOV("Move the content of [s] to register [d]",
            RegsNeeded.both, ImmedNeeded.No, ALU.Nothing, Immed.No),
    ADD("Adds the content of register [s] to register [d] without carry",
            RegsNeeded.both, ImmedNeeded.No, ALU.ADD, Immed.No),
    ADC("Adds the content of register [s] to register [d] with carry",
            RegsNeeded.both, ImmedNeeded.No, ALU.ADC, Immed.No),
    SUB("Subtracts the content of register [s] from register [d] without carry",
            RegsNeeded.both, ImmedNeeded.No, ALU.SUB, Immed.No),
    SBC("Subtracts the content of register [s] from register [d] with carry",
            RegsNeeded.both, ImmedNeeded.No, ALU.SBC, Immed.No),
    AND("Stores [s] and [d] in register [d].",
            RegsNeeded.both, ImmedNeeded.No, ALU.AND, Immed.No),
    OR("Stores [s] or [d] in register [d].",
            RegsNeeded.both, ImmedNeeded.No, ALU.OR, Immed.No),
    XOR("Stores [s] xor [d] in register [d].",
            RegsNeeded.both, ImmedNeeded.No, ALU.XOR, Immed.No),
    LDI("Loads Register [d] with the constant value [c]",
            RegsNeeded.dest, ImmedNeeded.Yes, ALU.Nothing, Immed.Regist),
    LDIs("Loads Register [d] with the constant value [c]",
            RegsNeeded.dest, ImmedNeeded.Yes, ALU.Nothing, Immed.instrSource),
    ADDI("Adds the constant [c] to register [d] without carry",
            RegsNeeded.dest, ImmedNeeded.Yes, ALU.ADD, Immed.Regist),
    ADDIs("Adds the constant [c] to register [d] without carry",
            RegsNeeded.dest, ImmedNeeded.Yes, ALU.ADD, Immed.instrSource),
    ADCI("Adds the constant [c] to register [d] with carry",
            RegsNeeded.dest, ImmedNeeded.Yes, ALU.ADC, Immed.Regist),
    ADCIs("Adds the constant [c] to register [d] with carry",
            RegsNeeded.dest, ImmedNeeded.Yes, ALU.ADC, Immed.instrSource),
    SUBI("Subtracts a constant [c] from register [d] without carry",
            RegsNeeded.dest, ImmedNeeded.Yes, ALU.SUB, Immed.Regist),
    SUBIs("Subtracts a constant [c] from register [d] without carry",
            RegsNeeded.dest, ImmedNeeded.Yes, ALU.SUB, Immed.instrSource),
    SBCI("Subtracts a constant [c] from register [d] with carry",
            RegsNeeded.dest, ImmedNeeded.Yes, ALU.SBC, Immed.Regist),
    SBCIs("Subtracts a constant [c] from register [d] with carry",
            RegsNeeded.dest, ImmedNeeded.Yes, ALU.SBC, Immed.instrSource),
    ANDI("Stores [d] and [c] in register [d]",
            RegsNeeded.dest, ImmedNeeded.Yes, ALU.AND, Immed.Regist),
    ANDIs("Stores [d] and [c] in register [d]",
            RegsNeeded.dest, ImmedNeeded.Yes, ALU.AND, Immed.instrSource),
    ORI("Stores [d] or [c] in register [d]",
            RegsNeeded.dest, ImmedNeeded.Yes, ALU.OR, Immed.Regist),
    ORIs("Stores [d] or [c] in register [d]",
            RegsNeeded.dest, ImmedNeeded.Yes, ALU.OR, Immed.instrSource),
    XORI("Stores [d] xor [c] in register [d]",
            RegsNeeded.dest, ImmedNeeded.Yes, ALU.XOR, Immed.Regist),
    XORIs("Stores [d] xor [c] in register [d]",
            RegsNeeded.dest, ImmedNeeded.Yes, ALU.XOR, Immed.instrSource),

    MUL("Multiplies the content of register [s] with register [d] and stores result in [d]",
            RegsNeeded.both, ImmedNeeded.No, ALU.MUL, Immed.No),
    MULI("Multiplies the constant [c] with register [d] and stores result in [d]",
            RegsNeeded.dest, ImmedNeeded.Yes, ALU.MUL, Immed.Regist),
    MULIs("Multiplies the constant [c] with register [d] and stores result in [d]",
            RegsNeeded.dest, ImmedNeeded.Yes, ALU.MUL, Immed.instrSource),


    CMP("Subtracts the content of register [s] from register [d] without carry, does not store the value",
            RegsNeeded.both, ImmedNeeded.No, ALU.SUB, Immed.No, EnRegWrite.No),
    CPI("Subtracts a constant [c] from register [d] without carry, does not store the value",
            RegsNeeded.dest, ImmedNeeded.Yes, ALU.SUB, Immed.Regist, EnRegWrite.No),
    CPIs("Subtracts a constant [c] from register [d] without carry, does not store the value",
            RegsNeeded.dest, ImmedNeeded.Yes, ALU.SUB, Immed.instrSource, EnRegWrite.No),


    LSL("Shifts register [d] by one bit to the left. A zero is filled in.",
            RegsNeeded.dest, ImmedNeeded.No, ALU.LSL, Immed.No),
    LSR("Shifts register [d] by one bit to the right. A zero is filled in.",
            RegsNeeded.dest, ImmedNeeded.No, ALU.LSR, Immed.No),
    ROL("Shifts register [d] by one bit to the left. The carry bit is filled in.",
            RegsNeeded.dest, ImmedNeeded.No, ALU.ROL, Immed.No),
    ROR("Shifts register [d] by one bit to the right. The carry bit is filled in.",
            RegsNeeded.dest, ImmedNeeded.No, ALU.ROR, Immed.No),
    ASR("Shifts register [d] by one bit to the right. The MSB remains unchanged.",
            RegsNeeded.dest, ImmedNeeded.No, ALU.ASR, Immed.No),

    SWAP("Swaps the high and low byte in register [d].",
            RegsNeeded.dest, ImmedNeeded.No, ALU.SWAP, Immed.No),
    SWAPN("Swaps the high and low nibbles of both bytes in register [d].",
            RegsNeeded.dest, ImmedNeeded.No, ALU.SWAPN, Immed.No),

    ST("Stores the content of register [s] to the memory at the address ([d])",
            RegsNeeded.both, ImmedNeeded.No, ReadRam.No, WriteRam.Yes, Branch.No, Immed.Zero, StoreSel.ALU, ALU.ADD, EnRegWrite.No),
    LD("Loads the value at memory address ([s]) to register [d]",
            RegsNeeded.both, ImmedNeeded.No, ReadRam.Yes, WriteRam.No, Branch.No, Immed.Zero, StoreSel.RAM, ALU.ADD, EnRegWrite.Yes, SourceToAlu.Yes),
    STS("Stores the content of register [s] to memory at the location given by [c]",
            RegsNeeded.source, ImmedNeeded.Yes, ReadRam.No, WriteRam.Yes, Branch.No, Immed.Regist, StoreSel.ALU, ALU.Nothing, EnRegWrite.No),
    STSs("Stores the content of register [s] to memory at the location given by [c]",
            RegsNeeded.source, ImmedNeeded.Yes, ReadRam.No, WriteRam.Yes, Branch.No, Immed.instrDest, StoreSel.ALU, ALU.Nothing, EnRegWrite.No),
    LDS("Loads the memory value at the location given by [c] to register [d]",
            RegsNeeded.dest, ImmedNeeded.Yes, ReadRam.Yes, WriteRam.No, Branch.No, Immed.Regist, StoreSel.RAM, ALU.Nothing, EnRegWrite.Yes),
    LDSs("Loads the memory value at the location given by [c] to register [d]",
            RegsNeeded.dest, ImmedNeeded.Yes, ReadRam.Yes, WriteRam.No, Branch.No, Immed.instrSource, StoreSel.RAM, ALU.Nothing, EnRegWrite.Yes),
    STO("Stores the content of register [s] to the memory at the address ([d]+[c])",
            RegsNeeded.both, ImmedNeeded.Yes, ReadRam.No, WriteRam.Yes, Branch.No, Immed.Regist, StoreSel.ALU, ALU.ADD, EnRegWrite.No),
    LDO("Loads the value at memory address ([s]+[c]) to register [d]",
            RegsNeeded.both, ImmedNeeded.Yes, ReadRam.Yes, WriteRam.No, Branch.No, Immed.Regist, StoreSel.RAM, ALU.ADD, EnRegWrite.Yes, SourceToAlu.Yes),

    BRC("Jumps to the address given by [c] if carry flag is set , Range is 512 words",
            RegsNeeded.none, ImmedNeeded.Yes, ReadRam.No, WriteRam.No, Branch.BRC, Immed.instr, StoreSel.RAM, ALU.Nothing, EnRegWrite.No),
    BRZ("Jumps to the address given by [c] if zero flag is set , Range is 512 words",
            RegsNeeded.none, ImmedNeeded.Yes, ReadRam.No, WriteRam.No, Branch.BRZ, Immed.instr, StoreSel.RAM, ALU.Nothing, EnRegWrite.No),
    BRNC("Jumps to the address given by [c] if carry flag is clear , Range is 512 words",
            RegsNeeded.none, ImmedNeeded.Yes, ReadRam.No, WriteRam.No, Branch.BRNC, Immed.instr, StoreSel.RAM, ALU.Nothing, EnRegWrite.No),
    BRNZ("Jumps to the address given by [c] if zero flag is clear , Range is 512 words",
            RegsNeeded.none, ImmedNeeded.Yes, ReadRam.No, WriteRam.No, Branch.BRNZ, Immed.instr, StoreSel.RAM, ALU.Nothing, EnRegWrite.No),

    RCALL("Jumps to the address given by [c], the return address is stored in register [d]",
            RegsNeeded.dest, ImmedNeeded.Yes, ReadRam.No, WriteRam.No, Branch.No, Immed.Regist, StoreSel.RAM, ALU.Nothing, EnRegWrite.Yes, SourceToAlu.No, StorePC.Yes, JmpAbs.Yes, WriteIO.No, ReadIO.No, Break.No),
    RRET("Jumps to the address given by register [s]",
            RegsNeeded.source, ImmedNeeded.No, ReadRam.No, WriteRam.No, Branch.No, Immed.No, StoreSel.RAM, ALU.Nothing, EnRegWrite.No, SourceToAlu.No, StorePC.No, JmpAbs.Yes, WriteIO.No, ReadIO.No, Break.No),

    JMP("Jumps to the address given by [c]",
            RegsNeeded.none, ImmedNeeded.Yes, ReadRam.No, WriteRam.No, Branch.No, Immed.Regist, StoreSel.RAM, ALU.Nothing, EnRegWrite.No, SourceToAlu.No, StorePC.No, JmpAbs.Yes, WriteIO.No, ReadIO.No, Break.No),
    JMPs("Jumps to the address given by [c]",
            RegsNeeded.none, ImmedNeeded.Yes, ReadRam.No, WriteRam.No, Branch.uncond, Immed.instr, StoreSel.RAM, ALU.Nothing, EnRegWrite.No),


    OUT("Writes the content of register [s] to io location given by [c]",
            RegsNeeded.source, ImmedNeeded.Yes, ReadRam.No, WriteRam.No, Branch.No, Immed.Regist, StoreSel.ALU, ALU.Nothing, EnRegWrite.No, SourceToAlu.No, StorePC.No, JmpAbs.No, WriteIO.Yes, ReadIO.No, Break.No),
    OUTs("Writes the content of register [s] to io location given by [c]",
            RegsNeeded.source, ImmedNeeded.Yes, ReadRam.No, WriteRam.No, Branch.No, Immed.instrDest, StoreSel.ALU, ALU.Nothing, EnRegWrite.No, SourceToAlu.No, StorePC.No, JmpAbs.No, WriteIO.Yes, ReadIO.No, Break.No),
    OUTO("Writes the content of register [s] to the io location ([d]+[c])",
            RegsNeeded.both, ImmedNeeded.Yes, ReadRam.No, WriteRam.No, Branch.No, Immed.Regist, StoreSel.ALU, ALU.ADD, EnRegWrite.No, SourceToAlu.No, StorePC.No, JmpAbs.No, WriteIO.Yes, ReadIO.No, Break.No),
    OUTR("Writes the content of register [s] to the io location ([d])",
            RegsNeeded.both, ImmedNeeded.No, ReadRam.No, WriteRam.No, Branch.No, Immed.Zero, StoreSel.ALU, ALU.ADD, EnRegWrite.No, SourceToAlu.No, StorePC.No, JmpAbs.No, WriteIO.Yes, ReadIO.No, Break.No),


    IN("Reads the io location given by [c] and stores it in register [d]",
            RegsNeeded.dest, ImmedNeeded.Yes, ReadRam.No, WriteRam.No, Branch.No, Immed.Regist, StoreSel.RAM, ALU.Nothing, EnRegWrite.Yes, SourceToAlu.Yes, StorePC.No, JmpAbs.No, WriteIO.No, ReadIO.Yes, Break.No),
    INs("Reads the io location given by [c] and stores it in register [d]",
            RegsNeeded.dest, ImmedNeeded.Yes, ReadRam.No, WriteRam.No, Branch.No, Immed.instrSource, StoreSel.RAM, ALU.Nothing, EnRegWrite.Yes, SourceToAlu.Yes, StorePC.No, JmpAbs.No, WriteIO.No, ReadIO.Yes, Break.No),
    INO("Reads the io location given by ([s]+[c]) and stores it in register [d]",
            RegsNeeded.both, ImmedNeeded.Yes, ReadRam.No, WriteRam.No, Branch.No, Immed.Regist, StoreSel.RAM, ALU.ADD, EnRegWrite.Yes, SourceToAlu.Yes, StorePC.No, JmpAbs.No, WriteIO.No, ReadIO.Yes, Break.No),
    INR("Reads the io location given by ([s]) and stores it in register [d]",
            RegsNeeded.both, ImmedNeeded.No, ReadRam.No, WriteRam.No, Branch.No, Immed.Zero, StoreSel.RAM, ALU.ADD, EnRegWrite.Yes, SourceToAlu.Yes, StorePC.No, JmpAbs.No, WriteIO.No, ReadIO.Yes, Break.No),

    BRK("Stops execution by blocking the clock",
            RegsNeeded.none, ImmedNeeded.No, ReadRam.No, WriteRam.No, Branch.No, Immed.No, StoreSel.RAM, ALU.Nothing, EnRegWrite.No, SourceToAlu.No, StorePC.No, JmpAbs.No, WriteIO.No, ReadIO.No, Break.Yes),;


    public enum RegsNeeded {none, source, dest, both}

    public enum ImmedNeeded {No, Yes}

    enum ReadRam {No, Yes}

    enum ReadIO {No, Yes}

    enum WriteRam {No, Yes}

    enum WriteIO {No, Yes}

    enum Break {No, Yes}

    enum SourceToAlu {No, Yes}

    enum Branch {No, BRC, BRZ, uncond, res, BRNC, BRNZ}

    enum Immed {No, Regist, Zero, One, Two, instrSource, instr, instrDest}

    enum StoreSel {RAM, ALU}

    enum ALU {
        Nothing, ADD, SUB, AND, OR, XOR, LSL, LSR, ASR, SWAP, SWAPN, MUL, res4, res5, res6, res7,
        res8, ADC, SBC, res9, res10, res11, ROL, ROR
    }

    enum EnRegWrite {No, Yes}

    enum StorePC {No, Yes}

    enum JmpAbs {No, Yes}

    private static final String SOURCEREG = "[Source Reg.]";
    private static final String DESTREG = "[Dest Reg.]";
    private static final String CONSTANT = "[Constant]";

    private final ReadRam rr;
    private final WriteRam wr;
    private final Branch br;
    private final Immed imed;
    private final StoreSel storeSel;
    private final ALU alu;
    private final EnRegWrite enRegWrite;
    private final StorePC storePC;
    private final SourceToAlu sourceToAlu;
    private final JmpAbs jmpAbs;
    private final WriteIO wio;
    private final ReadIO rio;
    private final Break brk;

    private final String description;
    private final RegsNeeded regsNeeded;
    private final ImmedNeeded immedNeeded;

    Opcode(String description, RegsNeeded rn, ImmedNeeded en, ReadRam rr, WriteRam wr, Branch br, Immed imed, StoreSel storeSel, ALU alu, EnRegWrite enRegWrite, SourceToAlu sourceToAlu, StorePC storePC, JmpAbs jmpAbs, WriteIO wio, ReadIO rio, Break brk) {
        this.description = addConstLimit(description, imed).replace("[d]", DESTREG).replace("[s]", SOURCEREG).replace("[c]", CONSTANT);
        this.regsNeeded = rn;
        this.immedNeeded = en;
        this.rr = rr;
        this.wr = wr;
        this.br = br;
        this.imed = imed;
        this.storeSel = storeSel;
        this.alu = alu;
        this.enRegWrite = enRegWrite;
        this.storePC = storePC;
        this.sourceToAlu = sourceToAlu;
        this.jmpAbs = jmpAbs;
        this.wio = wio;
        this.rio = rio;
        this.brk = brk;

        if (regsNeeded != RegsNeeded.none && imed == Immed.instr)
            throw new RuntimeException("immediate in instruction and registers used simultanious " + name());
    }

    private String addConstLimit(String description, Immed imed) {
        if (imed.equals(Immed.instrDest) || imed.equals(Immed.instrSource)) {
            description += " (0<=[c]<=31)";
        } else {
            if (imed.equals(Immed.instr)) {
                description += " (-256<=[c]<=255)";
            }
        }
        return description;
    }

    Opcode(String description, RegsNeeded rn, ImmedNeeded en, ReadRam rr, WriteRam wr, Branch br, Immed imed, StoreSel storeSel, ALU alu, EnRegWrite enRegWrite, SourceToAlu sta) {
        this(description, rn, en, rr, wr, br, imed, storeSel, alu, enRegWrite, sta, StorePC.No, JmpAbs.No, WriteIO.No, ReadIO.No, Break.No);
    }

    Opcode(String description, RegsNeeded rn, ImmedNeeded en, ReadRam rr, WriteRam wr, Branch br, Immed imed, StoreSel storeSel, ALU alu, EnRegWrite enRegWrite) {
        this(description, rn, en, rr, wr, br, imed, storeSel, alu, enRegWrite, SourceToAlu.No);
    }


    // Simple operation
    Opcode(String description, RegsNeeded rn, ImmedNeeded en, ALU alu, Immed immed) {
        this(description, rn, en,
                ReadRam.No,
                WriteRam.No,
                Branch.No,
                immed,
                StoreSel.ALU,
                alu,
                EnRegWrite.Yes);
    }

    //cmp
    Opcode(String description, RegsNeeded rn, ImmedNeeded en, ALU alu, Immed immed, EnRegWrite enRegWrite) {
        this(description, rn, en,
                ReadRam.No,
                WriteRam.No,
                Branch.No,
                immed,
                StoreSel.ALU,
                alu,
                enRegWrite);
    }


    int createControlWord() {
        return rr.ordinal()
                | (wr.ordinal() << 1)
                | (imed.ordinal() << 2)
                | (jmpAbs.ordinal() << 5)
                | (storeSel.ordinal() << 6)
                | (enRegWrite.ordinal() << 7)
                | (storePC.ordinal() << 8)
                | (sourceToAlu.ordinal() << 9)

                | (alu.ordinal() << 10)
                | (br.ordinal() << 15)
                | (wio.ordinal() << 18)
                | (rio.ordinal() << 19)
                | (brk.ordinal() << 20)
                ;
    }

    public RegsNeeded getRegsNeeded() {
        return regsNeeded;
    }

    public ImmedNeeded getImmedNeeded() {
        return immedNeeded;
    }

    public String getDescription() {
        return description;
    }

    public Immed getImmed() {
        return imed;
    }


    public static Opcode parseStr(String name) {
        for (Opcode op : Opcode.values())
            if (op.name().equalsIgnoreCase(name))
                return op;
        return null;
    }

    public static void writeControlWords(PrintStream out) {
        out.println("v2.0 raw");
        for (Opcode oc : Opcode.values())
            out.println(Integer.toHexString(oc.createControlWord()));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name());
        sb.append(" ");
        switch (regsNeeded) {
            case both:
                sb.append(DESTREG).append(", ").append(SOURCEREG);
                break;
            case source:
                sb.append(SOURCEREG);
                break;
            case dest:
                sb.append(DESTREG);
                break;
        }
        if (immedNeeded == ImmedNeeded.Yes) {
            if (regsNeeded != RegsNeeded.none)
                sb.append(", ");
            sb.append(CONSTANT);
        }
        sb.append("\n\t");
        sb.append(description);
        return sb.toString();
    }

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println(Opcode.values().length + " opcodes");
        try (PrintStream p = new PrintStream("/home/hneemann/Dokumente/DHBW/Technische_Informatik_I/Vorlesung/06_Prozessoren/java/assembler3/control.dat")) {
            writeControlWords(p);
        }

        for (Opcode op : Opcode.values()) {
            System.out.print(op.name() + ", ");
        }
        System.out.println();
        for (Register r : Register.values()) {
            System.out.print(r.name() + ", ");
        }


    }
}
