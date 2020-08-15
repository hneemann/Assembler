package de.neemann.assembler.asm;

import de.neemann.assembler.expression.Context;
import de.neemann.assembler.expression.ExpressionException;

import java.util.HashMap;

/**
 * Tries to replace a long const instruction by a short constant instruction
 *
 * @author hneemann
 */
public class OptimizerShort implements InstructionVisitor {
    private final HashMap<Opcode, Opcode> shortConstantMap;

    /**
     * Creates a new instance
     */
    public OptimizerShort() {
        shortConstantMap = new HashMap<>();
        shortConstantMap.put(Opcode.LDI, Opcode.LDIs);
        shortConstantMap.put(Opcode.OUT, Opcode.OUTs);
        shortConstantMap.put(Opcode.ADDI, Opcode.ADDIs);
        shortConstantMap.put(Opcode.ADCI, Opcode.ADCIs);
        shortConstantMap.put(Opcode.SUBI, Opcode.SUBIs);
        shortConstantMap.put(Opcode.SBCI, Opcode.SBCIs);
        shortConstantMap.put(Opcode.ANDI, Opcode.ANDIs);
        shortConstantMap.put(Opcode.ORI, Opcode.ORIs);
        shortConstantMap.put(Opcode.EORI, Opcode.EORIs);
        shortConstantMap.put(Opcode.CPI, Opcode.CPIs);
        shortConstantMap.put(Opcode.CPCI, Opcode.CPCIs);
        shortConstantMap.put(Opcode.LDS, Opcode.LDSs);
        shortConstantMap.put(Opcode.STS, Opcode.STSs);
        shortConstantMap.put(Opcode.MULI, Opcode.MULIs);
        shortConstantMap.put(Opcode.IN, Opcode.INs);
    }

    @Override
    public boolean visit(InstructionInterface instructionInterface, Context context) throws ExpressionException {
        if (instructionInterface instanceof Instruction) {
            Instruction instruction = (Instruction) instructionInterface;
            Opcode op = instruction.getOpcode();
            Opcode opShort = shortConstantMap.get(op);
            if (opShort != null) {
                int con = instruction.getConstant().getValue(context);
                if (con >= 0 && con <= 15)
                    instruction.setOpcode(opShort);
            }
        }
        return true;
    }
}
