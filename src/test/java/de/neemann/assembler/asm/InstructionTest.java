package de.neemann.assembler.asm;

import de.neemann.assembler.expression.Constant;
import de.neemann.assembler.expression.Context;
import de.neemann.assembler.expression.ExpressionException;
import junit.framework.TestCase;

/**
 * @author hneemann
 */
public class InstructionTest extends TestCase {

    private MachineCodeListener mc = new MachineCodeListener() {
        @Override
        public void add(int instr) {
        }
    };

    public void testConstantLDS() throws InstructionException, ExpressionException {
        Instruction.make(Opcode.LDSs, Register.R0, new Constant(31)).createMachineCode(null, mc);

        try {
            Instruction.make(Opcode.LDSs, Register.R0, new Constant(32)).createMachineCode(null, mc);
            assertTrue(false);
        } catch (ExpressionException e) {
            assertTrue(true);
        }

        try {
            Instruction.make(Opcode.LDSs, Register.R0, new Constant(-1)).createMachineCode(null, mc);
            assertTrue(false);
        } catch (ExpressionException e) {
            assertTrue(true);
        }
    }

    public void testConstantSTS() throws InstructionException, ExpressionException {
        Instruction.make(Opcode.STSs, Register.R0, new Constant(31)).createMachineCode(null, mc);

        try {
            Instruction.make(Opcode.STSs, Register.R0, new Constant(32)).createMachineCode(null, mc);
            assertTrue(false);
        } catch (ExpressionException e) {
            assertTrue(true);
        }

        try {
            Instruction.make(Opcode.STSs, Register.R0, new Constant(-1)).createMachineCode(null, mc);
            assertTrue(false);
        } catch (ExpressionException e) {
            assertTrue(true);
        }
    }

    public void testJMP() throws InstructionException, ExpressionException {
        Context c = new Context();
        c.setInstrAddr(1000);

        Instruction.make(Opcode.JMPs, new Constant(1001 + 255)).createMachineCode(c, mc);
        try {
            Instruction.make(Opcode.JMPs, new Constant(1001 + 256)).createMachineCode(c, mc);
            assertTrue(false);
        } catch (ExpressionException e) {
            assertTrue(true);
        }

        Instruction.make(Opcode.JMPs, new Constant(1001 - 256)).createMachineCode(c, mc);
        try {
            Instruction.make(Opcode.JMPs, new Constant(1001 - 257)).createMachineCode(c, mc);
            assertTrue(false);
        } catch (ExpressionException e) {
            assertTrue(true);
        }

    }

}