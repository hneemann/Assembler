package de.neemann.assembler.gui.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarFile;
import java.util.logging.Logger;

/**
 * Helper to show the info dialog!
 * Reads Build-Version and Build-Date from the Manifest.
 * Created by hneemann on 23.03.15.
 */
public final class InfoDialog {
    private static final Logger LOGGER = Logger.getLogger(InfoDialog.class.getName());
    private static InfoDialog instance;
    private final ArrayList<Manifest> infos;

    /**
     * @return the singleton instance
     */
    public static InfoDialog getInstance() {
        if (instance == null)
            try {
                instance = new InfoDialog();
            } catch (IOException e) {
                LOGGER.warning("error reading InfoDialog " + e.getMessage());
            }
        return instance;
    }

    private InfoDialog() throws IOException {
        infos = new ArrayList<Manifest>();
        Enumeration<URL> resEnum = Thread.currentThread().getContextClassLoader().getResources(JarFile.MANIFEST_NAME);
        while (resEnum.hasMoreElements()) {
            Manifest m = new Manifest(resEnum.nextElement());
            if (m.get(Manifest.REVISION) != null)
                infos.add(m);
        }
    }

    /**
     * Creates amessage by taking the given message and adding the manifestinfos to it
     *
     * @param message the given message
     * @return message and added manifest infos
     */
    public String createMessage(String message) {
        StringBuilder sb = new StringBuilder(message);
        sb.append("\n\n");
        for (Manifest m : infos) {
            m.createInfoString(sb);
        }
        return sb.toString();
    }

    /**
     * Shows the message in a JOptioPane dialog
     *
     * @param parent  the parent component
     * @param message the message
     */
    public void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, createMessage(message), "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Adds the help menu to a JFrame
     *
     * @param frame   the frame
     * @param message the message
     * @return the help menu
     */
    public JMenu addToFrame(final JFrame frame, final String message) {
        JMenuBar bar = frame.getJMenuBar();
        JMenu help = new JMenu("Help");
        bar.add(help);
        help.add(new AbstractAction("About") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                showInfo(frame, message);
            }
        });
        return help;
    }

    private final static class Manifest {
        private static final String REVISION = "Build-SCM-Revision";
        private static final String TIME = "Build-Time";

        private final HashMap<String, String> manifest;
        private final URL url;

        private Manifest(URL url) throws IOException {
            this.url = url;
            manifest = new HashMap<String, String>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    int p = line.indexOf(':');
                    if (p >= 0) {
                        String key = line.substring(0, p).trim();
                        String value = line.substring(p + 1).trim();
                        manifest.put(key, value);
                    }
                }
            } finally {
                reader.close();
            }
        }

        public String get(String key) {
            return manifest.get(key);
        }

        public void createInfoString(StringBuilder sb) {
            String path = url.getPath();
            int p = path.lastIndexOf("!/");
            path = path.substring(0, p);
            p = path.lastIndexOf('/');
            sb.append(path.substring(p + 1)).append('\n');
            sb.append("Build Revision").append(": ");
            sb.append(get(REVISION)).append("\n");
            sb.append("Build Time").append(": ");
            sb.append(get(TIME)).append("\n\n");
        }

        @Override
        public String toString() {
            return "Manifest{"
                    + "manifest=" + manifest
                    + ", url=" + url
                    + '}';
        }
    }
}
