package org.sfm.csv;

import org.junit.Test;
import org.sfm.csv.parser.CellConsumer;
import org.sfm.tuples.*;
import org.sfm.utils.ListHandler;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class CsvParserTest {


	private static String[][] SAMPLE_CSV_MIX_EXPECTATION =
			{
					{"cell1", "cell2", ""},
					{"cell\r\"value\"", "val2"},
					{"val3"},
					{"val4"}
			};
	@Test
	public void testReadCsvReaderLF() throws IOException {
		testCsvReader(SAMPLE_CSV_MIX_EXPECTATION, ',', '"', "\n");
	}

	@Test
	public void testReadCsvReaderCR() throws IOException {
		testCsvReader(SAMPLE_CSV_MIX_EXPECTATION, ',', '"', "\r");
	}

	@Test
	public void testReadCsvReaderCRLF() throws IOException {
		testCsvReader(SAMPLE_CSV_MIX_EXPECTATION, ',', '"', "\r\n");
	}

	@Test
	public void testReadCsvReaderTabs() throws IOException {
		testCsvReader(SAMPLE_CSV_MIX_EXPECTATION, '\t', '"', "\n");
	}

	@Test
	public void testDSLRootConfig() {
		assertEquals(9, CsvParser.bufferSize(9).bufferSize());
		assertEquals(3, CsvParser.limit(3).limit());
		assertEquals(3, CsvParser.skip(3).skip());
		assertEquals('-', CsvParser.separator('-').separator());
		assertEquals(';', CsvParser.quote(';').quote());
	}

	@Test
	public void testDSLRootAction() throws IOException {
		Iterator<String[]> it = CsvParser.iterate(getOneRowReader());
		assertTrue(it.hasNext());
		assertArrayEquals(new String[]{"value"}, it.next());
		assertFalse(it.hasNext());

		assertArrayEquals(new String[][]{{"value"}}, CsvParser.parse(getOneRowReader(), new AccumulateCellConsumer()).allValues());

	}

	@Test
	public void testDSLWithMapper() throws IOException {
		Iterator<Tuple2<String, String>> iterate =  CsvParser.<Tuple2<String, String>>mapTo(Tuples.typeDef(String.class, String.class)).iterate(new StringReader("val0,val1\nvalue1,value2"));

		assertTrue(iterate.hasNext());
		Tuple2<String, String> tuple2 = iterate.next();
		assertEquals("value1", tuple2.first());
		assertEquals("value2", tuple2.second());
		assertFalse(iterate.hasNext());

		//assertEquals("value", CsvParser.mapTo(String.class).iterate(new StringReader("val\nvalue")).next());
	}

	@Test
	public void testDSLMapWith() throws IOException {
		CsvMapper<Tuple2<String, String>> mapper = CsvMapperFactory.newInstance().newMapper(Tuples.typeDef(String.class, String.class));
		Iterator<Tuple2<String, String>> iterate =  CsvParser.<Tuple2<String, String>>mapWith(mapper).iterate(new StringReader("val0,val1\nvalue1,value2"));

		assertTrue(iterate.hasNext());
		Tuple2<String, String> tuple2 = iterate.next();
		assertEquals("value1", tuple2.first());
		assertEquals("value2", tuple2.second());
		assertFalse(iterate.hasNext());
	}

	@Test
	public void testDSLWitStatichMapper() throws IOException {
		Iterator<Tuple2<String, String>> iterate =  CsvParser.<Tuple2<String, String>>mapTo(Tuples.typeDef(String.class, String.class)).headers("0", "1").iterate(new StringReader("value1,value2"));

		assertTrue(iterate.hasNext());
		Tuple2<String, String> tuple2 = iterate.next();
		assertEquals("value1", tuple2.first());
		assertEquals("value2", tuple2.second());
		assertFalse(iterate.hasNext());
	}

	@Test
	public void testDSLMapToString() throws IOException {
		Iterator<String> iterate = CsvParser.mapTo(String.class).headers("value").iterate(new StringReader("value1,value2"));
		assertTrue(iterate.hasNext());
		String tuple2 = iterate.next();
		assertEquals("value1", tuple2);
		assertFalse(iterate.hasNext());
	}
	@Test
	public void testDSLMapToTuple2() throws IOException {
		Iterator<Tuple2<String, String>> iterate = CsvParser.mapTo(String.class, String.class).headers("0", "1").iterate(new StringReader("value1,value2"));
		assertTrue(iterate.hasNext());
		Tuple2<String, String> tuple2 = iterate.next();
		assertEquals("value1", tuple2.first());
		assertEquals("value2", tuple2.second());
		assertFalse(iterate.hasNext());
	}

	@Test
	public void testDSLMapToTuple3() throws IOException {
		Iterator<Tuple3<String, String, String>> iterate = CsvParser.mapTo(String.class, String.class, String.class).headers("0", "1", "2").iterate(new StringReader("value1,value2,value3"));
		assertTrue(iterate.hasNext());
		Tuple3<String, String, String> tuple2 = iterate.next();
		assertEquals("value1", tuple2.first());
		assertEquals("value2", tuple2.second());
		assertEquals("value3", tuple2.third());
		assertFalse(iterate.hasNext());
	}

	@Test
	public void testDSLMapToTuple4() throws IOException {
		Iterator<Tuple4<String, String, String, String>> iterate =
				CsvParser.mapTo(String.class, String.class, String.class, String.class).headers("0", "1", "2", "3").iterate(new StringReader("value1,value2,value3,value4"));
		assertTrue(iterate.hasNext());
		Tuple4<String, String, String, String> tuple2 = iterate.next();
		assertEquals("value1", tuple2.first());
		assertEquals("value2", tuple2.second());
		assertEquals("value3", tuple2.third());
		assertEquals("value4", tuple2.forth());
		assertFalse(iterate.hasNext());
	}

	@Test
	public void testDSLMapToTuple5() throws IOException {
		Iterator<Tuple5<String, String, String, String, String>> iterate =
				CsvParser.mapTo(String.class, String.class, String.class, String.class, String.class)
						.headers("0", "1", "2", "3", "4").iterate(new StringReader("value1,value2,value3,value4,value5"));
		assertTrue(iterate.hasNext());
		Tuple5<String, String, String, String, String> tuple2 = iterate.next();
		assertEquals("value1", tuple2.first());
		assertEquals("value2", tuple2.second());
		assertEquals("value3", tuple2.third());
		assertEquals("value4", tuple2.forth());
		assertEquals("value5", tuple2.fifth());
		assertFalse(iterate.hasNext());
	}

	private Reader getOneRowReader() {
		return new StringReader("value");
	}

	private void testCsvReader(String[][] expectations, char separator, char quote, String cr) throws IOException {
		CsvParser.DSL dsl = CsvParser
				.bufferSize(4)
				.separator(separator)
				.quote(quote);

		// reader call
		testParseAll(expectations, separator, quote, cr, dsl);

		testSkipThenParseAll(expectations, separator, quote, cr, dsl);

		testSkipThenParseRows(expectations, separator, quote, cr, dsl);

		testSkipThenParseRow(expectations, separator, quote, cr, dsl);

		// dsl call
		testIterate(expectations, separator, quote, cr, dsl);

		testSkipAndIterate(expectations, separator, quote, cr, dsl);

		testReadRows(expectations, separator, quote, cr, dsl);

		testReadRowsWithLimit(expectations, separator, quote, cr, dsl);

		testParse(expectations, separator, quote, cr, dsl);

		testParseWithLimit(expectations, separator, quote, cr, dsl);

	}

	private void testParse(String[][] expectations, char separator, char quote, String cr, CsvParser.DSL dsl) throws IOException {
		String[][] rows =
				dsl.parse(createReader(expectations, separator, quote, cr), new AccumulateCellConsumer()).allValues();

		assertArrayEquals(expectations, rows);
	}



	private void testParseWithLimit(String[][] expectations, char separator, char quote, String cr, CsvParser.DSL dsl) throws IOException {

		String[][] rows =
				dsl.limit(1).parse(createReader(expectations, separator, quote, cr), new AccumulateCellConsumer()).allValues();

		assertArrayEquals(toSubArray(expectations, 0, 1), rows);
	}


	private void testReadRows(String[][] expectations, char separator, char quote, String cr, CsvParser.DSL dsl) throws IOException {
		List<String[]> rows =
				dsl.reader(createReader(expectations, separator, quote, cr)).read(new ListHandler<String[]>()).getList();

		assertArrayEquals(expectations, rows.toArray(new String[0][]));
	}



	private void testReadRowsWithLimit(String[][] expectations, char separator, char quote, String cr, CsvParser.DSL dsl) throws IOException {
		List<String[]> rows =
				dsl.reader(createReader(expectations, separator, quote, cr)).read(new ListHandler<String[]>(), 1).getList();

		assertArrayEquals(toSubArray(expectations, 0, 1), rows.toArray(new String[0][]));
	}

	private void testIterate(String[][] expectations, char separator, char quote, String cr, CsvParser.DSL dsl) throws IOException {

		List<String[]> rows = new ArrayList<String[]>();
		for(String[] row : dsl.reader(createReader(expectations, separator, quote, cr))) {
			rows.add(row);
		}

		assertArrayEquals(expectations, rows.toArray(new String[0][]));
	}

	private void testSkipAndIterate(String[][] expectations, char separator, char quote, String cr, CsvParser.DSL dsl) throws IOException {

		List<String[]> rows = new ArrayList<String[]>();
		for(String[] row : dsl.skip(1).reader(createReader(expectations, separator, quote, cr))) {
			rows.add(row);
		}

		assertArrayEquals(toSubArray(expectations, 1), rows.toArray(new String[0][]));
	}

	private void testSkipThenParseRow(String[][] expectations, char separator, char quote, String cr, CsvParser.DSL dsl) throws IOException {
		AccumulateCellConsumer cellConsumer = new AccumulateCellConsumer();
		dsl.skip(1).reader(createReader(expectations, separator, quote, cr)).parseRow(cellConsumer);

		assertArrayEquals(toSubArray(expectations, 1, 1), cellConsumer.allValues());
	}

	private void testSkipThenParseRows(String[][] expectations, char separator, char quote, String cr, CsvParser.DSL dsl) throws IOException {
		String[][] cells;
		cells = dsl.skip(1).reader(createReader(expectations, separator, quote, cr)).parseRows(new AccumulateCellConsumer(), 2).allValues();

		assertArrayEquals(toSubArray(expectations, 1, 2), cells);
	}

	private void testSkipThenParseAll(String[][] expectations, char separator, char quote, String cr, CsvParser.DSL dsl) throws IOException {
		String[][] cells;
		cells = dsl.skip(1).reader(createReader(expectations, separator, quote, cr)).parseAll(new AccumulateCellConsumer()).allValues();

		assertArrayEquals(toSubArray(expectations, 1, expectations.length - 1), cells);
	}

	private String[][] toSubArray(String[][] expectations, int fromIndex) {
		return toSubArray(expectations, fromIndex, expectations.length - fromIndex);
	}
	private String[][] toSubArray(String[][] expectations, int fromIndex, int length) {
		return Arrays.asList(expectations).subList(fromIndex, fromIndex + length).toArray(new String[0][]);
	}

	private void testParseAll(String[][] expectations, char separator, char quote, String cr, CsvParser.DSL dsl) throws IOException {
		String[][] cells;
		cells =
				dsl.reader(createReader(expectations, separator, quote, cr)).parseAll(new AccumulateCellConsumer()).allValues();
		assertArrayEquals(expectations, cells);
	}

	private Reader createReader(String[][] expectations, char separator, char quote, String cr) {
		return new CharArrayReader(toCSV(expectations, separator, quote, cr).toString().toCharArray());
	}

	private CharSequence toCSV(String[][] cells, char separator, char quoteChar, String carriageReturn) {
		StringBuilder sb = new StringBuilder();

		for(int rowIndex = 0; rowIndex < cells.length; rowIndex++) {
			String[] row = cells[rowIndex];

			for (int colIndex = 0; colIndex < row.length; colIndex++) {
				String cell = row[colIndex];
				if (colIndex > 0) {
					sb.append(separator);
				}
				if (cell.indexOf(quoteChar) != -1) {
					sb.append(quoteChar);
					for (int j = 0; j < cell.length(); j++) {
						char c = cell.charAt(j);
						if (c == quoteChar) {
							sb.append(quoteChar);
						}
						sb.append(c);
					}
					sb.append(quoteChar);
				} else {
					sb.append(cell);
				}
			}
			sb.append(carriageReturn);

		}

		return sb;
	}

	int i = 0;

	//IFJAVA8_START
	@Test
	public void testStreamRows() throws
			IOException {
		Reader sr = new StringReader("row1\nrow2\nrow3");
		i = 0;
		CsvParser.stream(sr).forEach(strings -> assertArrayEquals(new String[] {"row" + ++i}, strings));
		assertEquals(3, i);
	}

	@Test
	public void testStreamRowsSkip() throws
			IOException {
		Reader sr = new StringReader("row1\nrow2\nrow3");
		i = 1;
		CsvParser.skip(1).stream(sr).forEach(strings -> assertArrayEquals(new String[]{"row" + ++i}, strings));
		assertEquals(3, i);
	}

	@Test
	public void testStreamRowsLimit() throws
			IOException {
		Reader sr = new StringReader("row1\nrow2\nrow3");
		i = 1;
		CsvParser.skip(1).stream(sr).limit(1).forEach(strings -> assertArrayEquals(new String[]{"row" + ++i}, strings));
		assertEquals(2, i);
	}



	//IFJAVA8_END

	private static class AccumulateCellConsumer implements CellConsumer {
		final List<String[]> rows = new ArrayList<String[]>();
		final List<String> currentRow = new ArrayList<String>();

		@Override
		public void newCell(char[] chars, int offset, int length) {
			currentRow.add(new String(chars, offset, length));
		}

		@Override
		public void endOfRow() {
			rows.add(currentRow.toArray(new String[0]));
			currentRow.clear();
		}

		@Override
		public void end() {
			if (!currentRow.isEmpty()) {
				rows.add(currentRow.toArray(new String[0]));
			}
			currentRow.clear();
		}

		public String[][] allValues() {
			return rows.toArray(new String[0][]);
		}
	}

}
