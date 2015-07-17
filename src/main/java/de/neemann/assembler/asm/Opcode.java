package de.neemann.assembler.asm;

import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * Defines the opcodes and creates the table for the control unit from the opcode description.
 *
 * @author hneemann
 */
public enum Opcode {
    NOP("does nothing", MnemonicArguments.NOTHING,
            ReadRam.No, WriteRam.No, Branch.No, ALUBSel.Source, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.No),
    MOV("Move the content of Rs to register Rd",
            MnemonicArguments.DEST_SOURCE, ALUCmd.Nothing, ALUBSel.Source),
    ADD("Adds the content of register Rs to register Rd without carry",
            MnemonicArguments.DEST_SOURCE, ALUCmd.ADD, ALUBSel.Source),
    ADC("Adds the content of register Rs to register Rd with carry",
            MnemonicArguments.DEST_SOURCE, ALUCmd.ADC, ALUBSel.Source),
    SUB("Subtracts the content of register Rs from register Rd without carry",
            MnemonicArguments.DEST_SOURCE, ALUCmd.SUB, ALUBSel.Source),
    SBC("Subtracts the content of register Rs from register Rd with carry",
            MnemonicArguments.DEST_SOURCE, ALUCmd.SBC, ALUBSel.Source),
    AND("Stores Rs and Rd in register Rd.",
            MnemonicArguments.DEST_SOURCE, ALUCmd.AND, ALUBSel.Source),
    OR("Stores Rs or Rd in register Rd.",
            MnemonicArguments.DEST_SOURCE, ALUCmd.OR, ALUBSel.Source),
    EOR("Stores Rs xor Rd in register Rd.",
            MnemonicArguments.DEST_SOURCE, ALUCmd.XOR, ALUBSel.Source),
    LDI("Loads Register Rd with the constant value const",
            MnemonicArguments.DEST_CONST, ALUCmd.Nothing, ALUBSel.ImReg),
    LDIs("Loads Register Rd with the constant value const",
            MnemonicArguments.DEST_CONST, ALUCmd.Nothing, ALUBSel.instrSource),
    ADDI("Adds the constant const to register Rd without carry",
            MnemonicArguments.DEST_CONST, ALUCmd.ADD, ALUBSel.ImReg),
    ADDIs("Adds the constant const to register Rd without carry",
            MnemonicArguments.DEST_CONST, ALUCmd.ADD, ALUBSel.instrSource),
    ADCI("Adds the constant const to register Rd with carry",
            MnemonicArguments.DEST_CONST, ALUCmd.ADC, ALUBSel.ImReg),
    ADCIs("Adds the constant const to register Rd with carry",
            MnemonicArguments.DEST_CONST, ALUCmd.ADC, ALUBSel.instrSource),
    SUBI("Subtracts a constant const from register Rd without carry",
            MnemonicArguments.DEST_CONST, ALUCmd.SUB, ALUBSel.ImReg),
    SUBIs("Subtracts a constant const from register Rd without carry",
            MnemonicArguments.DEST_CONST, ALUCmd.SUB, ALUBSel.instrSource),
    SBCI("Subtracts a constant const from register Rd with carry",
            MnemonicArguments.DEST_CONST, ALUCmd.SBC, ALUBSel.ImReg),
    SBCIs("Subtracts a constant const from register Rd with carry",
            MnemonicArguments.DEST_CONST, ALUCmd.SBC, ALUBSel.instrSource),
    ANDI("Stores Rd and const in register Rd",
            MnemonicArguments.DEST_CONST, ALUCmd.AND, ALUBSel.ImReg),
    ANDIs("Stores Rd and const in register Rd",
            MnemonicArguments.DEST_CONST, ALUCmd.AND, ALUBSel.instrSource),
    ORI("Stores Rd or const in register Rd",
            MnemonicArguments.DEST_CONST, ALUCmd.OR, ALUBSel.ImReg),
    ORIs("Stores Rd or const in register Rd",
            MnemonicArguments.DEST_CONST, ALUCmd.OR, ALUBSel.instrSource),
    EORI("Stores Rd xor const in register Rd",
            MnemonicArguments.DEST_CONST, ALUCmd.XOR, ALUBSel.ImReg),
    EORIs("Stores Rd xor const in register Rd",
            MnemonicArguments.DEST_CONST, ALUCmd.XOR, ALUBSel.instrSource),

