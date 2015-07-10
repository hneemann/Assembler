package de.neemann.assembler.gui;

import de.neemann.assembler.asm.InstructionException;
import de.neemann.assembler.asm.Opcode;
import de.neemann.assembler.asm.Program;
import de.neemann.assembler.asm.formatter.AsmFormatter;
import de.neemann.assembler.asm.formatter.AsmLightFormatter;
import de.neemann.assembler.asm.formatter.HexFormatter;
import de.neemann.assembler.expression.ExpressionException;
import de.neemann.assembler.gui.utils.*;
import de.neemann.assembler.parser.Parser;
import de.neemann.assembler.parser.ParserException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.prefs.Preferences;

/**
 * Created by hneemann on 17.06.14.
 */
public class Main extends JFrame implements ClosingWindowListener.ConfirmSave {

    private static final String MESSAGE = "ASM 3\n\n" +
            "Simple assembler to create a hex file for a\n" +
            "simple simulated 16 bit processor.\n\n" +
            "Written by H. Neemann in 2015.";

    private static final Preferences prefs = Preferences.userRoot().node("dt_asm2");
    private final JTextArea source;
    private final ToolTipAction save;
    private final ToolTipAction saveAs;
    private File filename;
    private File lastFilename;
    private String sourceOnDisk;

    public Main() {
        super("ASM 3");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setIconImages(IconCreator.createImages("asm32.png", "asm64.png", "asm128.png", "asm256.png"));

        addWindowListener(new ClosingWindowListener(this, this));

        ToolTipAction newFile = new ToolTipAction("New", IconCreator.create("document-new.png")) {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (ClosingWindowListener.checkForSave(Main.this, Main.this)) {
                    sourceOnDisk = "";
                    source.setText(sourceOnDisk);
                    setFilename(null);
                }
            }
        }.setToolTip("creates a new file");

