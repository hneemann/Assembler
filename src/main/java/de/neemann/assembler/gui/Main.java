package de.neemann.assembler.gui;

import de.neemann.assembler.asm.Program;
import de.neemann.assembler.parser.Parser;
import de.process.utils.gui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.prefs.Preferences;

/**
 * Created by hneemann on 17.06.14.
 */
public class Main extends JFrame implements ClosingWindowListener.ConfirmSave {

    private static final String MESSAGE = "ASM 2\n\n" +
            "Simple assembler to create a hex file for a\n" +
            "simple simulated 8 bit processor.\n\n" +
            "Written by H. Neemann in 2015.";

    private static final Preferences prefs = Preferences.userRoot().node("dt_asm2");
    private final JTextArea source;
    private final ToolTipAction save;
    private final ToolTipAction saveAs;
    private File filename;
    private String sourceOnDisk;

    public Main() {
        super("ASM 2");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

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
                        save(fc.getSelectedFile());
                    } catch (IOException e) {
                        new ErrorMessage("Error storing a file").addCause(e).show();
                    }
                }
            }
        }.setToolTip("Saves the file with a new name to disk.");

        ToolTipAction build = new ToolTipAction("Build", IconCreator.create("preferences.png")) {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Parser p = new Parser(source.getText());
                try {
                    Program prog = p.getProgram();

                    prog.link();
                    System.out.println(prog);

                    writeHex(prog, filename);
                    writeLst(prog, filename);
                    save(filename);

                } catch (Throwable e) {
                    new ErrorMessage("Error").addCause(e).show();
                }
            }
        }.setToolTip("Converts the source to a hex file.");

        source = new JTextArea(40, 50);

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

        JMenu help = new JMenu("Help");
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
        toolBar.add(build.createJButtonNoText());
        getContentPane().add(toolBar, BorderLayout.NORTH);

        pack();

        setLocationRelativeTo(null);
    }

    private File getDirectory() {
        if (filename != null)
            return filename.getParentFile();
        else
            return null;
    }

    private void save(File file) throws IOException {
        try (Writer w = new FileWriter(file)) {
            String text = this.source.getText();
            w.write(text);
            sourceOnDisk = text;
            setFilename(file);
        }
    }

    static private void writeHex(Program p, File name) throws IOException {
        if (name != null) {
            File f = new File(name.getPath() + ".hex");
            try (FileWriter wr = new FileWriter(f)) {
                wr.write("v2.0 raw");
                wr.write("\n");
                //p.format(wr);
            }
        }
    }

    static private void writeLst(Program p, File name) throws IOException {
        if (name != null) {
            File f = new File(name.getPath() + ".lst");
            try (FileWriter wr = new FileWriter(f)) {
                wr.write(p.toString());
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
        this.filename = file;
        if (file == null)
            setTitle("ASM 2");
        else {
            setTitle("[" + file.getName() + "] ASM 2");
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