    MUL("Multiplies the content of register Rs with register Rd and stores result in Rd",
            MnemonicArguments.DEST_SOURCE, ALUCmd.MUL, ALUBSel.Source),
    MULI("Multiplies the constant const with register Rd and stores result in Rd",
            MnemonicArguments.DEST_CONST, ALUCmd.MUL, ALUBSel.ImReg),
    MULIs("Multiplies the constant const with register Rd and stores result in Rd",
            MnemonicArguments.DEST_CONST, ALUCmd.MUL, ALUBSel.instrSource),


    CMP("Subtracts the content of register Rs from register Rd without carry, does not store the value",
            MnemonicArguments.DEST_SOURCE, ReadRam.No, WriteRam.No, Branch.No, ALUBSel.Source, ALUToBus.No, ALUCmd.SUB, EnRegWrite.No),
    CPI("Subtracts a constant const from register Rd without carry, does not store the value",
            MnemonicArguments.DEST_CONST, ReadRam.No, WriteRam.No, Branch.No, ALUBSel.ImReg, ALUToBus.No, ALUCmd.SUB, EnRegWrite.No),
    CPIs("Subtracts a constant const from register Rd without carry, does not store the value",
            MnemonicArguments.DEST_CONST, ReadRam.No, WriteRam.No, Branch.No, ALUBSel.instrSource, ALUToBus.No, ALUCmd.SUB, EnRegWrite.No),


    LSL("Shifts register Rd by one bit to the left. A zero is filled in.",
            MnemonicArguments.DEST, ALUCmd.LSL, ALUBSel.Source),
    LSR("Shifts register Rd by one bit to the right. A zero is filled in.",
            MnemonicArguments.DEST, ALUCmd.LSR, ALUBSel.Source),
    ROL("Shifts register Rd by one bit to the left. The carry bit is filled in.",
            MnemonicArguments.DEST, ALUCmd.ROL, ALUBSel.Source),
    ROR("Shifts register Rd by one bit to the right. The carry bit is filled in.",
            MnemonicArguments.DEST, ALUCmd.ROR, ALUBSel.Source),
    ASR("Shifts register Rd by one bit to the right. The MSB remains unchanged.",
            MnemonicArguments.DEST, ALUCmd.ASR, ALUBSel.Source),

    SWAP("Swaps the high and low byte in register Rd.",
            MnemonicArguments.DEST, ALUCmd.SWAP, ALUBSel.Source),
    SWAPN("Swaps the high and low nibbles of both bytes in register Rd.",
            MnemonicArguments.DEST, ALUCmd.SWAPN, ALUBSel.Source),

    ST("Stores the content of register Rs to the memory at the address (Rd)",
            MnemonicArguments.BDEST_SOURCE, ReadRam.No, WriteRam.Yes, Branch.No, ALUBSel.Zero, ALUToBus.No, ALUCmd.ADD, EnRegWrite.No, SourceToAluA.No, StorePC.No, JmpAbs.No, WriteIO.No, ReadIO.No, Break.No),
    LD("Loads the value at memory address (Rs) to register Rd",
            MnemonicArguments.DEST_BSOURCE, ReadRam.Yes, WriteRam.No, Branch.No, ALUBSel.Zero, ALUToBus.No, ALUCmd.ADD, EnRegWrite.Yes, SourceToAluA.Yes, StorePC.No, JmpAbs.No, WriteIO.No, ReadIO.No, Break.No),
    STS("Stores the content of register Rs to memory at the location given by const",
            MnemonicArguments.CONST_SOURCE, ReadRam.No, WriteRam.Yes, Branch.No, ALUBSel.ImReg, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.No),
    STSs("Stores the content of register Rs to memory at the location given by const",
            MnemonicArguments.CONST_SOURCE, ReadRam.No, WriteRam.Yes, Branch.No, ALUBSel.instrDest, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.No),
    LDS("Loads the memory value at the location given by const to register Rd",
            MnemonicArguments.DEST_CONST, ReadRam.Yes, WriteRam.No, Branch.No, ALUBSel.ImReg, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.Yes),
    LDSs("Loads the memory value at the location given by const to register Rd",
            MnemonicArguments.DEST_CONST, ReadRam.Yes, WriteRam.No, Branch.No, ALUBSel.instrSource, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.Yes),
    STD("Stores the content of register Rs to the memory at the address (Rd+const)",
            MnemonicArguments.BDEST_BCONST_SOURCE, ReadRam.No, WriteRam.Yes, Branch.No, ALUBSel.ImReg, ALUToBus.No, ALUCmd.ADD, EnRegWrite.No, SourceToAluA.No, StorePC.No, JmpAbs.No, WriteIO.No, ReadIO.No, Break.No),
    LDD("Loads the value at memory address (Rs+const) to register Rd",
            MnemonicArguments.DEST_BSOURCE_BCONST, ReadRam.Yes, WriteRam.No, Branch.No, ALUBSel.ImReg, ALUToBus.No, ALUCmd.ADD, EnRegWrite.Yes, SourceToAluA.Yes, StorePC.No, JmpAbs.No, WriteIO.No, ReadIO.No, Break.No),

