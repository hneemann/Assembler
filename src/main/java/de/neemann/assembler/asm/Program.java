package de.neemann.assembler.asm;

import de.neemann.assembler.expression.Constant;
import de.neemann.assembler.expression.Context;
import de.neemann.assembler.expression.Expression;
import de.neemann.assembler.expression.ExpressionException;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * @author hneemann
 */
public class Program {

    private final ArrayList<Instruction> prog;

    public Program() {
        prog = new ArrayList<>();
    }

    public Program add(Opcode opcode, int dest, Expression expr) {
        prog.add(new Instruction(opcode, dest, 0, expr));
        return this;
    }

    public Program add(Opcode opcode, int dest, int source) {
        prog.add(new Instruction(opcode, dest, source, null));
        return this;
    }

    public void writeHex(String filename) throws IOException, ExpressionException {
        try (PrintStream out = new PrintStream(filename)) {
            writeHex(out);
        }
    }

    public void writeHex(final PrintStream out) throws ExpressionException {
        Context context = new Context();
        out.println("v2.0 raw");
        for (Instruction in : prog)
            in.createMachineCode(context, new MachineCodeListener() {
                @Override
                public void add(int code) {
                    out.println(Integer.toHexString(code));
                }
            });
    }

    public void link() {
    }

    public static void main(String[] args) throws IOException, ExpressionException {
        new Program()
                .add(Opcode.LDI, 0, new Constant(1))
                .add(Opcode.LDI, 1, new Constant(2))
                .add(Opcode.ADDI, 0, new Constant(7))
                .add(Opcode.ADD, 0, 1)
                .writeHex("/home/hneemann/Dokumente/DHBW/Technische_Informatik_I/Vorlesung/06_Prozessoren/java/assembler3/z.asm.hex");
    }
}
