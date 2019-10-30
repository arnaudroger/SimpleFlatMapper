package org.simpleflatmapper.lightningcsv.test;

import org.junit.Test;
import org.simpleflatmapper.lightningcsv.CsvWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

import static org.junit.Assert.*;

public class CsvWriterTest {

    @Test
    public void testWriteCsvArrayRow() throws IOException {
        StringWriter sw = new StringWriter();
        CsvWriter.dsl().to(sw).appendRow("hello", "world!\nnew line").appendRow("something");

        assertEquals("hello,\"world!\n" +
                "new line\"\r\n" +
                "something\r\n", sw.toString());
    }

    @Test
    public void testWriteCsvListRow() throws IOException {
        StringWriter sw = new StringWriter();
        CsvWriter.dsl().to(sw).appendRow(Arrays.asList("hello", "world!\nnew line")).appendRow(Arrays.asList("something"));

        assertEquals("hello,\"world!\n" +
                "new line\"\r\n" +
                "something\r\n", sw.toString());
    }

    @Test
    public void testLowLevelCell() throws IOException {
        StringWriter sw = new StringWriter();
        CsvWriter.dsl().to(sw).appendCell("hello").appendCell( "world!\nnew line").endOfRow().appendCell("something").endOfRow();

        assertEquals("hello,\"world!\n" +
                "new line\"\r\n" +
                "something\r\n", sw.toString());
    }

    @Test
    public void testLowLevelCellSub() throws IOException {
        StringWriter sw = new StringWriter();
        CsvWriter.dsl().to(sw).appendCell("hello", 1, 4).appendCell( "world!\nnew line", 1, 2).endOfRow().appendCell("something", 1, 2).endOfRow();

        assertEquals("ell,o\r\n" +
                "o\r\n", sw.toString());
    }
    @Test
    public void testLowLevelCellCharArray() throws IOException {
        StringWriter sw = new StringWriter();
        CsvWriter.dsl().to(sw).appendCell("hello".toCharArray(), 1, 4).appendCell( "world!\nnew line".toCharArray(), 1, 2).endOfRow().appendCell("something".toCharArray(), 1, 2).endOfRow();

        assertEquals("ell,o\r\n" +
                "o\r\n", sw.toString());
    }

    @Test
    public void testLowLevelCellCharArraySub() throws IOException {
        StringWriter sw = new StringWriter();
        CsvWriter.dsl().to(sw).appendCell("hello".toCharArray()).appendCell( "world!\nnew line".toCharArray()).endOfRow().appendCell("something".toCharArray()).endOfRow();

        assertEquals("hello,\"world!\n" +
                "new line\"\r\n" +
                "something\r\n", sw.toString());
    }
    @Test
    public void testMixLevelCellCharSequence() throws IOException {
        StringWriter sw = new StringWriter();
        CsvWriter.dsl().to(sw).appendCell("hello").appendRow( "world!\nnew line").appendCell("something").appendRow();

        assertEquals("hello,\"world!\n" +
                "new line\"\r\n" +
                "something\r\n", sw.toString());
    }

    @Test
    public void testMixLevelCellCharArray() throws IOException {
        StringWriter sw = new StringWriter();
        CsvWriter.dsl().to(sw).appendCell("hello".toCharArray()).appendRow( "world!\nnew line").appendCell("something".toCharArray()).appendRow();

        assertEquals("hello,\"world!\n" +
                "new line\"\r\n" +
                "something\r\n", sw.toString());
    }

}