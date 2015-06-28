package de.neemann.assembler.asm;

import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * Defines the opcodes and creates the table for the control unit from the opcode description.
 *
 * @author hneemann
 */
public enum Opcode {
    NOP(RegsNeeded.none, ImmedNeeded.No, ReadRam.No, WriteRam.No, Branch.No, Immed.No, StoreSel.ALU, ALU.Nothing, EnRegWrite.No),
    MOV(RegsNeeded.both, ImmedNeeded.No, ALU.Nothing, Immed.No),
    ADD(RegsNeeded.both, ImmedNeeded.No, ALU.ADD, Immed.No),
    ADC(RegsNeeded.both, ImmedNeeded.No, ALU.ADC, Immed.No),
    SUB(RegsNeeded.both, ImmedNeeded.No, ALU.SUB, Immed.No),
    SBC(RegsNeeded.both, ImmedNeeded.No, ALU.SBC, Immed.No),
    AND(RegsNeeded.both, ImmedNeeded.No, ALU.AND, Immed.No),
    OR(RegsNeeded.both, ImmedNeeded.No, ALU.OR, Immed.No),
    XOR(RegsNeeded.both, ImmedNeeded.No, ALU.XOR, Immed.No),
    LDI(RegsNeeded.dest, ImmedNeeded.Yes, ALU.Nothing, Immed.Regist),
    ADDI(RegsNeeded.dest, ImmedNeeded.Yes, ALU.ADD, Immed.Regist),
    ADCI(RegsNeeded.dest, ImmedNeeded.Yes, ALU.ADC, Immed.Regist),
    SUBI(RegsNeeded.dest, ImmedNeeded.Yes, ALU.SUB, Immed.Regist),
    SBCI(RegsNeeded.dest, ImmedNeeded.Yes, ALU.SBC, Immed.Regist),
    ANDI(RegsNeeded.dest, ImmedNeeded.Yes, ALU.AND, Immed.Regist),
    ORI(RegsNeeded.dest, ImmedNeeded.Yes, ALU.OR, Immed.Regist),
    XORI(RegsNeeded.dest, ImmedNeeded.Yes, ALU.XOR, Immed.Regist),

    INC(RegsNeeded.dest, ImmedNeeded.No, ALU.ADD, Immed.One),
    DEC(RegsNeeded.dest, ImmedNeeded.No, ALU.SUB, Immed.One),

    STS(RegsNeeded.source, ImmedNeeded.Yes, ReadRam.No, WriteRam.Yes, Branch.No, Immed.Regist, StoreSel.ALU, ALU.Nothing, EnRegWrite.No),
    LDS(RegsNeeded.dest, ImmedNeeded.Yes, ReadRam.Yes, WriteRam.No, Branch.No, Immed.Regist, StoreSel.RAM, ALU.Nothing, EnRegWrite.Yes),
    // ST R0,R1,ofs  => (R1+ofs)=R0
    ST(RegsNeeded.both, ImmedNeeded.Yes, ReadRam.No, WriteRam.Yes, Branch.No, Immed.Regist, StoreSel.ALU, ALU.ADD, EnRegWrite.No),
    // LD R0,R1,ofs  => R0=(R1+ofs)
    LD(RegsNeeded.both, ImmedNeeded.Yes, ReadRam.Yes, WriteRam.No, Branch.No, Immed.Regist, StoreSel.RAM, ALU.ADD, EnRegWrite.Yes, StorePC.No, SourceToAlu.Yes),

