package de.neemann.assembler.asm;

/**
 * @author hneemann
 */
public class TeXWriter {

    public static void main(String[] args) {
        System.out.println("\\documentclass[12pt]{scrartcl}\n" +
                "\n" +
                "\\usepackage[ngerman]{babel}\n" +
                "\\usepackage[utf8]{inputenc}\n" +
                "\\usepackage{longtable}\n" +
                "\n" +
                "\\newcommand{\\code}[1]{\\texttt{#1}}\n" +
                "\\begin{document}\n" +
                "\\begin{longtable}{l|p{12cm}}");
        System.out.println("Command & Description \\\\");
        System.out.println("\\hline\n\\endhead");
        for (Opcode op : Opcode.values()) {
            System.out.print("\\code{" + op.name() + " " + op.getArguments() + "}");
            System.out.print(" & ");
            System.out.print(op.getDescription().replace("<", "$<$"));
            System.out.println("\\\\");
        }
        System.out.println("\\end{longtable}\n" +
                "\\end{document}");
    }
}
