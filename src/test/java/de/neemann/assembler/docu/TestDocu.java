package de.neemann.assembler.docu;

import de.neemann.assembler.asm.Opcode;
import junit.framework.TestCase;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.xml.sax.SAXException;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

/**
 * Not a test case. Is used to create the documentation.
 * * Created by hneemann on 06.02.17.
 */
public class TestDocu extends TestCase {

    private File getTargetFolder() {
        String mavenhome = System.getenv("mavenhome");
        if (mavenhome == null) {
            System.out.println("use hardcoded output folder!");
            mavenhome = "/home/hneemann/Dokumente/Java/assembler/assembler3";
        }
        return new File(mavenhome, "target/xml");
    }

    public void testDocu() throws IOException, TransformerException, SAXException {
        final File target = getTargetFolder();
        if (!target.exists())
            if (!target.mkdirs())
                throw new IOException("could not create target folder!");
        File xml = new File(target, "asm.xml");
        writeXML(xml);

        File res = new File(target.getParentFile().getParentFile(), "src/test/resources/docu");
        File fop = new File(target, "asm.fop");
        startXalan(xml,new File(res, "docu.xslt"),fop);

        FopFactory fopFactory = FopFactory.newInstance(new File(res, "fop.xconf"));
        File pdf = new File(target, "instructions.pdf");
        startFOP(fopFactory, fop, pdf);
    }

    private void writeXML(File file) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"))) {
            w.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            w.newLine();
            w.write("<root>");
            w.newLine();
            for (Opcode op : Opcode.values()) {
                w.write("  <opcode ");
                w.write("name=\"");
                w.write(op.name());
                w.write(" ");
                w.write(op.getArguments().toString());
                w.write("\" opcode=\"0x");
                w.write(Integer.toHexString(op.ordinal()));
                w.write("\">");
                w.write(escapeHTML(op.getDescription()));
                w.write("</opcode>");
                w.newLine();
            }
            w.write("</root>");
            w.newLine();
        }
    }

    private static String escapeHTML(String text) {
        StringBuilder sb = new StringBuilder(text.length() * 2);
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    private void startXalan(File xmlIn, File xslt, File xmlOut) throws TransformerException, FileNotFoundException {
        // 1. Instantiate a TransformerFactory.
        javax.xml.transform.TransformerFactory tFactory =
                javax.xml.transform.TransformerFactory.newInstance();

        // 2. Use the TransformerFactory to process the stylesheet Source and
        //    generate a Transformer.
        javax.xml.transform.Transformer transformer = tFactory.newTransformer
                (new javax.xml.transform.stream.StreamSource(xslt));

        // 3. Use the Transformer to transform an XML Source and send the
        //    output to a Result object.
        transformer.transform
                (new javax.xml.transform.stream.StreamSource(xmlIn),
                        new javax.xml.transform.stream.StreamResult(new
                                java.io.FileOutputStream(xmlOut)));
    }

    private void startFOP(FopFactory fopFactory, File xslfo, File pdfOut) throws IOException, TransformerException, FOPException {
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(pdfOut))) {
            // Step 3: Construct fop with desired output format
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

            // Step 4: Setup JAXP using identity transformer
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(); // identity transformer

            // Step 5: Setup input and output for XSLT transformation
            // Setup input stream
            Source src = new StreamSource(xslfo);

            // Resulting SAX events (the generated FO) must be piped through to FOP
            Result res = new SAXResult(fop.getDefaultHandler());

            // Step 6: Start XSLT transformation and FOP processing
            transformer.transform(src, res);
        }
    }

}