    JMP(RegsNeeded.none, ImmedNeeded.Yes, ReadRam.No, WriteRam.No, Branch.uncond, Immed.instr, StoreSel.RAM, ALU.Nothing, EnRegWrite.No),
    BRC(RegsNeeded.none, ImmedNeeded.Yes, ReadRam.No, WriteRam.No, Branch.BRC, Immed.instr, StoreSel.RAM, ALU.Nothing, EnRegWrite.No),
    BRZ(RegsNeeded.none, ImmedNeeded.Yes, ReadRam.No, WriteRam.No, Branch.BRZ, Immed.instr, StoreSel.RAM, ALU.Nothing, EnRegWrite.No),
    BRNC(RegsNeeded.none, ImmedNeeded.Yes, ReadRam.No, WriteRam.No, Branch.BRNC, Immed.instr, StoreSel.RAM, ALU.Nothing, EnRegWrite.No),
    BRNZ(RegsNeeded.none, ImmedNeeded.Yes, ReadRam.No, WriteRam.No, Branch.BRNZ, Immed.instr, StoreSel.RAM, ALU.Nothing, EnRegWrite.No),;

    enum RegsNeeded {none, source, dest, both}

    enum ImmedNeeded {No, Yes}

    enum ReadRam {No, Yes}

    enum WriteRam {No, Yes}

    enum SourceToAlu {No, Yes}

    enum Branch {No, BRC, BRZ, uncond, res, BRNC, BRNZ}

    enum Immed {No, Regist, One, instr}

    enum StoreSel {RAM, ALU}

    enum ALU {Nothing, ADD, SUB, AND, OR, XOR, LSL, LSR, res1, ADC, SBC, res2, res3, res4, ASR, ASL}

    enum EnRegWrite {No, Yes}

    enum StorePC {No, Yes}

    private final ReadRam rr;
    private final WriteRam wr;
    private final Branch br;
    private final Immed imed;
    private final StoreSel storeSel;
    private final ALU alu;
    private final EnRegWrite enRegWrite;
    private final StorePC storePC;
    private final SourceToAlu sourceToAlu;

    private final RegsNeeded regsNeeded;
    private final ImmedNeeded immedNeeded;

    Opcode(RegsNeeded rn, ImmedNeeded en, ReadRam rr, WriteRam wr, Branch br, Immed imed, StoreSel storeSel, ALU alu, EnRegWrite enRegWrite, StorePC storePC, SourceToAlu sourceToAlu) {
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
    }

    Opcode(RegsNeeded rn, ImmedNeeded en, ReadRam rr, WriteRam wr, Branch br, Immed imed, StoreSel storeSel, ALU alu, EnRegWrite enRegWrite) {
        this(rn, en, rr, wr, br, imed, storeSel, alu, enRegWrite, StorePC.No, SourceToAlu.No);
    }


    // Simple operation
    Opcode(RegsNeeded rn, ImmedNeeded en, ALU alu, Immed immed) {
        this(rn, en,
                ReadRam.No,
                WriteRam.No,
                Branch.No,
                immed,
                StoreSel.ALU,
                alu,
                EnRegWrite.Yes);
    }


    int createControlWord() {
        return rr.ordinal()
                | (wr.ordinal() << 1)
                | (imed.ordinal() << 2)
                | (storeSel.ordinal() << 4)
                | (enRegWrite.ordinal() << 5)
                | (storePC.ordinal() << 6)
                | (sourceToAlu.ordinal() << 7)

                | (alu.ordinal() << 8)
                | (br.ordinal() << 13)
                ;
    }

    public RegsNeeded getRegsNeeded() {
        return regsNeeded;
    }

    public ImmedNeeded getImmedNeeded() {
        return immedNeeded;
    }

    public Immed getImmed() {
        return imed;
    }

    public static void writeControlWords(PrintStream out) {
        out.println("v2.0 raw");
        for (Opcode oc : Opcode.values())
            out.println(Integer.toHexString(oc.createControlWord()));
    }

    public static void main(String[] args) throws FileNotFoundException {
        writeControlWords(System.out);
        System.out.println(Opcode.values().length + " opcodes");
        try (PrintStream p = new PrintStream("/home/hneemann/Dokumente/DHBW/Technische_Informatik_I/Vorlesung/06_Prozessoren/java/assembler3/control.dat")) {
            writeControlWords(p);
        }
    }

}
