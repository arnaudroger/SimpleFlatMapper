package org.simpleflatmapper.csv.test.bug;

import org.junit.Test;
import org.simpleflatmapper.csv.CsvParser;
import org.simpleflatmapper.util.ListCollector;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class Issue380Test {


    @Test
    public void testYamlWithQuoteProtectedCR() throws IOException {
        ListCollector<String[]> rowConsumer = new ListCollector<String[]>();
        ListCollector<String> commentConsumer = new ListCollector<String>();
        CsvParser.dsl()
                .withYamlComments()
                .forEach("# yaml comment with ,\"\rdata,#not a comment,v\r#c2", rowConsumer, commentConsumer);

        assertEquals(Arrays.asList("# yaml comment with ,\"", "#c2"), commentConsumer.getList());
        assertEquals(1, rowConsumer.getList().size());
        assertArrayEquals(new String[] {"data", "#not a comment", "v"}, rowConsumer.getList().get(0));
    }


    @Test
    public void testYamlWithQuoteProtectedLF() throws IOException {
        ListCollector<String[]> rowConsumer = new ListCollector<String[]>();
        ListCollector<String> commentConsumer = new ListCollector<String>();
        CsvParser.dsl()
                .withYamlComments()
                .forEach("# yaml comment with ,\"\ndata,#not a comment,v\n#c2", rowConsumer, commentConsumer);

        assertEquals(Arrays.asList("# yaml comment with ,\"", "#c2"), commentConsumer.getList());
        assertEquals(1, rowConsumer.getList().size());
        assertArrayEquals(new String[] {"data", "#not a comment", "v"}, rowConsumer.getList().get(0));
    }

    @Test
    public void testYamlWithQuoteProtectedCRLF() throws IOException {
        ListCollector<String[]> rowConsumer = new ListCollector<String[]>();
        ListCollector<String> commentConsumer = new ListCollector<String>();
        CsvParser.dsl()
                .withYamlComments()
                .forEach("# yaml comment with ,\"\r\ndata,#not a comment,v\r\n#c2", rowConsumer, commentConsumer);

        assertEquals(Arrays.asList("# yaml comment with ,\"", "#c2"), commentConsumer.getList());
        assertEquals(1, rowConsumer.getList().size());
        assertArrayEquals(new String[] {"data", "#not a comment", "v"}, rowConsumer.getList().get(0));
    }
}
