package de.neemann.assembler.gui;

import de.neemann.assembler.asm.InstructionException;
import de.neemann.assembler.asm.Opcode;
import de.neemann.assembler.asm.Program;
import de.neemann.assembler.asm.formatter.AsmFormatter;
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
 * Main frame of the assembler GUI
 *
 * Created by hneemann on 17.06.14.
 */
public class Main extends JFrame implements ClosingWindowListener.ConfirmSave {

    private static final String MESSAGE = "ASM 3\n\n" +
            "Simple assembler to create a hex file for a\n" +
            "simple simulated 16 bit processor.\n\n" +
            "Written by H. Neemann in 2015.";

    private static final Preferences prefs = Preferences.userRoot().node("dt_asm3");
    private final JTextArea source;
    private File filename;
    private File lastFilename;
    private String sourceOnDisk;

    public Main(File fileToOpen) {
        super("ASM 3");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setIconImages(IconCreator.createImages("asm32.png", "asm64.png", "asm128.png"));

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
                    JFileChooser fc = getjFileChooser();
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

        ToolTipAction openNew = new ToolTipAction("Open in New Window", IconCreator.create("document-open-new.png")) {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (ClosingWindowListener.checkForSave(Main.this, Main.this)) {
                    JFileChooser fc = getjFileChooser();
                    if (fc.showOpenDialog(Main.this) == JFileChooser.APPROVE_OPTION) {
                        new Main(fc.getSelectedFile()).setVisible(true);
                    }
                }
            }
        }.setToolTip("Opens a file in a new Window");

        ToolTipAction save = new ToolTipAction("Save", IconCreator.create("document-save.png")) {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    save();
                } catch (IOException e) {
                    new ErrorMessage("Error storing a file").addCause(e).show();
                }
            }
        }.setToolTip("Saves the file to disk.");

        ToolTipAction saveAs = new ToolTipAction("Save As", IconCreator.create("document-save-as.png")) {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    saveAs();
                } catch (IOException e) {
                    new ErrorMessage("Error storing a file").addCause(e).show();
                }
            }
        }.setToolTip("Saves the file with a new name to disk.");

        ToolTipAction build = new ToolTipAction("Build", IconCreator.create("preferences.png")) {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    writeHex(createProgram(), filename);
                } catch (Throwable e) {
                    new ErrorMessage("Error").addCause(e).show();
                }
            }
        }.setToolTip("Converts the source to a hex file.");

        ToolTipAction show = new ToolTipAction("Show Listing", IconCreator.create("listing.png")) {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    Program program = createProgram();
                    writeHex(program, filename);

                    ByteArrayOutputStream text = new ByteArrayOutputStream();
                    program.traverse(new AsmFormatter(new PrintStream(text)));
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
                    createProgram().traverse(new AsmFormatter(new PrintStream(text), false));
                    new ListDialog(Main.this, text.toString()).setVisible(true);
                } catch (Throwable e) {
                    new ErrorMessage("Error").addCause(e).show();
                }
            }
        }.setToolTip("Converts the source to a listing without line numbers and shows it.");

        ToolTipAction saveLst = new ToolTipAction("Save Listing") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    writeLst(createProgram(), filename);
                } catch (Throwable e) {
                    new ErrorMessage("Error").addCause(e).show();
                }
            }
        }.setToolTip("Converts the source to a listing and writes it to disk.");

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

        if (fileToOpen == null) {
            String n = prefs.get("name", null);
            if (n != null)
                fileToOpen = new File(n);
        }
        if (fileToOpen != null)
            try {
                load(fileToOpen);
            } catch (IOException e) {
                new ErrorMessage("Error loading a file").addCause(e).show();
            }

        JScrollPane scrollPane = new JScrollPane(source);
        scrollPane.setRowHeaderView(new TextLineNumber(source, 3));
        getContentPane().add(scrollPane);


        JMenu file = new JMenu("File");
        file.add(newFile.createJMenuItem());
        file.add(open.createJMenuItem());
        file.add(openNew.createJMenuItem());
        file.add(save.createJMenuItem());
        file.add(saveAs.createJMenuItem());

        JMenu assemble = new JMenu("ASM");
        assemble.add(build.createJMenuItem());
        assemble.add(show.createJMenuItem());
        assemble.add(showLight.createJMenuItem());
        assemble.add(saveLst.createJMenuItem());

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
        toolBar.add(openNew.createJButtonNoText());
        toolBar.add(save.createJButtonNoText());
        toolBar.add(show.createJButtonNoText());
        toolBar.add(build.createJButtonNoText());
        getContentPane().add(toolBar, BorderLayout.NORTH);

        pack();

        setLocationRelativeTo(null);
    }

    private JFileChooser getjFileChooser() {
        File parent = null;

        if (filename != null)
            parent = filename.getParentFile();
        else {
            if (lastFilename != null)
                parent = lastFilename.getParentFile();
        }

        JFileChooser fc = new JFileChooser(parent);
        fc.setFileFilter(new FileNameExtensionFilter("Assembler files", "asm"));
        return fc;
    }

    private Program createProgram() throws ExpressionException, InstructionException, IOException, ParserException {
        save();
        try (Parser p = new Parser(filename)) {
            return p.parseProgram()
                    .optimizeAndLink();
        }
    }

    private void save() throws IOException {
        if (filename == null)
            saveAs();
        else
            writeSource(filename);
    }

    private void saveAs() throws IOException {
        JFileChooser fc = getjFileChooser();
        if (fc.showSaveDialog(Main.this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".asm"))
                file = new File(file.getParentFile(), file.getName() + ".asm");
            writeSource(file);
        }
    }

    private void writeSource(File file) throws IOException {
        try (Writer w = new FileWriter(file)) {
            String text = this.source.getText();
            w.write(text);
            sourceOnDisk = text;
            setFilename(file);
        }
    }

    static private void writeHex(Program p, File name) throws IOException, ExpressionException {
        if (name != null) {
            File f = makeFilename(name, ".asm", ".hex");
            try (PrintStream ps = new PrintStream(f)) {
                p.traverse(new HexFormatter(ps));
            }
        }
    }

    static private void writeLst(Program p, File name) throws IOException, ExpressionException {
        if (name != null) {
            File f = makeFilename(name, ".asm", ".lst");
            try (PrintStream ps = new PrintStream(f)) {
                p.traverse(new AsmFormatter(ps));
            }
        }
    }

    private static File makeFilename(File f, String origExt, String newExt) {
        String name = f.getName();
        if (name.endsWith(origExt)) {
            String newName = name.substring(0, name.length() - origExt.length()) + newExt;
            return new File(f.getParentFile(), newName);
        } else {
            return new File(f.getParentFile(), name + newExt);
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
                    Main m = new Main(null);
                    m.setVisible(true);
                }
            });
        } else {
            try {
                File file = new File(args[0]);
                try (Parser p = new Parser(file)) {
                    Program prog = p.parseProgram().optimizeAndLink();
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
        try {
            save();
        } catch (IOException e) {
            new ErrorMessage("Error storing a file").addCause(e).show();
        }
    }
}
