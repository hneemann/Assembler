package de.neemann.assembler.asm;

import java.io.PrintStream;

/**
 * Defines the opcodes and creates the table for the control unit from the opcode description.
 *
 * @author hneemann
 */
public enum Opcode {
    //CHECKSTYLE.OFF: JavadocVariable
    NOP("Does nothing.", MnemonicArguments.NOTHING, new Flags()),
    MOV("Move the content of Rs to register Rd.",
            MnemonicArguments.DEST_SOURCE, new Flags()
            .set(SrcToBus.Yes)
            .set(EnRegWrite.Yes)),
    ADD("Adds the content of register Rs to register Rd without carry.",
            MnemonicArguments.DEST_SOURCE, new Flags()
            .set(ALUCmd.ADD)
            .set(StoreFlags.Yes)
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)),
    ADC("Adds the content of register Rs to register Rd with carry.",
            MnemonicArguments.DEST_SOURCE, new Flags()
            .set(ALUCmd.ADC)
            .set(StoreFlags.Yes)
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)),
    SUB("Subtracts the content of register Rs from register Rd without carry.",
            MnemonicArguments.DEST_SOURCE, new Flags()
            .set(ALUCmd.SUB)
            .set(StoreFlags.Yes)
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)),
    SBC("Subtracts the content of register Rs from register Rd with carry.",
            MnemonicArguments.DEST_SOURCE, new Flags()
            .set(ALUCmd.SBC)
            .set(StoreFlags.Yes)
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)),
    AND("Stores Rs and Rd in register Rd.",
            MnemonicArguments.DEST_SOURCE, new Flags()
            .set(ALUCmd.AND)
            .set(StoreFlags.Yes)
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)),
    OR("Stores Rs or Rd in register Rd.",
            MnemonicArguments.DEST_SOURCE, new Flags()
            .set(ALUCmd.OR)
            .set(StoreFlags.Yes)
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)),
    EOR("Stores Rs xor Rd in register Rd.",
            MnemonicArguments.DEST_SOURCE, new Flags()
            .set(ALUCmd.XOR)
            .set(StoreFlags.Yes)
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)),
    LDI("Loads Register Rd with the constant value [const].",
            MnemonicArguments.DEST_CONST, new Flags()
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)
            .set(ImmExtMode.src0)
            .set(ALUBSel.ImReg)),
    LDIs("Loads Register Rd with the constant value [const].",
            MnemonicArguments.DEST_CONST, new Flags()
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)
            .set(ALUBSel.instrSource)),
    ADDI("Adds the constant [const] to register Rd without carry.",
            MnemonicArguments.DEST_CONST, new Flags()
            .set(ALUCmd.ADD)
            .set(StoreFlags.Yes)
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)
            .set(ImmExtMode.src0)
            .set(ALUBSel.ImReg)),
    ADDIs("Adds the constant [const] to register Rd without carry.",
            MnemonicArguments.DEST_CONST, new Flags()
            .set(ALUCmd.ADD)
            .set(StoreFlags.Yes)
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)
            .set(ALUBSel.instrSource)),
    ADCI("Adds the constant [const] to register Rd with carry.",
            MnemonicArguments.DEST_CONST, new Flags()
            .set(ALUCmd.ADC)
            .set(StoreFlags.Yes)
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)
            .set(ImmExtMode.src0)
            .set(ALUBSel.ImReg)),
    ADCIs("Adds the constant [const] to register Rd with carry.",
            MnemonicArguments.DEST_CONST, new Flags()
            .set(ALUCmd.ADC)
            .set(ALUToBus.Yes)
            .set(StoreFlags.Yes)
            .set(EnRegWrite.Yes)
            .set(ALUBSel.instrSource)),
    SUBI("Subtracts a constant [const] from register Rd without carry.",
            MnemonicArguments.DEST_CONST, new Flags()
            .set(ALUCmd.SUB)
            .set(StoreFlags.Yes)
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)
            .set(ImmExtMode.src0)
            .set(ALUBSel.ImReg)),
    SUBIs("Subtracts a constant [const] from register Rd without carry.",
            MnemonicArguments.DEST_CONST, new Flags()
            .set(ALUCmd.SUB)
            .set(StoreFlags.Yes)
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)
            .set(ALUBSel.instrSource)),
    SBCI("Subtracts a constant [const] from register Rd with carry.",
            MnemonicArguments.DEST_CONST, new Flags()
            .set(ALUCmd.SBC)
            .set(ALUToBus.Yes)
            .set(StoreFlags.Yes)
            .set(EnRegWrite.Yes)
            .set(ImmExtMode.src0)
            .set(ALUBSel.ImReg)),
    SBCIs("Subtracts a constant [const] from register Rd with carry.",
            MnemonicArguments.DEST_CONST, new Flags()
            .set(ALUCmd.SBC)
            .set(StoreFlags.Yes)
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)
            .set(ALUBSel.instrSource)),

    NEG("Stores the two's complement of Rd in register Rd.",
            MnemonicArguments.DEST, new Flags()
            .set(ALUCmd.NEG)
            .set(StoreFlags.No)
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)),

    ANDI("Stores Rd and [const] in register Rd.",
            MnemonicArguments.DEST_CONST, new Flags()
            .set(ALUCmd.AND)
            .set(StoreFlags.Yes)
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)
            .set(ImmExtMode.src0)
            .set(ALUBSel.ImReg)),
    ANDIs("Stores Rd and [const] in register Rd.",
            MnemonicArguments.DEST_CONST, new Flags()
            .set(ALUCmd.AND)
            .set(StoreFlags.Yes)
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)
            .set(ALUBSel.instrSource)),
    ORI("Stores Rd or [const] in register Rd.",
            MnemonicArguments.DEST_CONST, new Flags()
            .set(ALUCmd.OR)
            .set(StoreFlags.Yes)
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)
            .set(ImmExtMode.src0)
            .set(ALUBSel.ImReg)),
    ORIs("Stores Rd or [const] in register Rd.",
            MnemonicArguments.DEST_CONST, new Flags()
            .set(ALUCmd.OR)
            .set(StoreFlags.Yes)
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)
            .set(ALUBSel.instrSource)),
    EORI("Stores Rd xor [const] in register Rd.",
            MnemonicArguments.DEST_CONST, new Flags()
            .set(ALUCmd.XOR)
            .set(StoreFlags.Yes)
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)
            .set(ImmExtMode.src0)
            .set(ALUBSel.ImReg)),
    EORIs("Stores Rd xor [const] in register Rd.",
            MnemonicArguments.DEST_CONST, new Flags()
            .set(ALUCmd.XOR)
            .set(ALUToBus.Yes)
            .set(StoreFlags.Yes)
            .set(EnRegWrite.Yes)
            .set(ALUBSel.instrSource)),

    NOT("Stores not Rd in register Rd.",
            MnemonicArguments.DEST, new Flags()
            .set(ALUCmd.NOT)
            .set(StoreFlags.No)
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)),

    MUL("Multiplies the content of register Rs with register Rd and stores result in Rd.",
            MnemonicArguments.DEST_SOURCE, new Flags()
            .set(ALUCmd.MUL)
            .set(StoreFlags.Yes)
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)),
    MULI("Multiplies the constant [const] with register Rd and stores result in Rd.",
            MnemonicArguments.DEST_CONST, new Flags()
            .set(ALUCmd.MUL)
            .set(StoreFlags.Yes)
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)
            .set(ImmExtMode.src0)
            .set(ALUBSel.ImReg)),
    MULIs("Multiplies the constant [const] with register Rd and stores result in Rd.",
            MnemonicArguments.DEST_CONST, new Flags()
            .set(ALUCmd.MUL)
            .set(StoreFlags.Yes)
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)
            .set(ALUBSel.instrSource)),

    CMP("Subtracts the content of register Rs from register Rd without carry, does not store the result.",
            MnemonicArguments.DEST_SOURCE, new Flags()
            .set(StoreFlags.Yes)
            .set(ALUCmd.SUB)),
    CPC("Subtracts the content of register Rs from register Rd with carry, does not store the result.",
            MnemonicArguments.DEST_SOURCE, new Flags()
            .set(StoreFlags.Yes)
            .set(ALUCmd.SBC)),
    CPI("Subtracts a constant [const] from register Rd without carry, does not store the result.",
            MnemonicArguments.DEST_CONST, new Flags()
            .set(ALUCmd.SUB)
            .set(StoreFlags.Yes)
            .set(ImmExtMode.src0)
            .set(ALUBSel.ImReg)),
    CPIs("Subtracts a constant [const] from register Rd without carry, does not store the result.",
            MnemonicArguments.DEST_CONST, new Flags()
            .set(ALUCmd.SUB)
            .set(StoreFlags.Yes)
            .set(ALUBSel.instrSource)),
    CPCI("Subtracts a constant [const] from register Rd with carry, does not store the result.",
            MnemonicArguments.DEST_CONST, new Flags()
            .set(ALUCmd.SBC)
            .set(StoreFlags.Yes)
            .set(ImmExtMode.src0)
            .set(ALUBSel.ImReg)),
    CPCIs("Subtracts a constant [const] from register Rd with carry, does not store the result.",
            MnemonicArguments.DEST_CONST, new Flags()
            .set(ALUCmd.SBC)
            .set(StoreFlags.Yes)
            .set(ALUBSel.instrSource)),

    LSL("Shifts register Rd by one bit to the left. A zero bit is filled in and the highest bit is moved to the carry bit.",
            MnemonicArguments.DEST, new Flags()
            .set(ALUCmd.LSL)
            .set(StoreFlags.Yes)
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)),
    LSR("Shifts register Rd by one bit to the right. A zero bit is filled in and the lowest bit is moved to the carry bit.",
            MnemonicArguments.DEST, new Flags()
            .set(ALUCmd.LSR)
            .set(StoreFlags.Yes)
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)),
    ROL("Shifts register Rd by one bit to the left. The carry bit is filled in and the highest bit is moved to the carry bit.",
            MnemonicArguments.DEST, new Flags()
            .set(ALUCmd.ROL)
            .set(StoreFlags.Yes)
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)),
    ROR("Shifts register Rd by one bit to the right. The carry bit is filled in and the lowest bit is moved to the carry bit.",
            MnemonicArguments.DEST, new Flags()
            .set(ALUCmd.ROR)
            .set(StoreFlags.Yes)
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)),
    ASR("Shifts register Rd by one bit to the right. The MSB remains unchanged and the lowest bit is moved to the carry bit.",
            MnemonicArguments.DEST, new Flags()
            .set(ALUCmd.ASR)
            .set(StoreFlags.Yes)
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)),
    SWAP("Swaps the high and low byte in register Rd.",
            MnemonicArguments.DEST, new Flags()
            .set(ALUCmd.SWAP)
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)),
    SWAPN("Swaps the high and low nibbles of both bytes in register Rd.",
            MnemonicArguments.DEST, new Flags()
            .set(ALUCmd.SWAPN)
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)),

    ST("Stores the content of register Rs to the memory at the address [Rd].",
            MnemonicArguments.BDEST_SOURCE, new Flags()
            .set(WriteRam.Yes)
            .set(SrcToBus.Yes)
            .set(ALUBSel.Zero)
            .set(ALUCmd.ADD)),
    LD("Loads the value at memory address [Rs] to register Rd.",
            MnemonicArguments.DEST_BSOURCE, new Flags()
            .set(ReadRam.Yes)
            .set(ALUBSel.Zero)
            .set(ALUCmd.ADD)
            .set(SourceToAluA.Yes)
            .set(EnRegWrite.Yes)),
    STS("Stores the content of register Rs to memory at the location given by [const].",
            MnemonicArguments.CONST_SOURCE, new Flags()
            .set(WriteRam.Yes)
            .set(SrcToBus.Yes)
            .set(ImmExtMode.dest0)
            .set(ALUBSel.ImReg)),
    STSs("Stores the content of register Rs to memory at the location given by [const].",
            MnemonicArguments.CONST_SOURCE, new Flags()
            .set(WriteRam.Yes)
            .set(SrcToBus.Yes)
            .set(ALUBSel.instrDest)),
    LDS("Loads the memory value at the location given by [const] to register Rd.",
            MnemonicArguments.DEST_CONST, new Flags()
            .set(ReadRam.Yes)
            .set(ImmExtMode.src0)
            .set(ALUBSel.ImReg)
            .set(EnRegWrite.Yes)),
    LDSs("Loads the memory value at the location given by [const] to register Rd.",
            MnemonicArguments.DEST_CONST, new Flags()
            .set(ReadRam.Yes)
            .set(ALUBSel.instrSource)
            .set(EnRegWrite.Yes)),
    STD("Stores the content of register Rs to the memory at the address (Rd+[const]).",
            MnemonicArguments.BDEST_BCONST_SOURCE, new Flags()
            .set(WriteRam.Yes)
            .set(SrcToBus.Yes)
            .set(ImmExtMode.extend)
            .set(ALUBSel.ImReg)
            .set(ALUCmd.ADD)),
    LDD("Loads the value at memory address (Rs+[const]) to register Rd.",
            MnemonicArguments.DEST_BSOURCE_BCONST, new Flags()
            .set(ReadRam.Yes)
            .set(ImmExtMode.extend)
            .set(ALUBSel.ImReg)
            .set(ALUCmd.ADD)
            .set(EnRegWrite.Yes)
            .set(SourceToAluA.Yes)),

    LPM("Loads the value at program address [Rs] to register Rd. In a single cycle machine this requires dual ported program memory.",
            MnemonicArguments.DEST_BSOURCE, new Flags()
            .set(ALUBSel.Rom)
            .set(ALUCmd.PassInB)
            .set(ALUToBus.Yes)
            .set(EnRegWrite.Yes)),

    BRCS("Jumps to the address given by [const] if carry flag is set.",
            MnemonicArguments.CONST, new Flags()
            .set(ALUBSel.instrSourceAndDest)
            .set(Branch.BRC)),
    BREQ("Jumps to the address given by [const] if zero flag is set.",
            MnemonicArguments.CONST, new Flags()
            .set(ALUBSel.instrSourceAndDest)
            .set(Branch.BRZ)),
    BRMI("Jumps to the address given by [const] if negative flag is set.",
            MnemonicArguments.CONST, new Flags()
            .set(ALUBSel.instrSourceAndDest)
            .set(Branch.BRN)),
    BRCC("Jumps to the address given by [const] if carry flag is clear.",
            MnemonicArguments.CONST, new Flags()
            .set(ALUBSel.instrSourceAndDest)
            .set(Branch.BRNC)),
    BRNE("Jumps to the address given by [const] if zero flag is clear.",
            MnemonicArguments.CONST, new Flags()
            .set(ALUBSel.instrSourceAndDest)
            .set(Branch.BRNZ)),
    BRPL("Jumps to the address given by [const] if negative flag is clear.",
            MnemonicArguments.CONST, new Flags()
            .set(ALUBSel.instrSourceAndDest)
            .set(Branch.BRNN)),

    RCALL("Jumps to the address given by [const], the return address is stored in register Rd.",
            MnemonicArguments.DEST_CONST, new Flags()
            .set(ImmExtMode.src0)
            .set(ALUBSel.ImReg)
            .set(StorePC.Yes)
            .set(EnRegWrite.Yes)
            .set(JmpAbs.Yes)),
    RRET("Jumps to the address given by register Rs.",
            MnemonicArguments.SOURCE, new Flags()
            .set(JmpAbs.Yes)),

    JMP("Jumps to the address given by [const].",
            MnemonicArguments.CONST, new Flags()
            .set(ImmExtMode.src0)
            .set(ALUBSel.ImReg)
            .set(JmpAbs.Yes)),
    JMPs("Jumps to the address given by [const].",
            MnemonicArguments.CONST, new Flags()
            .set(ALUBSel.instrSourceAndDest)
            .set(Branch.uncond)),


    OUT("Writes the content of register Rs to io location given by [const].",
            MnemonicArguments.CONST_SOURCE, new Flags()
            .set(ImmExtMode.dest0)
            .set(ALUBSel.ImReg)
            .set(SrcToBus.Yes)
            .set(WriteIO.Yes)),
    OUTs("Writes the content of register Rs to io location given by [const].",
            MnemonicArguments.CONST_SOURCE, new Flags()
            .set(ALUBSel.instrDest)
            .set(SrcToBus.Yes)
            .set(WriteIO.Yes)),
    OUTR("Writes the content of register Rs to the io location [Rd].",
            MnemonicArguments.BDEST_SOURCE, new Flags()
            .set(ALUCmd.ADD)
            .set(ALUBSel.Zero)
            .set(SrcToBus.Yes)
            .set(WriteIO.Yes)),


    IN("Reads the io location given by [const] and stores it in register Rd.",
            MnemonicArguments.DEST_CONST, new Flags()
            .set(ImmExtMode.src0)
            .set(ALUBSel.ImReg)
            .set(EnRegWrite.Yes)
            .set(SourceToAluA.Yes)
            .set(ReadIO.Yes)),
    INs("Reads the io location given by [const] and stores it in register Rd.",
            MnemonicArguments.DEST_CONST, new Flags()
            .set(ALUBSel.instrSource)
            .set(EnRegWrite.Yes)
            .set(SourceToAluA.Yes)
            .set(ReadIO.Yes)),
    INR("Reads the io location given by (Rs) and stores it in register Rd.",
            MnemonicArguments.DEST_BSOURCE, new Flags()
            .set(ALUBSel.Zero)
            .set(ALUCmd.ADD)
            .set(EnRegWrite.Yes)
            .set(SourceToAluA.Yes)
            .set(ReadIO.Yes)),

    BRK("Stops execution by stopping the simulator.",
            MnemonicArguments.NOTHING, new Flags()
            .set(Break.Yes)),

    RETI("Return from Interrupt.",
            MnemonicArguments.NOTHING, new Flags()
            .set(JmpAbs.Yes)
            .set(RetI.Yes));

    //CHECKSTYLE.ON: JavadocVariable

    enum ReadRam {No, Yes}

    enum ReadIO {No, Yes}

    enum WriteRam {No, Yes}

    enum WriteIO {No, Yes}

    enum Break {No, Yes}

    enum SourceToAluA {No, Yes}

    enum Branch {No, BRC, BRZ, BRN, uncond, BRNC, BRNZ, BRNN}

    enum ALUBSel {Source, Rom, ImReg, Zero, res, instrSource, instrSourceAndDest, instrDest}

    enum ALUToBus {No, Yes}

    enum SrcToBus {No, Yes}

    enum ImmExtMode {extend, res, src0, dest0}

    enum ALUCmd {
        PassInB, ADD, SUB, AND, OR, XOR, NOT, NEG, LSL, LSR, ASR, SWAP, SWAPN, MUL, res4, res5,
        res6, ADC, SBC, res7, res8, res9, res10, res11, ROL, ROR
    }

    enum EnRegWrite {No, Yes}

    enum StorePC {No, Yes}

    enum JmpAbs {No, Yes}

    enum RetI {No, Yes}

    enum StoreFlags {No, Yes}

    private static final class Flags {
        private ReadRam rr = ReadRam.No;
        private WriteRam wr = WriteRam.No;
        private Branch br = Branch.No;
        private ALUBSel aluBSel = ALUBSel.Source;
        private ImmExtMode immExtMode = ImmExtMode.extend;
        private ALUToBus aluToBus = ALUToBus.No;
        private SrcToBus srcToBus = SrcToBus.No;
        private ALUCmd aluCmd = ALUCmd.PassInB;
        private EnRegWrite enRegWrite = EnRegWrite.No;
        private StorePC storePC = StorePC.No;
        private SourceToAluA sourceToAluA = SourceToAluA.No;
        private JmpAbs jmpAbs = JmpAbs.No;
        private RetI retI = RetI.No;
        private WriteIO wio = WriteIO.No;
        private ReadIO rio = ReadIO.No;
        private Break brk = Break.No;
        private StoreFlags strFlags = StoreFlags.No;

        public Flags set(ReadRam rr) {
            this.rr = rr;
            return this;
        }

        public Flags set(WriteRam wr) {
            this.wr = wr;
            return this;
        }

        public Flags set(ImmExtMode immExtMode) {
            this.immExtMode = immExtMode;
            return this;
        }

        public Flags set(Branch br) {
            this.br = br;
            return this;
        }

        public Flags set(ALUBSel aluBSel) {
            this.aluBSel = aluBSel;
            return this;
        }

        public Flags set(ALUToBus aluToBus) {
            this.aluToBus = aluToBus;
            return this;
        }

        public Flags set(SrcToBus srcToBus) {
            this.srcToBus = srcToBus;
            return this;
        }

        public Flags set(ALUCmd aluCmd) {
            this.aluCmd = aluCmd;
            return this;
        }

        public Flags set(EnRegWrite enRegWrite) {
            this.enRegWrite = enRegWrite;
            return this;
        }

        public Flags set(StorePC storePC) {
            this.storePC = storePC;
            return this;
        }

        public Flags set(SourceToAluA sourceToAlu) {
            this.sourceToAluA = sourceToAlu;
            return this;
        }

        public Flags set(JmpAbs jmpAbs) {
            this.jmpAbs = jmpAbs;
            return this;
        }

        public Flags set(WriteIO wio) {
            this.wio = wio;
            return this;
        }

        public Flags set(ReadIO rio) {
            this.rio = rio;
            return this;
        }

        public Flags set(Break brk) {
            this.brk = brk;
            return this;
        }

        public Flags set(StoreFlags strFlags) {
            this.strFlags = strFlags;
            return this;
        }

        public Flags set(RetI retI) {
            this.retI = retI;
            return this;
        }
    }

    private final String description;
    private final MnemonicArguments arguments;
    private final Flags f;

    Opcode(String description, MnemonicArguments arguments, Flags flags) {
        this.description = addConstLimit(description, flags.aluBSel);
        this.arguments = arguments;
        this.f = flags;
    }

    private String addConstLimit(String description, ALUBSel imed) {
        if (imed.equals(ALUBSel.instrDest) || imed.equals(ALUBSel.instrSource)) {
            description += " (0<=[const]<=15)";
        } else {
            if (imed.equals(ALUBSel.instrSourceAndDest)) {
                description += " (-128<=[const]<=127)";
            }
        }
        return description;
    }

    int createControlWord(boolean out) {
        return new ControlWordBuilder(out)
                .add(f.aluBSel)
                .add(f.srcToBus)
                .add(f.aluCmd)
                .add(f.enRegWrite)
                .add(f.strFlags)
                .add(f.aluToBus)
                .add(f.immExtMode)
                .add(f.br)
                .add(f.sourceToAluA)
                .add(f.rr)
                .add(f.wr)
                .add(f.jmpAbs)
                .add(f.wio)
                .add(f.rio)
                .add(f.storePC)
                .add(f.brk)
                .add(f.retI)
                .getControlWord();
    }

