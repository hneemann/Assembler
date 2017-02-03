package de.neemann.assembler.asm;

/**
 * @author hneemann
 */
public final class TeXWriter {

    private TeXWriter() {
    }

    /**
     * Writes the documentation of the instructions as LaTeX file
     *
     * @param args args
     */
    public static void main(String[] args) {
        System.out.println("\\documentclass[12pt]{scrartcl}\n"
                + "\n"
                + "\\usepackage[ngerman]{babel}\n"
                + "\\usepackage[utf8]{inputenc}\n"
                + "\\usepackage{longtable}\n"
                + "\\usepackage{array}\n"
                + "\n"
                + "\\parindent0pt\n"
                + "\n"
                + "\\newcommand{\\code}[1]{\\texttt{#1}}\n"
                + "\\newcolumntype{R}{>{\\raggedright\\arraybackslash}p{10.5cm}}\n"
                + "\n"
                + "\\begin{document}\n"
                + "\\textbf{\\Large Instruction Set Summary}\\\\[1ex]\n"
                + "\\begin{longtable}{l|c|R}\n"
                + "Command & Op & Description \\\\\n");
        System.out.println("\\hline\n\\endhead");
        for (Opcode op : Opcode.values()) {
            System.out.print("\\code{" + op.name() + " " + op.getArguments() + "}");
            System.out.print(" & ");
            System.out.print(" 0x");
            System.out.print(Integer.toHexString(op.ordinal()));
            System.out.print(" & ");
            System.out.print(op.getDescription().replace("<", "$<$"));
            System.out.println("\\\\");
        }
        System.out.println("\\end{longtable}\n"
                + "\\end{document}");
    }
}
