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
            ReadRam.No, WriteRam.No, Branch.No, ALUBSel.Source, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.No),
    MOV("Move the content of [s] to register [d]",
            RegsNeeded.both, ImmedNeeded.No, ALUCmd.Nothing, ALUBSel.Source),
    ADD("Adds the content of register [s] to register [d] without carry",
            RegsNeeded.both, ImmedNeeded.No, ALUCmd.ADD, ALUBSel.Source),
    ADC("Adds the content of register [s] to register [d] with carry",
            RegsNeeded.both, ImmedNeeded.No, ALUCmd.ADC, ALUBSel.Source),
    SUB("Subtracts the content of register [s] from register [d] without carry",
            RegsNeeded.both, ImmedNeeded.No, ALUCmd.SUB, ALUBSel.Source),
    SBC("Subtracts the content of register [s] from register [d] with carry",
            RegsNeeded.both, ImmedNeeded.No, ALUCmd.SBC, ALUBSel.Source),
    AND("Stores [s] and [d] in register [d].",
            RegsNeeded.both, ImmedNeeded.No, ALUCmd.AND, ALUBSel.Source),
    OR("Stores [s] or [d] in register [d].",
            RegsNeeded.both, ImmedNeeded.No, ALUCmd.OR, ALUBSel.Source),
    XOR("Stores [s] xor [d] in register [d].",
            RegsNeeded.both, ImmedNeeded.No, ALUCmd.XOR, ALUBSel.Source),
    LDI("Loads Register [d] with the constant value [c]",
            RegsNeeded.dest, ImmedNeeded.Yes, ALUCmd.Nothing, ALUBSel.ImReg),
    LDIs("Loads Register [d] with the constant value [c]",
            RegsNeeded.dest, ImmedNeeded.Yes, ALUCmd.Nothing, ALUBSel.instrSource),
    ADDI("Adds the constant [c] to register [d] without carry",
            RegsNeeded.dest, ImmedNeeded.Yes, ALUCmd.ADD, ALUBSel.ImReg),
    ADDIs("Adds the constant [c] to register [d] without carry",
            RegsNeeded.dest, ImmedNeeded.Yes, ALUCmd.ADD, ALUBSel.instrSource),
    ADCI("Adds the constant [c] to register [d] with carry",
            RegsNeeded.dest, ImmedNeeded.Yes, ALUCmd.ADC, ALUBSel.ImReg),
    ADCIs("Adds the constant [c] to register [d] with carry",
            RegsNeeded.dest, ImmedNeeded.Yes, ALUCmd.ADC, ALUBSel.instrSource),
    SUBI("Subtracts a constant [c] from register [d] without carry",
            RegsNeeded.dest, ImmedNeeded.Yes, ALUCmd.SUB, ALUBSel.ImReg),
    SUBIs("Subtracts a constant [c] from register [d] without carry",
            RegsNeeded.dest, ImmedNeeded.Yes, ALUCmd.SUB, ALUBSel.instrSource),
    SBCI("Subtracts a constant [c] from register [d] with carry",
            RegsNeeded.dest, ImmedNeeded.Yes, ALUCmd.SBC, ALUBSel.ImReg),
    SBCIs("Subtracts a constant [c] from register [d] with carry",
            RegsNeeded.dest, ImmedNeeded.Yes, ALUCmd.SBC, ALUBSel.instrSource),
    ANDI("Stores [d] and [c] in register [d]",
            RegsNeeded.dest, ImmedNeeded.Yes, ALUCmd.AND, ALUBSel.ImReg),
    ANDIs("Stores [d] and [c] in register [d]",
            RegsNeeded.dest, ImmedNeeded.Yes, ALUCmd.AND, ALUBSel.instrSource),
    ORI("Stores [d] or [c] in register [d]",
            RegsNeeded.dest, ImmedNeeded.Yes, ALUCmd.OR, ALUBSel.ImReg),
    ORIs("Stores [d] or [c] in register [d]",
            RegsNeeded.dest, ImmedNeeded.Yes, ALUCmd.OR, ALUBSel.instrSource),
    XORI("Stores [d] xor [c] in register [d]",
            RegsNeeded.dest, ImmedNeeded.Yes, ALUCmd.XOR, ALUBSel.ImReg),
    XORIs("Stores [d] xor [c] in register [d]",
            RegsNeeded.dest, ImmedNeeded.Yes, ALUCmd.XOR, ALUBSel.instrSource),

    MUL("Multiplies the content of register [s] with register [d] and stores result in [d]",
            RegsNeeded.both, ImmedNeeded.No, ALUCmd.MUL, ALUBSel.Source),
    MULI("Multiplies the constant [c] with register [d] and stores result in [d]",
            RegsNeeded.dest, ImmedNeeded.Yes, ALUCmd.MUL, ALUBSel.ImReg),
    MULIs("Multiplies the constant [c] with register [d] and stores result in [d]",
            RegsNeeded.dest, ImmedNeeded.Yes, ALUCmd.MUL, ALUBSel.instrSource),


    CMP("Subtracts the content of register [s] from register [d] without carry, does not store the value",
            RegsNeeded.both, ImmedNeeded.No, ReadRam.No, WriteRam.No, Branch.No, ALUBSel.Source, ALUToBus.No, ALUCmd.SUB, EnRegWrite.No),
    CPI("Subtracts a constant [c] from register [d] without carry, does not store the value",
            RegsNeeded.dest, ImmedNeeded.Yes, ReadRam.No, WriteRam.No, Branch.No, ALUBSel.ImReg, ALUToBus.No, ALUCmd.SUB, EnRegWrite.No),
    CPIs("Subtracts a constant [c] from register [d] without carry, does not store the value",
            RegsNeeded.dest, ImmedNeeded.Yes, ReadRam.No, WriteRam.No, Branch.No, ALUBSel.instrSource, ALUToBus.No, ALUCmd.SUB, EnRegWrite.No),


    LSL("Shifts register [d] by one bit to the left. A zero is filled in.",
            RegsNeeded.dest, ImmedNeeded.No, ALUCmd.LSL, ALUBSel.Source),
    LSR("Shifts register [d] by one bit to the right. A zero is filled in.",
            RegsNeeded.dest, ImmedNeeded.No, ALUCmd.LSR, ALUBSel.Source),
    ROL("Shifts register [d] by one bit to the left. The carry bit is filled in.",
            RegsNeeded.dest, ImmedNeeded.No, ALUCmd.ROL, ALUBSel.Source),
    ROR("Shifts register [d] by one bit to the right. The carry bit is filled in.",
            RegsNeeded.dest, ImmedNeeded.No, ALUCmd.ROR, ALUBSel.Source),
    ASR("Shifts register [d] by one bit to the right. The MSB remains unchanged.",
            RegsNeeded.dest, ImmedNeeded.No, ALUCmd.ASR, ALUBSel.Source),

    SWAP("Swaps the high and low byte in register [d].",
            RegsNeeded.dest, ImmedNeeded.No, ALUCmd.SWAP, ALUBSel.Source),
    SWAPN("Swaps the high and low nibbles of both bytes in register [d].",
            RegsNeeded.dest, ImmedNeeded.No, ALUCmd.SWAPN, ALUBSel.Source),

    ST("Stores the content of register [s] to the memory at the address ([d])",
            RegsNeeded.both, ImmedNeeded.No, RegIsAddress.dest, ReadRam.No, WriteRam.Yes, Branch.No, ALUBSel.Zero, ALUToBus.No, ALUCmd.ADD, EnRegWrite.No, SourceToAluA.No, StorePC.No, JmpAbs.No, WriteIO.No, ReadIO.No, Break.No),
    LD("Loads the value at memory address ([s]) to register [d]",
            RegsNeeded.both, ImmedNeeded.No, RegIsAddress.source, ReadRam.Yes, WriteRam.No, Branch.No, ALUBSel.Zero, ALUToBus.No, ALUCmd.ADD, EnRegWrite.Yes, SourceToAluA.Yes, StorePC.No, JmpAbs.No, WriteIO.No, ReadIO.No, Break.No),
    STS("Stores the content of register [s] to memory at the location given by [c]",
            RegsNeeded.source, ImmedNeeded.Yes, ReadRam.No, WriteRam.Yes, Branch.No, ALUBSel.ImReg, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.No),
    STSs("Stores the content of register [s] to memory at the location given by [c]",
            RegsNeeded.source, ImmedNeeded.Yes, ReadRam.No, WriteRam.Yes, Branch.No, ALUBSel.instrDest, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.No),
    LDS("Loads the memory value at the location given by [c] to register [d]",
            RegsNeeded.dest, ImmedNeeded.Yes, ReadRam.Yes, WriteRam.No, Branch.No, ALUBSel.ImReg, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.Yes),
    LDSs("Loads the memory value at the location given by [c] to register [d]",
            RegsNeeded.dest, ImmedNeeded.Yes, ReadRam.Yes, WriteRam.No, Branch.No, ALUBSel.instrSource, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.Yes),
    STD("Stores the content of register [s] to the memory at the address ([d]+[c])",
            RegsNeeded.both, ImmedNeeded.Yes, RegIsAddress.dest, ReadRam.No, WriteRam.Yes, Branch.No, ALUBSel.ImReg, ALUToBus.No, ALUCmd.ADD, EnRegWrite.No, SourceToAluA.No, StorePC.No, JmpAbs.No, WriteIO.No, ReadIO.No, Break.No),
    LDD("Loads the value at memory address ([s]+[c]) to register [d]",
            RegsNeeded.both, ImmedNeeded.Yes, RegIsAddress.source, ReadRam.Yes, WriteRam.No, Branch.No, ALUBSel.ImReg, ALUToBus.No, ALUCmd.ADD, EnRegWrite.Yes, SourceToAluA.Yes, StorePC.No, JmpAbs.No, WriteIO.No, ReadIO.No, Break.No),

    BRC("Jumps to the address given by [c] if carry flag is set.",
            RegsNeeded.none, ImmedNeeded.Yes, ReadRam.No, WriteRam.No, Branch.BRC, ALUBSel.instrSourceAndDest, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.No),
    BRZ("Jumps to the address given by [c] if zero flag is set.",
            RegsNeeded.none, ImmedNeeded.Yes, ReadRam.No, WriteRam.No, Branch.BRZ, ALUBSel.instrSourceAndDest, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.No),
    BRN("Jumps to the address given by [c] if negative flag is set.",
            RegsNeeded.none, ImmedNeeded.Yes, ReadRam.No, WriteRam.No, Branch.BRN, ALUBSel.instrSourceAndDest, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.No),
    BRNC("Jumps to the address given by [c] if carry flag is clear.",
            RegsNeeded.none, ImmedNeeded.Yes, ReadRam.No, WriteRam.No, Branch.BRNC, ALUBSel.instrSourceAndDest, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.No),
    BRNZ("Jumps to the address given by [c] if zero flag is clear.",
            RegsNeeded.none, ImmedNeeded.Yes, ReadRam.No, WriteRam.No, Branch.BRNZ, ALUBSel.instrSourceAndDest, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.No),
    BRNN("Jumps to the address given by [c] if negative flag is clear.",
            RegsNeeded.none, ImmedNeeded.Yes, ReadRam.No, WriteRam.No, Branch.BRNN, ALUBSel.instrSourceAndDest, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.No),

    RCALL("Jumps to the address given by [c], the return address is stored in register [d]",
            RegsNeeded.dest, ImmedNeeded.Yes, RegIsAddress.none, ReadRam.No, WriteRam.No, Branch.No, ALUBSel.ImReg, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.Yes, SourceToAluA.No, StorePC.Yes, JmpAbs.Yes, WriteIO.No, ReadIO.No, Break.No),
    RRET("Jumps to the address given by register [s]",
            RegsNeeded.source, ImmedNeeded.No, RegIsAddress.none, ReadRam.No, WriteRam.No, Branch.No, ALUBSel.Source, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.No, SourceToAluA.No, StorePC.No, JmpAbs.Yes, WriteIO.No, ReadIO.No, Break.No),

    JMP("Jumps to the address given by [c]",
            RegsNeeded.none, ImmedNeeded.Yes, RegIsAddress.none, ReadRam.No, WriteRam.No, Branch.No, ALUBSel.ImReg, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.No, SourceToAluA.No, StorePC.No, JmpAbs.Yes, WriteIO.No, ReadIO.No, Break.No),
    JMPs("Jumps to the address given by [c]",
            RegsNeeded.none, ImmedNeeded.Yes, ReadRam.No, WriteRam.No, Branch.uncond, ALUBSel.instrSourceAndDest, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.No),


    OUT("Writes the content of register [s] to io location given by [c]",
            RegsNeeded.source, ImmedNeeded.Yes, RegIsAddress.none, ReadRam.No, WriteRam.No, Branch.No, ALUBSel.ImReg, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.No, SourceToAluA.No, StorePC.No, JmpAbs.No, WriteIO.Yes, ReadIO.No, Break.No),
    OUTs("Writes the content of register [s] to io location given by [c]",
            RegsNeeded.source, ImmedNeeded.Yes, RegIsAddress.none, ReadRam.No, WriteRam.No, Branch.No, ALUBSel.instrDest, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.No, SourceToAluA.No, StorePC.No, JmpAbs.No, WriteIO.Yes, ReadIO.No, Break.No),
    OUTR("Writes the content of register [s] to the io location ([d])",
            RegsNeeded.both, ImmedNeeded.No, RegIsAddress.dest, ReadRam.No, WriteRam.No, Branch.No, ALUBSel.Zero, ALUToBus.No, ALUCmd.ADD, EnRegWrite.No, SourceToAluA.No, StorePC.No, JmpAbs.No, WriteIO.Yes, ReadIO.No, Break.No),


    IN("Reads the io location given by [c] and stores it in register [d]",
            RegsNeeded.dest, ImmedNeeded.Yes, RegIsAddress.none, ReadRam.No, WriteRam.No, Branch.No, ALUBSel.ImReg, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.Yes, SourceToAluA.Yes, StorePC.No, JmpAbs.No, WriteIO.No, ReadIO.Yes, Break.No),
    INs("Reads the io location given by [c] and stores it in register [d]",
            RegsNeeded.dest, ImmedNeeded.Yes, RegIsAddress.none, ReadRam.No, WriteRam.No, Branch.No, ALUBSel.instrSource, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.Yes, SourceToAluA.Yes, StorePC.No, JmpAbs.No, WriteIO.No, ReadIO.Yes, Break.No),
    INR("Reads the io location given by ([s]) and stores it in register [d]",
            RegsNeeded.both, ImmedNeeded.No, RegIsAddress.source, ReadRam.No, WriteRam.No, Branch.No, ALUBSel.Zero, ALUToBus.No, ALUCmd.ADD, EnRegWrite.Yes, SourceToAluA.Yes, StorePC.No, JmpAbs.No, WriteIO.No, ReadIO.Yes, Break.No),

    BRK("Stops execution by disabling the programm counter",
            RegsNeeded.none, ImmedNeeded.No, RegIsAddress.none, ReadRam.No, WriteRam.No, Branch.No, ALUBSel.Source, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.No, SourceToAluA.No, StorePC.No, JmpAbs.No, WriteIO.No, ReadIO.No, Break.Yes);


    public enum RegsNeeded {none, source, dest, both}

    public enum RegIsAddress {none, source, dest}

    public enum ImmedNeeded {No, Yes}

    enum ReadRam {No, Yes}

    enum ReadIO {No, Yes}

    enum WriteRam {No, Yes}

    enum WriteIO {No, Yes}

    enum Break {No, Yes}

    enum SourceToAluA {No, Yes}

    enum Branch {No, BRC, BRZ, BRN, uncond, BRNC, BRNZ, BRNN}

    enum ALUBSel {Source, ImReg, Zero, hFF, hFFF, instrSource, instrSourceAndDest, instrDest}

    enum ALUToBus {No, Yes}

    enum ALUCmd {
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
    private final ALUBSel aluBSel;
    private final ALUToBus aluToBus;
    private final ALUCmd aluCmd;
    private final EnRegWrite enRegWrite;
    private final StorePC storePC;
    private final SourceToAluA sourceToAluA;
    private final JmpAbs jmpAbs;
    private final WriteIO wio;
    private final ReadIO rio;
    private final Break brk;
    private final RegIsAddress regIsAddress;

    private final String description;
    private final RegsNeeded regsNeeded;
    private final ImmedNeeded immedNeeded;

    Opcode(String description, RegsNeeded rn, ImmedNeeded en, RegIsAddress regIsAddress, ReadRam rr, WriteRam wr, Branch br, ALUBSel aluBSel, ALUToBus aluToBus, ALUCmd aluCmd, EnRegWrite enRegWrite, SourceToAluA sourceToAluA, StorePC storePC, JmpAbs jmpAbs, WriteIO wio, ReadIO rio, Break brk) {
        this.regIsAddress = regIsAddress;
        this.description = addConstLimit(description, aluBSel).replace("[d]", DESTREG).replace("[s]", SOURCEREG).replace("[c]", CONSTANT);
        this.regsNeeded = rn;
        this.immedNeeded = en;
        this.rr = rr;
        this.wr = wr;
        this.br = br;
        this.aluBSel = aluBSel;
        this.aluToBus = aluToBus;
        this.aluCmd = aluCmd;
        this.enRegWrite = enRegWrite;
        this.storePC = storePC;
        this.sourceToAluA = sourceToAluA;
        this.jmpAbs = jmpAbs;
        this.wio = wio;
        this.rio = rio;
        this.brk = brk;
    }

    private String addConstLimit(String description, ALUBSel imed) {
        if (imed.equals(ALUBSel.instrDest) || imed.equals(ALUBSel.instrSource)) {
            description += " (0<=[c]<=31)";
        } else {
            if (imed.equals(ALUBSel.instrSourceAndDest)) {
                description += " (-256<=[c]<=255)";
            }
        }
        description += " (Opcode 0x" + Integer.toHexString(this.ordinal()) + ")";
        return description;
    }

    Opcode(String description, RegsNeeded rn, ImmedNeeded en, ReadRam rr, WriteRam wr, Branch br, ALUBSel aluBSel, ALUToBus aluToBus, ALUCmd aluCmd, EnRegWrite enRegWrite, SourceToAluA sta) {
        this(description, rn, en, RegIsAddress.none, rr, wr, br, aluBSel, aluToBus, aluCmd, enRegWrite, sta, StorePC.No, JmpAbs.No, WriteIO.No, ReadIO.No, Break.No);
    }

    Opcode(String description, RegsNeeded rn, ImmedNeeded en, ReadRam rr, WriteRam wr, Branch br, ALUBSel aluBSel, ALUToBus aluToBus, ALUCmd aluCmd, EnRegWrite enRegWrite) {
        this(description, rn, en, rr, wr, br, aluBSel, aluToBus, aluCmd, enRegWrite, SourceToAluA.No);
    }


    // Simple operation
    Opcode(String description, RegsNeeded rn, ImmedNeeded en, ALUCmd aluCmd, ALUBSel aluBSel) {
        this(description, rn, en,
                ReadRam.No,
                WriteRam.No,
                Branch.No,
                aluBSel,
                ALUToBus.Yes,
                aluCmd,
                EnRegWrite.Yes);
    }

    int createControlWord() {
        return rr.ordinal()
                | (wr.ordinal() << 1)
                | (aluBSel.ordinal() << 2)
                | (jmpAbs.ordinal() << 5)
                | (aluToBus.ordinal() << 6)
                | (enRegWrite.ordinal() << 7)
                | (storePC.ordinal() << 8)
                | (sourceToAluA.ordinal() << 9)

                | (aluCmd.ordinal() << 10)
                | (br.ordinal() << 15)
                | (wio.ordinal() << 18)
                | (rio.ordinal() << 19)
                | (brk.ordinal() << 20);
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

    public ALUBSel getALUBSel() {
        return aluBSel;
    }

    public ALUToBus getAluToBus() {
        return aluToBus;
    }

    public ReadRam getReadRam() {
        return rr;
    }

    public ReadRam getRr() {
        return rr;
    }

    public ReadIO getReadIO() {
        return rio;
    }

    public StorePC getStorePC() {
        return storePC;
    }

    public EnRegWrite getEnRegWrite() {
        return enRegWrite;
    }

    public RegIsAddress getRegIsAddress() {
        return regIsAddress;
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
        try (PrintStream p = new PrintStream("/home/hneemann/Dokumente/DHBW/Technische_Informatik_II/Systemnahes_Programmieren/java/assembler3/control.dat")) {
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