    BRC("Jumps to the address given by const if carry flag is set.",
            MnemonicArguments.CONST, ReadRam.No, WriteRam.No, Branch.BRC, ALUBSel.instrSourceAndDest, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.No),
    BRZ("Jumps to the address given by const if zero flag is set.",
            MnemonicArguments.CONST, ReadRam.No, WriteRam.No, Branch.BRZ, ALUBSel.instrSourceAndDest, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.No),
    BRN("Jumps to the address given by const if negative flag is set.",
            MnemonicArguments.CONST, ReadRam.No, WriteRam.No, Branch.BRN, ALUBSel.instrSourceAndDest, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.No),
    BRNC("Jumps to the address given by const if carry flag is clear.",
            MnemonicArguments.CONST, ReadRam.No, WriteRam.No, Branch.BRNC, ALUBSel.instrSourceAndDest, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.No),
    BRNZ("Jumps to the address given by const if zero flag is clear.",
            MnemonicArguments.CONST, ReadRam.No, WriteRam.No, Branch.BRNZ, ALUBSel.instrSourceAndDest, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.No),
    BRNN("Jumps to the address given by const if negative flag is clear.",
            MnemonicArguments.CONST, ReadRam.No, WriteRam.No, Branch.BRNN, ALUBSel.instrSourceAndDest, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.No),

    RCALL("Jumps to the address given by const, the return address is stored in register Rd",
            MnemonicArguments.DEST_CONST, ReadRam.No, WriteRam.No, Branch.No, ALUBSel.ImReg, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.Yes, SourceToAluA.No, StorePC.Yes, JmpAbs.Yes, WriteIO.No, ReadIO.No, Break.No),
    RRET("Jumps to the address given by register Rs",
            MnemonicArguments.SOURCE, ReadRam.No, WriteRam.No, Branch.No, ALUBSel.Source, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.No, SourceToAluA.No, StorePC.No, JmpAbs.Yes, WriteIO.No, ReadIO.No, Break.No),

    JMP("Jumps to the address given by const",
            MnemonicArguments.CONST, ReadRam.No, WriteRam.No, Branch.No, ALUBSel.ImReg, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.No, SourceToAluA.No, StorePC.No, JmpAbs.Yes, WriteIO.No, ReadIO.No, Break.No),
    JMPs("Jumps to the address given by const",
            MnemonicArguments.CONST, ReadRam.No, WriteRam.No, Branch.uncond, ALUBSel.instrSourceAndDest, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.No),


