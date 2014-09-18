package org.sfm.csv.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;
import org.sfm.csv.parser.BytesCellHandler;
import org.sfm.csv.parser.CharsCellHandler;
import org.sfm.csv.parser.CsvParser;

public class CsvParserTest {

	@Test
	public void testReadCsvInputStream() throws IOException {
		InputStream sr = new ByteArrayInputStream("cell1,cell2,\n\"cell\r\"\"value\"\"\",val2".getBytes("UTF-8"));
		final CharSequence[][] css = new CharSequence[2][3];
		new CsvParser(8).parse(sr, new BytesCellHandler() {
			int row = 0, col = 0;
			@Override
			public void newCell(byte[] bytes, int offset, int length) {
				String value = new String(bytes, offset, length);
				css[row][col++] = value;
			}
			@Override
			public void endOfRow() {
				row++;
				col = 0;
			}
			@Override
			public void end() {
			}
		});
		assertEquals("cell1", css[0][0].toString());
		assertEquals("cell2", css[0][1].toString());
		assertEquals("", css[0][2].toString());
		assertEquals("\"cell\r\"\"value\"\"\"", css[1][0].toString());
		assertEquals("val2", css[1][1].toString());
		assertNull(css[1][2]);
		
	}

	@Test
	public void testReadCsvReader() throws IOException {
		Reader sr = new StringReader("cell1,cell2,\n\"cell\r\"\"value\"\"\",val2");
		final CharSequence[][] css = new CharSequence[2][3];
		new CsvParser(8).parse(sr, new CharsCellHandler() {
			int row = 0, col = 0;
			@Override
			public void newCell(char[] chars, int offset, int length) {
				String value = new String(chars, offset, length);
				css[row][col++] = value;
			}
			
			@Override
			public void endOfRow() {
				row++;
				col = 0;
			}

			@Override
			public void end() {
			}
		});
		assertEquals("cell1", css[0][0].toString());
		assertEquals("cell2", css[0][1].toString());
		assertEquals("", css[0][2].toString());
		assertEquals("\"cell\r\"\"value\"\"\"", css[1][0].toString());
		assertEquals("val2", css[1][1].toString());
		assertNull(css[1][2]);
		
	}
}
