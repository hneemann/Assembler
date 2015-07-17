package de.neemann.assembler.asm;

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
        new InstructionBuilder(Opcode.LDSs).setDest(Register.R0).setConstant(31).build().createMachineCode(null, mc);

        try {
            new InstructionBuilder(Opcode.LDSs).setDest(Register.R0).setConstant(32).build().createMachineCode(null, mc);
            assertTrue(false);
        } catch (ExpressionException e) {
            assertTrue(true);
        }

        try {
            new InstructionBuilder(Opcode.LDSs).setDest(Register.R0).setConstant(-1).build().createMachineCode(null, mc);
            assertTrue(false);
        } catch (ExpressionException e) {
            assertTrue(true);
        }
    }

    public void testConstantSTS() throws InstructionException, ExpressionException {
        new InstructionBuilder(Opcode.STSs).setSource(Register.R0).setConstant(31).build().createMachineCode(null, mc);

        try {
            new InstructionBuilder(Opcode.STSs).setSource(Register.R0).setConstant(32).build().createMachineCode(null, mc);
            assertTrue(false);
        } catch (ExpressionException e) {
            assertTrue(true);
        }

        try {
            new InstructionBuilder(Opcode.STSs).setSource(Register.R0).setConstant(-1).build().createMachineCode(null, mc);
            assertTrue(false);
        } catch (ExpressionException e) {
            assertTrue(true);
        }
    }

    public void testJMP() throws InstructionException, ExpressionException {
        Context c = new Context();
        c.setInstrAddr(1000);

        new InstructionBuilder(Opcode.JMPs).setConstant(1001 + 255).build().createMachineCode(c, mc);
        try {
            new InstructionBuilder(Opcode.JMPs).setConstant(1001 + 256).build().createMachineCode(c, mc);
            assertTrue(false);
        } catch (ExpressionException e) {
            assertTrue(true);
        }

        new InstructionBuilder(Opcode.JMPs).setConstant(1001 - 256).build().createMachineCode(c, mc);
        try {
            new InstructionBuilder(Opcode.JMPs).setConstant(1001 - 257).build().createMachineCode(c, mc);
            assertTrue(false);
        } catch (ExpressionException e) {
            assertTrue(true);
        }

    }

}