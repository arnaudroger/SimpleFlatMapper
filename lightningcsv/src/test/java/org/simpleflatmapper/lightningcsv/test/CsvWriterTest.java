package org.simpleflatmapper.lightningcsv.test;

import org.junit.Test;
import org.simpleflatmapper.lightningcsv.CsvWriter;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.*;

public class CsvWriterTest {

    @Test
    public void testWriteCsv() throws IOException {
        StringWriter sw = new StringWriter();
        CsvWriter.dsl().to(sw).appendRow("hello", "world!\nnew line").appendRow("something");

        assertEquals("hello,\"world!\n" +
                "new line\"\r\n" +
                "something\r\n", sw.toString());
    }

}