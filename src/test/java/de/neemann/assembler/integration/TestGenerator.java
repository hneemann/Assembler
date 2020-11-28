package de.neemann.assembler.integration;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class TestGenerator implements Test {
    private static final int DELTAY = 80;
    private static final int DELTAX = 260;
    private final ArrayList<Method> methodList;
    private final Object testMethods;
    private BufferedWriter writer;
    private int maxRows = 10;
    private int row;
    private int xPos;
    private int yPos;

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
        this.maxRows = maxRows;
        return this;
    }

    public void write(String path) throws IOException {
        write(new BufferedWriter(new FileWriter(new File(path))));
    }

    public void write() throws IOException {
        write(new BufferedWriter(new PrintWriter(System.out)));
    }

    public void write(BufferedWriter writer) throws IOException {
        this.writer = writer;
        try (writer) {
            writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<circuit>\n" +
                    "  <version>1</version>\n" +
                    "  <attributes/>\n" +
                    "  <visualElements>\n" +
                    "    <visualElement>\n" +
                    "      <elementName>Clock</elementName>\n" +
                    "      <elementAttributes>\n" +
                    "        <entry>\n" +
                    "          <string>runRealTime</string>\n" +
                    "          <boolean>true</boolean>\n" +
                    "        </entry>\n" +
                    "        <entry>\n" +
                    "          <string>Label</string>\n" +
                    "          <string>Clk</string>\n" +
                    "        </entry>\n" +
                    "        <entry>\n" +
                    "          <string>Frequency</string>\n" +
                    "          <int>200</int>\n" +
                    "        </entry>\n" +
                    "      </elementAttributes>\n" +
                    "      <pos x=\"-180\" y=\"20\"/>\n" +
                    "    </visualElement>\n" +
                    "    <visualElement>\n" +
                    "      <elementName>Processor.dig</elementName>\n" +
                    "      <elementAttributes/>\n" +
                    "      <pos x=\"-160\" y=\"20\"/>\n" +
                    "    </visualElement>\n");
            for (Method m : methodList) {
                try {
                    m.invoke(testMethods, this);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
            writer.write("  </visualElements>\n" +
                    "  <wires>\n" +
                    "    <wire>\n" +
                    "      <p1 x=\"-180\" y=\"20\"/>\n" +
                    "      <p2 x=\"-160\" y=\"20\"/>\n" +
                    "    </wire>\n" +
                    "  </wires>\n" +
                    "  <measurementOrdering/>\n" +
                    "</circuit>");
        }
    }

    @Override
    public void add(ProcessorTest processorTest) {
        try {
            String code = processorTest.getCode();

            writer.write("    <visualElement>\n" +
                    "      <elementName>Testcase</elementName>\n" +
                    "      <elementAttributes>\n" +
                    "        <entry>\n" +
                    "          <string>Label</string>\n" +
                    "          <string>" + processorTest.getLabel() + "</string>\n" +
                    "        </entry>\n" +
                    "        <entry>\n" +
                    "          <string>Testdata</string>\n" +
                    "          <testData>\n" +
                    "            <dataString>" + code + "</dataString>\n" +
                    "          </testData>\n" +
                    "        </entry>\n" +
                    "      </elementAttributes>\n" +
                    "      <pos x=\"" + xPos + "\" y=\"" + yPos + "\"/>\n" +
                    "    </visualElement>\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        yPos += DELTAY;
        row++;
        if (row >= maxRows) {
            row = 0;
            yPos = 0;
            xPos += DELTAX;
        }
    }
}
