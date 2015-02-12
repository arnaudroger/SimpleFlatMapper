package org.sfm.tuples;

import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

public class GenerateTuplesTest {

    @Test
    public void testGenerateTuple2() throws IOException {
        StringWriter stringWriter = new StringWriter();
        GenerateTuples.generateTuple(stringWriter, 2);
        assertEquals(getContent("src/main/java/org/sfm/tuples/Tuple2.java"), stringWriter.toString());
    }

    private String getContent(String file) throws IOException {
        StringBuilder sb = new StringBuilder();

        FileReader reader = new FileReader(file);
        char[] buffer = new char[4096];
        int l;
        while((l = reader.read(buffer)) >= 0) {
            sb.append(buffer, 0, l);
        }

        return sb.toString();
    }
}