        ToolTipAction open = new ToolTipAction("Open", IconCreator.create("document-open.png")) {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (ClosingWindowListener.checkForSave(Main.this, Main.this)) {
                    JFileChooser fc = new JFileChooser(getDirectory());
                    fc.setFileFilter(new FileNameExtensionFilter("Assembler files", "asm"));
                    if (fc.showOpenDialog(Main.this) == JFileChooser.APPROVE_OPTION) {
                        try {
                            load(fc.getSelectedFile());
                        } catch (IOException e) {
                            new ErrorMessage("Error loading a file").addCause(e).show();
                        }
                    }
                }
            }
        }.setToolTip("Opens a file.");

        save = new ToolTipAction("Save", IconCreator.create("document-save.png")) {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (filename == null)
                    saveAs.actionPerformed(actionEvent);
                else
                    try {
                        save(filename);
                    } catch (IOException e) {
                        new ErrorMessage("Error storing a file").addCause(e).show();
                    }
            }
        }.setToolTip("Saves the file to disk.");

        saveAs = new ToolTipAction("Save As", IconCreator.create("document-save-as.png")) {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser fc = new JFileChooser(getDirectory());
                if (fc.showSaveDialog(Main.this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        File file = fc.getSelectedFile();
                        if (!file.getName().toLowerCase().endsWith(".asm"))
                            file = new File(file.getParentFile(), file.getName() + ".asm");
                        save(file);
                    } catch (IOException e) {
                        new ErrorMessage("Error storing a file").addCause(e).show();
                    }
                }
            }
        }.setToolTip("Saves the file with a new name to disk.");

        ToolTipAction build = new ToolTipAction("Build", IconCreator.create("preferences.png")) {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    Program prog = createProgram()
                            .traverse(new AsmFormatter(System.out));

                    writeHex(prog, filename);
                    writeLst(prog, filename);
                    save(filename);

                } catch (Throwable e) {
                    new ErrorMessage("Error").addCause(e).show();
                }
            }
        }.setToolTip("Converts the source to a hex file.");

        ToolTipAction show = new ToolTipAction("Show Listing", IconCreator.create("listing.png")) {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    ByteArrayOutputStream text = new ByteArrayOutputStream();
                    createProgram().traverse(new AsmFormatter(new PrintStream(text)));
                    new ListDialog(Main.this, text.toString()).setVisible(true);
                } catch (Throwable e) {
                    new ErrorMessage("Error").addCause(e).show();
                }
            }
        }.setToolTip("Converts the source to a listing and shows it.");

        ToolTipAction showLight = new ToolTipAction("Show simpler Listing") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    ByteArrayOutputStream text = new ByteArrayOutputStream();
                    createProgram().traverse(new AsmLightFormatter(new PrintStream(text)));
                    new ListDialog(Main.this, text.toString()).setVisible(true);
                } catch (Throwable e) {
                    new ErrorMessage("Error").addCause(e).show();
                }
            }
        }.setToolTip("Converts the source to a listing without line numbers and shows it.");


        ToolTipAction helpOpcodes = new ToolTipAction("Show help") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    StringBuilder sb = new StringBuilder();
                    for (Opcode op : Opcode.values())
                        sb.append(op).append("\n\n");
                    new ListDialog(Main.this, "Instructions", sb.toString()).setVisible(true);
                } catch (Throwable e) {
                    new ErrorMessage("Error").addCause(e).show();
                }
            }
        }.setToolTip("Shows a short description of available opcodes.");


        source = new JTextArea(40, 50);
        source.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        String n = prefs.get("name", null);
        if (n != null)
            try {
                load(new File(n));
            } catch (IOException e) {
                new ErrorMessage("Error loading a file").addCause(e).show();
            }


        JScrollPane scrollPane = new JScrollPane(source);
        scrollPane.setRowHeaderView(new TextLineNumber(source, 3));
        getContentPane().add(scrollPane);


        JMenu file = new JMenu("File");
        file.add(newFile.createJMenuItem());
        file.add(open.createJMenuItem());
        file.add(save.createJMenuItem());
        file.add(saveAs.createJMenuItem());

        JMenu assemble = new JMenu("ASM");
        assemble.add(build.createJMenuItem());
        assemble.add(show.createJMenuItem());
        assemble.add(showLight.createJMenuItem());

        JMenu help = new JMenu("Help");
        help.add(helpOpcodes.createJMenuItem());
        help.add(new JMenuItem(new AbstractAction("About") {
            @Override
            public void actionPerformed(ActionEvent e) {
                InfoDialog.getInstance().showInfo(Main.this, MESSAGE);
            }
        }));


        JMenuBar menuBar = new JMenuBar();
        menuBar.add(file);
        menuBar.add(assemble);
        menuBar.add(help);
        setJMenuBar(menuBar);

        JToolBar toolBar = new JToolBar();
        toolBar.add(open.createJButtonNoText());
        toolBar.add(save.createJButtonNoText());
        toolBar.add(show.createJButtonNoText());
        toolBar.add(build.createJButtonNoText());
        getContentPane().add(toolBar, BorderLayout.NORTH);

        pack();

        setLocationRelativeTo(null);
    }

    private Program createProgram() throws ExpressionException, InstructionException, IOException, ParserException {
        return new Parser(source.getText())
                .getProgram()
                .appendData()
                .optimize()
                .link()
                .optimizeJmp()
                .link();
    }

    private File getDirectory() {
        if (filename != null)
            return filename.getParentFile();
        else {
            if (lastFilename != null)
                return lastFilename.getParentFile();
            else
                return null;
        }
    }

    private void save(File file) throws IOException {
        try (Writer w = new FileWriter(file)) {
            String text = this.source.getText();
            w.write(text);
            sourceOnDisk = text;
            setFilename(file);
        }
    }

    static private void writeHex(Program p, File name) throws IOException, ExpressionException {
        if (name != null) {
            File f = new File(name.getPath() + ".hex");
            try (PrintStream ps = new PrintStream(f)) {
                p.traverse(new HexFormatter(ps));
            }
        }
    }

    static private void writeLst(Program p, File name) throws IOException, ExpressionException {
        if (name != null) {
            File f = new File(name.getPath() + ".lst");
            try (PrintStream ps = new PrintStream(f)) {
                p.traverse(new AsmFormatter(ps));
            }
        }
    }


    private void load(File file) throws IOException {
        try (Reader in = new FileReader(file)) {
            StringBuilder sb = new StringBuilder();

            int c;
            while ((c = in.read()) >= 0)
                sb.append((char) c);

            sourceOnDisk = sb.toString();
            source.setText(sourceOnDisk);
            setFilename(file);
        }

    }

    private void setFilename(File file) {
        lastFilename = filename;
        this.filename = file;
        if (file == null)
            setTitle("ASM 3");
        else {
            setTitle("[" + file.getName() + "] ASM 3");
            prefs.put("name", file.toString());
        }
    }

    public static void main(String[] args) {

        if (args.length == 0) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    Main m = new Main();
                    m.setVisible(true);
                }
            });
        } else {
            try {
                File file = new File(args[0]);
                try (FileReader in = new FileReader(file)) {
                    Parser p = new Parser(in);
                    Program prog = p.getProgram();
                    prog.link();
                    writeHex(prog, file);
                    writeLst(prog, file);
                }
            } catch (Throwable e) {
                System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
                System.exit(1);
            }
        }

    }

    @Override
    public boolean isStateChanged() {
        return !source.getText().equals(sourceOnDisk);
    }

    @Override
    public void saveChanges() {
        save.actionPerformed(null);
    }
}
