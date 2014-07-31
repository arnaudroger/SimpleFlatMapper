package org.sfm.text;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;
import static org.junit.Assert.*;

public class CsvParserTest {

	@Test
	public void testReadCsv() throws IOException {
		StringReader sr = new StringReader("cell1,cell2,\n\"cell\r\"\"value\"\"\",val2");
		final CharSequence[][] css = new CharSequence[2][3];
		new CsvParser().parser(sr, new CellHandler() {
			
			@Override
			public void cell(long row, long col, CharSequence value) {
				css[(int)row][(int)col] = value;
			}
		});
		assertEquals("cell1", css[0][0].toString());
		assertEquals("cell2", css[0][1].toString());
		assertEquals("", css[0][2].toString());
		assertEquals("cell\n\"value\"", css[1][0].toString());
		assertEquals("val2", css[1][1].toString());
		assertNull(css[1][2]);
		
	}

}
