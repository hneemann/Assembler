package de.neemann.assembler.asm;

import de.neemann.assembler.expression.Context;
import de.neemann.assembler.expression.ExpressionException;

import java.util.HashMap;

/**
 * @author hneemann
 */
public class Optimizer implements InstructionVisitor {
    private final Program program;
    private final HashMap<Opcode, Opcode> shortConstantMap;

    public Optimizer(Program program) {
        this.program = program;
        shortConstantMap = new HashMap<>();
        shortConstantMap.put(Opcode.LDI, Opcode.LDIs);
        shortConstantMap.put(Opcode.OUT, Opcode.OUTs);
        shortConstantMap.put(Opcode.ADDI, Opcode.ADDIs);
        shortConstantMap.put(Opcode.ADCI, Opcode.ADCIs);
        shortConstantMap.put(Opcode.SUBI, Opcode.SUBIs);
        shortConstantMap.put(Opcode.SBCI, Opcode.SBCIs);
        shortConstantMap.put(Opcode.ANDI, Opcode.ANDIs);
        shortConstantMap.put(Opcode.ORI, Opcode.ORIs);
        shortConstantMap.put(Opcode.XORI, Opcode.XORIs);
        shortConstantMap.put(Opcode.CPI, Opcode.CPIs);
        shortConstantMap.put(Opcode.LDS, Opcode.LDSs);
        shortConstantMap.put(Opcode.STS, Opcode.STSs);
    }

    @Override
    public void visit(Instruction instruction, Context context) throws ExpressionException {
        Opcode op = instruction.getOpcode();

        Opcode opShort = shortConstantMap.get(op);
        if (opShort != null) {
            int con = instruction.getConstant().getValue(context);
            if (con >= 0 && con <= 31)
                instruction.setOpcode(opShort);
        }
    }
}
