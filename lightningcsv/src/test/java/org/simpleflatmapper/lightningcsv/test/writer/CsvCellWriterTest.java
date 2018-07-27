package org.simpleflatmapper.lightningcsv.test.writer;

import org.junit.Test;
import org.simpleflatmapper.lightningcsv.CsvCellWriter;

import java.io.IOException;

import static org.junit.Assert.*;


public class CsvCellWriterTest {


    @Test
    public void defaultDoesNotEscapeOnRegularText() throws IOException {
        StringBuilder sb = new StringBuilder();
        CsvCellWriter.DEFAULT_WRITER.writeValue("val", sb);
        assertEquals("val", sb.toString());
    }

    @Test
    public void alwaysEscapeEscapeOnRegularText() throws IOException {
        StringBuilder sb = new StringBuilder();
        CsvCellWriter.DEFAULT_WRITER.alwaysEscape().writeValue("val", sb);
        assertEquals("\"val\"", sb.toString());
    }

    @Test
    public void defaultEscapeOnComma() throws IOException {
        StringBuilder sb = new StringBuilder();
        CsvCellWriter.DEFAULT_WRITER.writeValue("va,l", sb);
        assertEquals("\"va,l\"", sb.toString());
    }

    @Test
    public void defaultEscapeCR() throws IOException {
        StringBuilder sb = new StringBuilder();
        CsvCellWriter.DEFAULT_WRITER.writeValue("va\rl", sb);
        assertEquals("\"va\rl\"", sb.toString());
    }
    @Test
    public void defaultEscapeLF() throws IOException {
        StringBuilder sb = new StringBuilder();
        CsvCellWriter.DEFAULT_WRITER.writeValue("va\nl", sb);
        assertEquals("\"va\nl\"", sb.toString());
    }

    @Test
    public void defaultEscapeQuote() throws IOException {
        StringBuilder sb = new StringBuilder();
        CsvCellWriter.DEFAULT_WRITER.writeValue("va\"l", sb);
        assertEquals("\"va\"\"l\"", sb.toString());
    }

    @Test
    public void eolCREscapeLF() throws IOException {
        StringBuilder sb = new StringBuilder();
        CsvCellWriter.DEFAULT_WRITER.endOfLine("\r").writeValue("va\nl", sb);
        assertEquals("\"va\nl\"", sb.toString());
    }

}
