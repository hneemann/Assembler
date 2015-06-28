package de.neemann.assembler.asm;

import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * @author hneemann
 */
public enum Opcode {
    NOP(ReadRam.No, WriteRam.No, Branch.No, Immed.No, StoreSel.ALU, ALU.Nothing, EnRegWrite.No, StorePC.No, SourceDef.Instr, 0, DestDef.Instr, 0),
    MOV(ALU.Nothing, Immed.No),
    ADD(ALU.ADD, Immed.No),
    ADC(ALU.ADC, Immed.No),
    SUB(ALU.SUB, Immed.No),
    SBC(ALU.SBC, Immed.No),
    AND(ALU.AND, Immed.No),
    OR(ALU.OR, Immed.No),
    LDI(ALU.Nothing, Immed.Yes),
    XOR(ALU.XOR, Immed.No),
    ADDI(ALU.ADD, Immed.Yes),
    ADCI(ALU.ADC, Immed.Yes),
    SUBI(ALU.SUB, Immed.Yes),
    SBCI(ALU.SBC, Immed.Yes),
    ANDI(ALU.AND, Immed.Yes),
    ORI(ALU.OR, Immed.Yes),
    XORI(ALU.XOR, Immed.Yes),;

    enum ReadRam {No, Yes}

    ;

    enum WriteRam {No, Yes}

    ;

    enum Branch {No, Yes}

    ;

    enum Immed {No, Yes}

    ;

    enum StoreSel {RAM, ALU}

    ;

    enum ALU {Nothing, ADD, SUB, AND, OR, XOR, LSL, LSR, res1, ADC, SBC, res2, res3, res4, ASR, ASL}

    ;

    enum SourceDef {Instr, Control}

    ;

    enum DestDef {Instr, Control}

    ;

    enum EnRegWrite {No, Yes}

    ;

    enum StorePC {No, Yes}

    ;

    private final ReadRam rr;
    private final WriteRam wr;
    private final Branch br;
    private final Immed imed;
    private final StoreSel storeSel;
    private final ALU alu;
    private final SourceDef sourceDef;
    private final DestDef destDef;
    private final EnRegWrite enRegWrite;
    private final StorePC storePC;
    private final int sourceReg;
    private final int destReg;

    Opcode(ReadRam rr, WriteRam wr, Branch br, Immed imed, StoreSel storeSel, ALU alu, EnRegWrite enRegWrite, StorePC storePC, SourceDef sourceDef, int sourceReg, DestDef destDef, int destReg) {
        this.rr = rr;
        this.wr = wr;
        this.br = br;
        this.imed = imed;
        this.storeSel = storeSel;
        this.alu = alu;
        this.sourceDef = sourceDef;
        this.destDef = destDef;
        this.enRegWrite = enRegWrite;
        this.storePC = storePC;
        this.sourceReg = sourceReg;
        this.destReg = destReg;
    }

    // Simple operation
    Opcode(ALU alu, Immed immed) {
        this(ReadRam.No,
                WriteRam.No,
                Branch.No,
                immed,
                StoreSel.ALU,
                alu,
                EnRegWrite.Yes,
                StorePC.No,
                SourceDef.Instr, 0,
                DestDef.Instr, 0);
    }


    int createControlWord() {
        return rr.ordinal()
                | (wr.ordinal() << 1)
                | (br.ordinal() << 2)
                | (imed.ordinal() << 3)
                | (storeSel.ordinal() << 4)
                | (enRegWrite.ordinal() << 5)
                | (storePC.ordinal() << 6)
                | (sourceDef.ordinal() << 7)
                | (destDef.ordinal() << 8)

                | (alu.ordinal() << 9)
                | (sourceReg << 14)
                | (destReg << 18)
                ;
    }

    public static void writeControlWords(PrintStream out) {
        out.println("v2.0 raw");
        for (Opcode oc : Opcode.values())
            out.println(Integer.toHexString(oc.createControlWord()));
    }

    public static void main(String[] args) throws FileNotFoundException {
        writeControlWords(System.out);
        try (PrintStream p = new PrintStream("/home/hneemann/Dokumente/DHBW/Technische_Informatik_I/Vorlesung/06_Prozessoren/java/assembler3/control.dat")) {
            writeControlWords(p);
        }
    }

}
