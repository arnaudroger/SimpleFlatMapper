package org.sfm.csv.parser;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.junit.Test;
import org.sfm.utils.ListHandler;

public class CsvParserTest {

	@Test
	public void testReadCsvReaderLF() throws IOException {
		testReadCsv(new StringReader("cell1,cell2,\n\"cell\r\"\"value\"\"\",val2\nval3"));
	}
	@Test
	public void testReadCsvReaderCR() throws IOException {
		testReadCsv(new StringReader("cell1,cell2,\r\"cell\r\"\"value\"\"\",val2\rval3"));
	}
	@Test
	public void testReadCsvReaderCRLF() throws IOException {
		testReadCsv(new StringReader("cell1,cell2,\r\n\"cell\r\"\"value\"\"\",val2\r\nval3"));
	}
	@Test
	public void testReadCsvReaderMixed() throws IOException {
		testReadCsv(new StringReader("cell1,cell2,\r\"cell\r\"\"value\"\"\",val2\nval3"));
	}

	private void testReadCsv(Reader sr) throws IOException {
		final CharSequence[][] css = new CharSequence[3][3];
		new CsvParser(8).parse(sr, new CharsCellHandler() {
			int row = 0, col = 0;
			@Override
			public void newCell(char[] chars, int offset, int length) {
				String value = new String(chars, offset, length);
				css[row][col++] = value;
			}
			
			@Override
			public boolean endOfRow() {
				row++;
				col = 0;
				return true;
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
		assertEquals("val3", css[2][0].toString());
	}
	
	@Test
	public void testReadRowsCsvReader() throws IOException {
		Reader sr = new StringReader("cell1,cell2,\n\"cell\r\"\"value\"\"\",val2");
		List<String[]> list = new CsvParser(8).readRows(sr, new ListHandler<String[]>()).getList();
		assertEquals("cell1", list.get(0)[0]);
		assertEquals("cell2", list.get(0)[1]);
		assertEquals("", list.get(0)[2]);
		assertEquals("cell\r\"value\"", list.get(1)[0]);
		assertEquals("val2", list.get(1)[1]);
		
	}
}
