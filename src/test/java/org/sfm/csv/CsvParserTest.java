package org.sfm.csv;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.junit.Test;
import org.sfm.csv.parser.CellConsumer;
import org.sfm.csv.parser.StringArrayConsumer;
import org.sfm.utils.ListHandler;
import org.sfm.utils.RowHandler;

public class CsvParserTest {

	@Test
	public void testReadCsvReaderLF() throws IOException {
		testReadCsv(new StringReader("cell1,cell2,\n\"cell\r\"\"value\"\"\",val2\nval3\nval4"));
	}
	@Test
	public void testReadCsvReaderCR() throws IOException {
		testReadCsv(new StringReader("cell1,cell2,\r\"cell\r\"\"value\"\"\",val2\rval3\rval4"));
	}
	@Test
	public void testReadCsvReaderCRLF() throws IOException {
		testReadCsv(new StringReader("cell1,cell2,\r\n"
				+ "\"cell\r\"\"value\"\"\",val2\r\n"
				+ "val3\r\nval4"));
	}
	@Test
	public void testReadCsvReaderMixed() throws IOException {
		testReadCsv(new StringReader("cell1,cell2,\r"
				+ "\"cell\r\"\"value\"\"\",val2\r"
				+ "val3\nval4"));
	}

	private void testReadCsv(Reader sr) throws IOException {
		final CharSequence[][] css = new CharSequence[4][3];
		new CsvParser(8).parse(sr, new CellConsumer() {
			int row = 0, col = 0;
			@Override
			public void newCell(char[] chars, int offset, int length) {
				String value = new String(chars, offset, length);
				System.out.println("X'" + value + "'X");
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
		assertEquals("val3", css[2][0].toString());
		assertEquals("val4", css[3][0].toString());
	}

	@Test
	public void testReadRowsSkip() throws
			IOException {
		Reader sr = new StringReader("row1\nrow2\nrow3");
		int nbRows = new CsvParser().parse(sr,  StringArrayConsumer.newInstance(new RowHandler<String[]>() {
			int i = 1;
			@Override
			public void handle(String[] strings) throws Exception {
				assertEquals("row" + (i+1) , strings[0]);
				i++;
			}
		}), 1).handler().i;

		assertEquals(3, nbRows);
	}

	@Test
	public void testReadRowsSkipLimit() throws
			IOException {
		Reader sr = new StringReader("row1\nrow2\nrow3");
		int nbRows = new CsvParser().parse(sr,  StringArrayConsumer.newInstance(new RowHandler<String[]>() {
			int i = 1;
			@Override
			public void handle(String[] strings) throws Exception {
				assertEquals("row" + (i+1) , strings[0]);
				i++;
			}
		}), 1, 1).handler().i;

		assertEquals(2, nbRows);
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
