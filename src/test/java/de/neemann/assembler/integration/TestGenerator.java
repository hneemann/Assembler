package de.neemann.assembler.integration;

import de.neemann.assembler.asm.Opcode;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static de.neemann.assembler.docu.TestDocu.getMavenRoot;

public class TestGenerator implements Test {
    private static final int DELTAY = 80;
    private static final int DELTAX = 160;
    private final ArrayList<Method> methodList;
    private final Object testMethods;
    private int maxCols = 10;
    private int col;
    private int xPos;
    private int yPos;
    private Element visualElements;
    private int[] opcodesUsed = new int[Opcode.values().length];

    public TestGenerator(Object testMethods) {
        this.testMethods = testMethods;
        methodList = new ArrayList<>();
        for (Method m : testMethods.getClass().getMethods()) {
            if (m.getName().startsWith("test") && m.getParameterCount() == 1 && m.getParameterTypes()[0] == Test.class) {
                methodList.add(m);
            }
        }
    }

    public TestGenerator setRows(int maxRows) {
        this.maxCols = maxRows;
        return this;
    }

    public void write(String path) throws IOException, JDOMException {
        File file = new File(getMavenRoot(), "src/test/resources/dig/ProcessorTestTemplate.dig");
        Document circuit = new SAXBuilder().build(file);
        visualElements = circuit.getRootElement().getChild("visualElements");

        for (Method m : methodList) {
            try {
                m.invoke(testMethods, this);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        Format format = Format.getPrettyFormat()
                .setIndent("    ")
                .setTextMode(Format.TextMode.PRESERVE);
        new XMLOutputter(format).output(circuit, new FileOutputStream(path));

        for (Opcode oc : Opcode.values()) {
            if (opcodesUsed[oc.ordinal()]==0)
                System.out.println(oc.name()+": "+opcodesUsed[oc.ordinal()]);
        }
    }

    @Override
    public void add(ProcessorTest processorTest) {
        for (Opcode oc : processorTest.getUsedOpcodes())
            opcodesUsed[oc.ordinal()]++;

        String code = processorTest.getCode();

        visualElements.addContent(
                new Element("visualElement")
                        .addContent(new Element("elementName").setText("Testcase"))
                        .addContent(new Element("elementAttributes")
                                .addContent(new Element("entry")
                                        .addContent(new Element("string").setText("Label"))
                                        .addContent(new Element("string").setText(processorTest.getLabel())))
                                .addContent(new Element("entry")
                                        .addContent(new Element("string").setText("Testdata"))
                                        .addContent(new Element("testData")
                                                .addContent(new Element("dataString").setText(code)))))
                        .addContent(new Element("pos")
                                .setAttribute("x", Integer.toString(xPos))
                                .setAttribute("y", Integer.toString(yPos))));
        xPos += DELTAX;
        col++;
        if (col >= maxCols) {
            col = 0;
            xPos = 0;
            yPos += DELTAY;
        }
    }
}
