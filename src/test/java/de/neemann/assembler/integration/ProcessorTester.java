package de.neemann.assembler.integration;

import de.neemann.assembler.asm.InstructionException;
import de.neemann.assembler.asm.MachineCodeListener;
import de.neemann.assembler.asm.Program;
import de.neemann.assembler.expression.ExpressionException;
import de.neemann.assembler.parser.Parser;
import de.neemann.assembler.parser.ParserException;
import de.neemann.digital.core.Model;
import de.neemann.digital.core.NodeException;
import de.neemann.digital.core.Signal;
import de.neemann.digital.core.memory.DataField;
import de.neemann.digital.core.memory.ROM;
import de.neemann.digital.draw.elements.PinException;
import de.neemann.digital.draw.library.ElementNotFoundException;
import de.neemann.digital.testing.UnitTester;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ProcessorTester {
    private final Model model;

    public ProcessorTester(String processor) throws ElementNotFoundException, NodeException, PinException, IOException {
        model = new UnitTester(new File(processor)).getModel();
    }

    public ProcessorTester setRegister(String name, long value) throws TesterException {
        Signal reg = getSignal(name);
        if (reg == null)
            throw new TesterException("Register " + name + " not found!");
        reg.getSetter().set(value, 0);
        return this;
    }

    private Signal getSignal(String name) {
        for (Signal s : model.getSignals())
            if (s.getName().equals(name))
                return s;
        return null;

    }

    public ProcessorTester runToBrk(String code) throws ExpressionException, ParserException, InstructionException, IOException, TesterException, NodeException {
        return runToBrk(code, -1);
    }

    public ProcessorTester runToBrk(String code, int expectedInstructions) throws ExpressionException, ParserException, InstructionException, IOException, TesterException, NodeException {
        Program prog = createProgram(code);

        DataFieldListener machineCodeListener = new DataFieldListener();
        prog.traverse((instruction, context) -> {
            machineCodeListener.setAddr(context.getInstrAddr());
            instruction.createMachineCode(context, machineCodeListener);
            return true;
        });

        if (expectedInstructions >= 0)
            assertEquals("instructions", expectedInstructions, machineCodeListener.getInstructionCounter());

        List<ROM> romList = model.findNode(ROM.class, rom -> rom.getDataBits() == 16);

        if (romList.size() != 1)
            throw new TesterException("program ROM not found");

        romList.get(0).setData(machineCodeListener.getDataField());

        model.init();

        model.runToBreak();

        return this;
    }

    private Program createProgram(String code) throws IOException, ExpressionException, ParserException, InstructionException {
        try (Parser p = new Parser(code)) {
            return p.parseProgram().optimizeAndLink();
        }
    }

    public ProcessorTester checkRegister(String name, long value) throws TesterException {
        Signal reg = getSignal(name);
        if (reg == null)
            throw new TesterException("Register " + name + " not found!");

        assertEquals(name + "=" + value, value, reg.getValue().getValue());

        return this;
    }

    private static class DataFieldListener implements MachineCodeListener {
        private final DataField df;
        private int addr;
        private int instructionCounter;

        public DataFieldListener() {
            this.df = new DataField();
            this.addr = 0;
        }

        @Override
        public void add(int instr) {
            df.setData(addr, instr);
            addr++;
            instructionCounter++;
        }

        public void setAddr(int addr) {
            this.addr = addr;
        }

        public DataField getDataField() {
            return df;
        }

        public int getInstructionCounter() {
            return instructionCounter;
        }
    }

    public static class TesterException extends Exception {
        public TesterException(String message) {
            super(message);
        }
    }
}