//    private static HashMap<String, Integer> countMap = new HashMap<>();

    private static final class ControlWordBuilder {
        private final boolean out;
        private int pos;
        private int controlWord;
        private StringBuilder sb;

        private ControlWordBuilder(boolean out) {
            this.out = out;
        }

        private ControlWordBuilder add(Enum<?> e) {
            controlWord |= e.ordinal() << pos;

//            if (e.ordinal() != 0) {
//                String simpleName = e.getClass().getSimpleName();
//                Integer v = countMap.get(simpleName);
//                if (v == null) v = 0;
//                countMap.put(simpleName, v + 1);
//            }

            int vals = e.getClass().getEnumConstants().length;
            int width;
            switch (vals) {
                case 2:
                    width = 1;
                    break;
                case 4:
                    width = 2;
                    break;
                case 8:
                    width = 3;
                    break;
                case 26:
                    width = 5;
                    break;
                default:
                    throw new RuntimeException("invalid enum count: " + e.getClass().getSimpleName() + ": " + vals);
            }
            if (out) {
                if (sb == null)
                    sb = new StringBuilder();
                else
                    sb.append(",");
                sb.append(width);

                if (width == 1) {
                    System.out.print(pos);
                } else {
                    System.out.print(pos + "-" + (pos + width - 1));
                }
                System.out.println("\t:" + e.getClass().getSimpleName());
            }
            pos += width;
            return this;
        }

        public int getControlWord() {
            if (sb != null)
                System.out.println("Splitter: " + sb);
            return controlWord;
        }
    }

    /**
     * @return the description of this opcode
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the control value for the ALUB multiplexer
     */
    public ALUBSel getALUBSel() {
        return f.aluBSel;
    }

    /**
     * @return true if the ALU result is connected to the data bus
     */
    public ALUToBus getAluToBus() {
        return f.aluToBus;
    }

    /**
     * @return true if the SRC-Register is connected to the data bus
     */
    public SrcToBus getSrcToBus() {
        return f.srcToBus;
    }

    /**
     * @return the ReadRam flag, if set to yes, the RAM output is connected to the data bus
     */
    public ReadRam getReadRam() {
        return f.rr;
    }

    /**
     * @return the WriteRam flag, if set to yes, the RAM stores the value on the data bus
     */
    public WriteRam getWriteRam() {
        return f.wr;
    }

    /**
     * @return the ReadIO flag, if set to yes, the ReadIO pin is set to true
     */
    public ReadIO getReadIO() {
        return f.rio;
    }

    /**
     * @return the WriteIO flag, if set to yes, the WriteIO pin is set to true
     */
    public WriteIO getWriteIO() {
        return f.wio;
    }

    /**
     * @return if set to yes the PC is connected to the data bus
     */
    public StorePC getStorePC() {
        return f.storePC;
    }

    /**
     * @return if set to yes value on the data bus is stored in the destination register
     */
    public EnRegWrite getEnRegWrite() {
        return f.enRegWrite;
    }

    /**
     * @return returns the flags of this opcode
     */
    public MnemonicArguments getArguments() {
        return arguments;
    }

    /**
     * @return returns the immediate register extend mode
     */
    public ImmExtMode getImmExtMode() {
        return f.immExtMode;
    }

    /**
     * returns an opcode given by the string
     *
     * @param name the string representation of the opcode
     * @return the opcode or null if not found
     */
    public static Opcode parseStr(String name) {
        for (Opcode op : Opcode.values())
            if (op.name().equalsIgnoreCase(name))
                return op;
        return null;
    }

    /**
     * Writes all the control words to a hex file.
     * This hex file can be loaded to a ROM module to configure the control unit of the cpu.
     *
     * @param out the stream fo write the hex file to
     */
    public static void writeControlWords(PrintStream out) {
        out.print("v2.0 raw\n");
        for (Opcode oc : Opcode.values()) {
            out.print(Integer.toHexString(oc.createControlWord(false)));
            out.print("\n");
        }
    }

    @Override
    public String toString() {
        return name() + " " + arguments.toString() + "\n\t" + description;
    }

    /* used to create the Control Unit Rom content!
    public static void main(String[] args) throws FileNotFoundException {
        System.out.println(Opcode.values().length + " opcodes");
        try (PrintStream p = new PrintStream("/home/hneemann/Dokumente/DHBW/Technische_Informatik_II/Systemnahes_Programmieren/control.hex")) {
            writeControlWords(p);
        }

//        System.out.println(countMap);

        for (Opcode op : Opcode.values()) {
            System.out.print(op.name() + ", ");
        }
        System.out.println();
        for (Register r : Register.values()) {
            System.out.print(r.name() + ", ");
        }

        System.out.println();
        NOP.createControlWord(true);
    }/* */
}
