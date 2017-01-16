package org.simpleflatmapper.tuple.test;

import org.junit.Test;
import org.simpleflatmapper.tuple.gen.GenerateTuples;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GenerateTuplesTest {

    @Test
    public void testGenerateTuple2() throws IOException {
        StringWriter stringWriter = new StringWriter();
        GenerateTuples.generateTuple(stringWriter,0, 2, true);
        assertEquals(getContent("src/main/java/org/simpleflatmapper/tuple/Tuple2.java"), stringWriter.toString());
    }

    @Test
    public void testGenerateTuple3() throws IOException {
        StringWriter stringWriter = new StringWriter();
        GenerateTuples.generateTuple(stringWriter,2,  3, true);
        assertEquals(getContent("src/main/java/org/simpleflatmapper/tuple/Tuple3.java"), stringWriter.toString());
    }

    @Test
    public void testGenerateTuple4() throws IOException {
        StringWriter stringWriter = new StringWriter();
        GenerateTuples.generateTuple(stringWriter,3,  4, true);
        assertEquals(getContent("src/main/java/org/simpleflatmapper/tuple/Tuple4.java"), stringWriter.toString());
    }
    @Test
    public void testGenerateTuple5() throws IOException {
        StringWriter stringWriter = new StringWriter();
        GenerateTuples.generateTuple(stringWriter,4, 5, true);
        assertEquals(getContent("src/main/java/org/simpleflatmapper/tuple/Tuple5.java"), stringWriter.toString());
    }

    @Test
    public void testNth() {
        assertEquals("sixth", GenerateTuples.getThName(5));
        assertEquals("seventh", GenerateTuples.getThName(6));
        assertEquals("eighth", GenerateTuples.getThName(7));
        assertEquals("ninth", GenerateTuples.getThName(8));
        assertEquals("tenth", GenerateTuples.getThName(9));

        assertNull(GenerateTuples.getThName(10));
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
