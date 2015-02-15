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
        GenerateTuples.generateTuple(stringWriter,0, 2, true);
        assertEquals(getContent("src/main/java/org/sfm/tuples/Tuple2.java"), stringWriter.toString());
    }

    @Test
    public void testGenerateTuple3() throws IOException {
        StringWriter stringWriter = new StringWriter();
        GenerateTuples.generateTuple(stringWriter,2,  3, true);
        assertEquals(getContent("src/main/java/org/sfm/tuples/Tuple3.java"), stringWriter.toString());
    }

    @Test
    public void testGenerateTuple4() throws IOException {
        StringWriter stringWriter = new StringWriter();
        GenerateTuples.generateTuple(stringWriter,3,  4, true);
        assertEquals(getContent("src/main/java/org/sfm/tuples/Tuple4.java"), stringWriter.toString());
    }
    @Test
    public void testGenerateTuple5() throws IOException {
        StringWriter stringWriter = new StringWriter();
        GenerateTuples.generateTuple(stringWriter,4, 5, true);
        assertEquals(getContent("src/main/java/org/sfm/tuples/Tuple5.java"), stringWriter.toString());
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
