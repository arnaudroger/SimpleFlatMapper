package org.sfm.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;

public class CsvParserTest {

	@Test
	public void testReadCsvInputStream() throws IOException {
		InputStream sr = new ByteArrayInputStream("cell1,cell2,\n\"cell\r\"\"value\"\"\",val2".getBytes("UTF-8"));
		final CharSequence[][] css = new CharSequence[2][3];
		new CsvParser(8).parse(sr, new BytesCellHandler() {
			
			@Override
			public void cell(long row, long col, byte[] bytes, int offset, int length) {
				String buffer = new String(bytes);
				String value = new String(bytes, offset, length);
				System.out.println(buffer + " => " + value);
				css[(int)row][(int)col] = value;
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
			
			@Override
			public void cell(long row, long col, char[] chars, int offset, int length) {
				String buffer = new String(chars);
				String value = new String(chars, offset, length);
				System.out.println(buffer + " => " + value);
				css[(int)row][(int)col] = value;
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