    OUT("Writes the content of register Rs to io location given by const",
            MnemonicArguments.CONST_SOURCE, ReadRam.No, WriteRam.No, Branch.No, ALUBSel.ImReg, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.No, SourceToAluA.No, StorePC.No, JmpAbs.No, WriteIO.Yes, ReadIO.No, Break.No),
    OUTs("Writes the content of register Rs to io location given by const",
            MnemonicArguments.CONST_SOURCE, ReadRam.No, WriteRam.No, Branch.No, ALUBSel.instrDest, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.No, SourceToAluA.No, StorePC.No, JmpAbs.No, WriteIO.Yes, ReadIO.No, Break.No),
    OUTR("Writes the content of register Rs to the io location (Rd)",
            MnemonicArguments.BDEST_SOURCE, ReadRam.No, WriteRam.No, Branch.No, ALUBSel.Zero, ALUToBus.No, ALUCmd.ADD, EnRegWrite.No, SourceToAluA.No, StorePC.No, JmpAbs.No, WriteIO.Yes, ReadIO.No, Break.No),


    IN("Reads the io location given by const and stores it in register Rd",
            MnemonicArguments.DEST_CONST, ReadRam.No, WriteRam.No, Branch.No, ALUBSel.ImReg, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.Yes, SourceToAluA.Yes, StorePC.No, JmpAbs.No, WriteIO.No, ReadIO.Yes, Break.No),
    INs("Reads the io location given by const and stores it in register Rd",
            MnemonicArguments.DEST_CONST, ReadRam.No, WriteRam.No, Branch.No, ALUBSel.instrSource, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.Yes, SourceToAluA.Yes, StorePC.No, JmpAbs.No, WriteIO.No, ReadIO.Yes, Break.No),
    INR("Reads the io location given by (Rs) and stores it in register Rd",
            MnemonicArguments.DEST_BSOURCE, ReadRam.No, WriteRam.No, Branch.No, ALUBSel.Zero, ALUToBus.No, ALUCmd.ADD, EnRegWrite.Yes, SourceToAluA.Yes, StorePC.No, JmpAbs.No, WriteIO.No, ReadIO.Yes, Break.No),

    BRK("Stops execution by disabling the programm counter",
            MnemonicArguments.NOTHING, ReadRam.No, WriteRam.No, Branch.No, ALUBSel.Source, ALUToBus.No, ALUCmd.Nothing, EnRegWrite.No, SourceToAluA.No, StorePC.No, JmpAbs.No, WriteIO.No, ReadIO.No, Break.Yes);


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

    private final String description;
    private final MnemonicArguments arguments;

    Opcode(String description, MnemonicArguments arguments, ReadRam rr, WriteRam wr, Branch br, ALUBSel aluBSel, ALUToBus aluToBus, ALUCmd aluCmd, EnRegWrite enRegWrite, SourceToAluA sourceToAluA, StorePC storePC, JmpAbs jmpAbs, WriteIO wio, ReadIO rio, Break brk) {
        this.arguments = arguments;
        this.description = addConstLimit(description, aluBSel);
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
            description += " (0<=const<=31)";
        } else {
            if (imed.equals(ALUBSel.instrSourceAndDest)) {
                description += " (-256<=const<=255)";
            }
        }
        description += " (Opcode 0x" + Integer.toHexString(this.ordinal()) + ")";
        return description;
    }

    Opcode(String description, MnemonicArguments arguments, ReadRam rr, WriteRam wr, Branch br, ALUBSel aluBSel, ALUToBus aluToBus, ALUCmd aluCmd, EnRegWrite enRegWrite, SourceToAluA sta) {
        this(description, arguments, rr, wr, br, aluBSel, aluToBus, aluCmd, enRegWrite, sta, StorePC.No, JmpAbs.No, WriteIO.No, ReadIO.No, Break.No);
    }

    Opcode(String description, MnemonicArguments arguments, ReadRam rr, WriteRam wr, Branch br, ALUBSel aluBSel, ALUToBus aluToBus, ALUCmd aluCmd, EnRegWrite enRegWrite) {
        this(description, arguments, rr, wr, br, aluBSel, aluToBus, aluCmd, enRegWrite, SourceToAluA.No);
    }


    // Simple operation
    Opcode(String description, MnemonicArguments arguments, ALUCmd aluCmd, ALUBSel aluBSel) {
        this(description, arguments,
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

    public MnemonicArguments getArguments() {
        return arguments;
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
        return name() + " " + arguments.toString() + "\n\t" + description;
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
