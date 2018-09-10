package org.simpleflatmapper.lightningcsv.test;

import org.junit.Test;
import org.simpleflatmapper.lightningcsv.CloseableCsvReader;
import org.simpleflatmapper.lightningcsv.CsvParser;
import org.simpleflatmapper.lightningcsv.CsvReader;
import org.simpleflatmapper.lightningcsv.Row;
import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.lightningcsv.parser.BufferOverflowException;
import org.simpleflatmapper.lightningcsv.parser.CellConsumer;
import org.simpleflatmapper.util.TypeReference;
import org.simpleflatmapper.util.CloseableIterator;
import org.simpleflatmapper.util.ListCollector;

import java.io.CharArrayReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
//IFJAVA8_START
import java.util.stream.Collectors;
import java.util.stream.Stream;
//IFJAVA8_END
import static org.junit.Assert.*;

public class CsvParserTest {


	public static final String ROW_DATA = "h1,h2\nv1,v2\nv3\nv4,v5,v6";
	private static String[][] SAMPLE_CSV_MIX_EXPECTATION =
			{
					{"cell1", "cell2", ""},
					{"cell\r\"value\"", "val2"},
					{"val3"},
					{"val4", ""}
			};
	@Test
	public void testReadCsvReaderLF() throws IOException {
		testCsvReader(SAMPLE_CSV_MIX_EXPECTATION, ',', '"', '\\', "\n");
		testCsvReader(SAMPLE_CSV_MIX_EXPECTATION, ',', '"', '"', "\n");
	}


	@Test
	public void testReadCsvReaderLFSINGLEQUOTE() throws IOException {
		testCsvReader(SAMPLE_CSV_MIX_EXPECTATION, ',', '\'', '\'', "\n");
		testCsvReader(SAMPLE_CSV_MIX_EXPECTATION, ',', '\'', '\\', "\n");
	}

	@Test
	public void testReadCsvReaderCR() throws IOException {
		testCsvReader(SAMPLE_CSV_MIX_EXPECTATION, ',', '"', '"',"\r");
		testCsvReader(SAMPLE_CSV_MIX_EXPECTATION, ',', '"', '\\',"\r");
	}

	@Test
	public void testReadCsvReaderCRLF() throws IOException {
		testCsvReader(SAMPLE_CSV_MIX_EXPECTATION, ',', '"', '"',"\r\n");
		testCsvReader(SAMPLE_CSV_MIX_EXPECTATION, ',', '"', '\\',"\r\n");
	}

	@Test
	public void testReadCsvReaderTabs() throws IOException {
		testCsvReader(SAMPLE_CSV_MIX_EXPECTATION, '\t', '"','"', "\n");
		testCsvReader(SAMPLE_CSV_MIX_EXPECTATION, '\t', '"','\\', "\n");
	}

    @Test
    public void testReadCsvReaderOneChar() throws IOException {
        Iterator<String[]> iterator = CsvParser.iterator(new StringReader("0"));
        String[] strs = iterator.next();
        assertEquals("0", strs[0]);
    }
    
    @Test
	public void test459EscapeChar() throws IOException {
		Iterator<String[]> iterator = CsvParser.dsl().escape('\\').iterator(new StringReader("\"blah\\r\\n\\\"helo\\\"\",ggg"));
		String[] strs = iterator.next();
		assertEquals("blah\r\n\"helo\"", strs[0]);
		assertEquals("ggg", strs[1]);
	}

    @Test
    public void testSimpleCsv() throws  IOException {
		List<String[]> list = CsvParser.forEach("a,b\n" +
				"c", new ListCollector<String[]>()).getList();

		assertArrayEquals(new String[] {"a", "b"}, list.get(0));
		assertArrayEquals(new String[] {"c"}, list.get(1));
	}

	@Test
	public void testSimpleCsQuotes() throws  IOException {
		List<String[]> list = CsvParser.forEach("\"a\",\"b\"\n" +
				"\"c\"", new ListCollector<String[]>()).getList();

		assertArrayEquals(new String[] {"a", "b"}, list.get(0));
		assertArrayEquals(new String[] {"c"}, list.get(1));
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
		Iterator<String[]> it = CsvParser.iterator(getOneRowReader());
		assertTrue(it.hasNext());
		assertArrayEquals(new String[]{"value"}, it.next());
		assertFalse(it.hasNext());

		assertArrayEquals(new String[][]{{"value"}}, CsvParser.parse(getOneRowReader(), new AccumulateCellConsumer()).allValues());

	}

	private Reader getOneRowReader() {
		return new StringReader("value");
	}

	private void testCsvReader(String[][] expectations, char separator, char quote, char escape, String cr) throws IOException {
		CsvParser.DSL dsl = CsvParser
				.bufferSize(4)
				.separator(separator)
				.escape(escape)
				.quote(quote);

		CsvParser.DSL dslTrim = dsl.trimSpaces();

		testDsl(expectations, separator, quote, escape, cr, dsl);
		testDsl(expectations, separator, quote, escape, cr, dslTrim);
		testDsl(expectations, separator, quote, escape, cr, dsl.parallelReader());


	}

	private void testDsl(String[][] expectations, char separator, char quote, char escape, String cr, CsvParser.DSL dsl) throws IOException {
		// reader call
		testParseAll(expectations, separator, quote, escape, cr, dsl);

		testSkipThenParseAll(expectations, separator, quote, escape, cr, dsl);

		testSkipThenParseRows(expectations, separator, quote, escape, cr, dsl);

		testSkipThenParseRow(expectations, separator, quote, escape, cr, dsl);

		// schema call
		testIterator(expectations, separator, quote, escape, cr, dsl);

		testSkipAndIterator(expectations, separator, quote, escape, cr, dsl);

		testReadRows(expectations, separator, quote, escape, cr, dsl);

		testReadRowsWithLimit(expectations, separator, quote, escape, cr, dsl);

		testParse(expectations, separator, quote, escape, cr, dsl);

		testParseWithLimit(expectations, separator, quote, escape, cr, dsl);
	}

	private void testParse(String[][] expectations, char separator, char quote, char escape, String cr, CsvParser.DSL dsl) throws IOException {
		String[][] rows =
				dsl.parse(createReader(expectations, separator, quote, escape, cr), new AccumulateCellConsumer()).allValues();

		assertArrayEquals(expectations, rows);
	}



	private void testParseWithLimit(String[][] expectations, char separator, char quote, char escape, String cr, CsvParser.DSL dsl) throws IOException {

		String[][] rows =
				dsl.limit(1).parse(createReader(expectations, separator, quote, escape, cr), new AccumulateCellConsumer()).allValues();

		assertArrayEquals(toSubArray(expectations, 0, 1), rows);
	}


	private void testReadRows(String[][] expectations, char separator, char quote, char escape, String cr, CsvParser.DSL dsl) throws IOException {
		List<String[]> rows =
				dsl.reader(createReader(expectations, separator, quote, escape, cr)).read(new ListCollector<String[]>()).getList();

		assertArrayEquals(expectations, rows.toArray(new String[0][]));
	}



	private void testReadRowsWithLimit(String[][] expectations, char separator, char quote, char escape, String cr, CsvParser.DSL dsl) throws IOException {
		List<String[]> rows =
				dsl.reader(createReader(expectations, separator, quote, escape, cr)).read(new ListCollector<String[]>(), 1).getList();

		assertArrayEquals(toSubArray(expectations, 0, 1), rows.toArray(new String[0][]));
	}

	private void testIterator(String[][] expectations, char separator, char quote, char escape, String cr, CsvParser.DSL dsl) throws IOException {

		List<String[]> rows = new ArrayList<String[]>();
		for(String[] row : dsl.reader(createReader(expectations, separator, quote, escape, cr))) {
			rows.add(row);
		}

		assertArrayEquals(expectations, rows.toArray(new String[0][]));
	}

	private void testSkipAndIterator(String[][] expectations, char separator, char quote, char escape, String cr, CsvParser.DSL dsl) throws IOException {

		List<String[]> rows = new ArrayList<String[]>();
		for(String[] row : dsl.skip(1).reader(createReader(expectations, separator, quote, escape, cr))) {
			rows.add(row);
		}

		assertArrayEquals(toSubArray(expectations, 1), rows.toArray(new String[0][]));
	}

	private void testSkipThenParseRow(String[][] expectations, char separator, char quote, char escape, String cr, CsvParser.DSL dsl) throws IOException {
		AccumulateCellConsumer cellConsumer = new AccumulateCellConsumer();
		dsl.skip(1).reader(createReader(expectations, separator, quote, escape, cr)).parseRow(cellConsumer);

		assertArrayEquals(toSubArray(expectations, 1, 1), cellConsumer.allValues());
	}

	private void testSkipThenParseRows(String[][] expectations, char separator, char quote, char escape, String cr, CsvParser.DSL dsl) throws IOException {
		String[][] cells;
		cells = dsl
				.skip(1)
				.reader(createReader(expectations, separator, quote, escape, cr))
				.parseRows(new AccumulateCellConsumer(), 2)
				.allValues();

		assertArrayEquals(toSubArray(expectations, 1, 2), cells);
	}

	private void testSkipThenParseAll(String[][] expectations, char separator, char quote, char escape, String cr, CsvParser.DSL dsl) throws IOException {
		String[][] cells;
		cells =
				dsl
				.skip(1)
				.reader(createReader(expectations, separator, quote, escape, cr))
				.parseAll(new AccumulateCellConsumer()).allValues();

		assertArrayEquals(toSubArray(expectations, 1, expectations.length - 1), cells);
	}

	private String[][] toSubArray(String[][] expectations, int fromIndex) {
		return toSubArray(expectations, fromIndex, expectations.length - fromIndex);
	}
	private String[][] toSubArray(String[][] expectations, int fromIndex, int length) {
		return Arrays.asList(expectations).subList(fromIndex, fromIndex + length).toArray(new String[0][]);
	}

	private void testParseAll(String[][] expectations, char separator, char quote, char escape, String cr, CsvParser.DSL dsl) throws IOException {
		String[][] cells;
		cells =
				dsl.reader(createReader(expectations, separator, quote, escape, cr)).parseAll(new AccumulateCellConsumer()).allValues();
		assertArrayEquals(Arrays.deepToString(expectations) + " " + Arrays.deepToString(cells), expectations, cells);
	}

	private Reader createReader(String[][] expectations, char separator, char quote, char escape, String cr) {
		return new CharArrayReader(toCSV(expectations, separator, quote, escape, cr).toString().toCharArray());
	}

	private CharSequence toCSV(String[][] cells, char separator, char quoteChar, char escape, String carriageReturn) {
		StringBuilder sb = new StringBuilder();

		for(int rowIndex = 0; rowIndex < cells.length; rowIndex++) {
			String[] row = cells[rowIndex];

			for (int colIndex = 0; colIndex < row.length; colIndex++) {
				String cell = row[colIndex];
				if (colIndex > 0) {
					sb.append(separator);
				}
				if (needEscape(cell, quoteChar, separator)) {
					sb.append(quoteChar);
					for (int j = 0; j < cell.length(); j++) {
						char c = cell.charAt(j);
						if (c == quoteChar) {
							sb.append(escape);
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

	private boolean needEscape(String cell, char quote, char separator) {
		return cell.indexOf(quote) != 1
				||cell.indexOf(separator) != 1
				|| cell.indexOf('\n') != -1
				|| cell.indexOf('\r') != -1;
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

	@Test
	public void testStreamRowsFromFile() throws
			IOException {

		File f = createTempCsv("row1\nrow2\nrow3");
		i = 0;
		CsvParser.stream(f).forEach(strings -> assertArrayEquals(new String[] {"row" + ++i}, strings));
		assertEquals(3, i);
	}

	@Test
	public void testStreamRowsFromFileNewCall() throws
			IOException {

		File f = createTempCsv("row1\nrow2\nrow3");
		i = 0;
		assertTrue(CsvParser.stream(f, stream -> {
            stream.forEach(strings -> assertArrayEquals(new String[] {"row" + ++i}, strings));
            return Boolean.TRUE;
        }));
		assertEquals(3, i);
	}
	@Test
	public void testStreamRowsFromString() throws
			IOException {

		String f = ("row1\nrow2\nrow3");
		i = 0;
		CsvParser.stream(f).forEach(strings -> assertArrayEquals(new String[] {"row" + ++i}, strings));
		assertEquals(3, i);
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
		public boolean endOfRow() {
			rows.add(currentRow.toArray(new String[0]));
			currentRow.clear();
			return true;
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



	@Test
	public void testIssue84CsvParser() throws IOException {
		String str = "my_field,second_field\n" +
				",,";

		Iterator<String[]> iterator = CsvParser.iterator(new StringReader(str));

		while(iterator.hasNext()) {
			System.out.println(iterator.next());
		}

	}


	@Test
	public void testMaxBufferSize() throws IOException {
		Iterator<String[]> iterator = CsvParser.maxBufferSize(4).bufferSize(2).iterator(new StringReader( "f1\nf11\nf111"));
		iterator.next();
		iterator.next();
		try {
			iterator.next();
			fail("Expect BufferOverflowException");
		} catch (Exception e) {
			if (!(e instanceof BufferOverflowException)) {
				fail("Expect BufferOverflowException");
			}
			// expected
		}

		iterator = CsvParser.maxBufferSize(9).bufferSize(2).iterator(new StringReader("12345678\n1234567890"));
		iterator.next();
		try {
			iterator.next();
			fail("Expect BufferOverflowException");
		} catch (Exception e) {
			// expected
			if (!(e instanceof BufferOverflowException)) {
				fail("Expect BufferOverflowException");
			}
		}

		iterator = CsvParser.maxBufferSize(11).bufferSize(2).iterator(new StringReader("1234567890"));

		String[] row = iterator.next();
		assertEquals("1234567890", row[0]);

	}

	@Test
	public void testIterateStringsFromFile() throws IOException {
		File file = createTempCsv("1,2");

		CloseableIterator<String[]> iterator = CsvParser.iterator(file);
		try {
			assertArrayEquals(new String[]{"1", "2"}, iterator.next());
		} finally {
			iterator.close();
		}

	}

	@Test
	public void testIterateStringsFromString() throws IOException {
		Iterator<String[]> iterator = CsvParser.iterator("1,2");
		assertArrayEquals(new String[]{"1", "2"}, iterator.next());
		iterator = CsvParser.iterator(new StringBuilder("1,2"));
		assertArrayEquals(new String[]{"1", "2"}, iterator.next());
	}
	

	
	private File createTempCsv(String str) throws IOException {
		File file = File.createTempFile("test", ".csv");

		FileWriter writer = new FileWriter(file);
		try {
			writer.write(str);
		} finally {
			writer.close();
		}
		return file;
	}

	@Test
	public void testCsvReaderFromFile() throws IOException {
		File file = createTempCsv("value");

		CloseableCsvReader reader = CsvParser.reader(file);
		try {
			Iterator<String[]> iterator = reader.iterator();
			assertArrayEquals(new String[] {"value"}, iterator.next());
		} finally {
			reader.close();
		}

	}

	@Test
	public void testCsvReaderFromString() throws IOException {
		CsvReader reader = CsvParser.reader("value");
		Iterator<String[]> iterator = reader.iterator();
		assertArrayEquals(new String[] {"value"}, iterator.next());

		reader = CsvParser.reader(new StringBuilder("value"));
		iterator = reader.iterator();
		assertArrayEquals(new String[] {"value"}, iterator.next());
	}


	@Test
	public void testParseFromFile() throws IOException {
		File file = createTempCsv("value");

		final String[][] allValues = CsvParser.parse(file, new AccumulateCellConsumer()).allValues();

		assertArrayEquals(new String[][] {{"value"}}, allValues);

	}

	@Test
	public void testForEach() throws Exception {
		testForEachList(CsvParser.forEach("a,b\nc,d", new ListCollector<String[]>()).getList());
		testForEachList(CsvParser.forEach(createTempCsv("a,b\nc,d"), new ListCollector<String[]>()).getList());
		testForEachList(CsvParser.forEach(new StringReader("a,b\nc,d"), new ListCollector<String[]>()).getList());
	}

	private void testForEachList(List<String[]> list) {
		assertArrayEquals(new String[] { "a", "b" }, list.get(0));
		assertArrayEquals(new String[] { "c", "d" }, list.get(1));
		assertEquals(2, list.size());
	}

	@Test
	public void testParsingFromString() throws IOException {
		String[][] allValues = CsvParser.parse("value", new AccumulateCellConsumer()).allValues();
		assertArrayEquals(new String[][] {{"value"}}, allValues);
		allValues = CsvParser.parse(new StringBuilder("value"), new AccumulateCellConsumer()).allValues();
		assertArrayEquals(new String[][] {{"value"}}, allValues);
	}


	public static class MyScalaClass {
		public String myField;
		public java.util.Date secondField;
	}


	@Test
	public void test264() throws IOException {
		Iterator<String[]> it = CsvParser.iterator(new StringReader("\" \""));
		String[] strings = it.next();
		assertArrayEquals(new String[]{" "}, strings);

		it = CsvParser.iterator(new StringReader("\"\""));
		strings = it.next();
		assertArrayEquals(new String[]{""}, strings);

		it = CsvParser.iterator(new StringReader("345,\"\""));
		strings = it.next();
		assertArrayEquals(new String[]{"345", ""}, strings);

		it = CsvParser.iterator(new StringReader("345,\"\",543"));
		strings = it.next();
		assertArrayEquals(new String[]{"345", "", "543"}, strings);

		it = CsvParser.iterator(new StringReader("\"\"\""));
		strings = it.next();
		assertArrayEquals(new String[]{"\""}, strings);

		it = CsvParser.iterator(new StringReader("\"\"\"\""));
		strings = it.next();
		assertArrayEquals(new String[]{"\""}, strings);

	}

	@Test
	public void testQuotedStringShift() throws IOException {
		Iterator<String[]> it = CsvParser.iterator("\"\"\"a\"\"b\"\"c\"\"d\"");
		String[] strings = it.next();
		assertArrayEquals(new String[]{"\"a\"b\"c\"d"}, strings);
	}

	@Test
	public void testTrimSpaceToQuote() throws IOException {
		final String[] strings = CsvParser.dsl().trimSpaces().iterator("value, \"val\" ").next();
		assertArrayEquals(new String[] {"value", "val"}, strings);
	}

	@Test
	public void testTrimSpaceToQuoteQuoteProtectedSpaced() throws IOException {
		final String[] strings = CsvParser.dsl().trimSpaces().iterator("value, \"  \"  , \"a\"   ,   \"\",\"a\"    ").next();
		assertArrayEquals(new String[] {"value", "  ", "a", "", "a"}, strings);
	}
	@Test
	public void testTrimSpaceOnNoQuote() throws IOException {
		final CsvParser.DSL dsl = CsvParser.dsl().trimSpaces();
		assertArrayEquals(new String[] {"value", "val", "", ""}, dsl.iterator("value, val  ,, ").next());
		assertArrayEquals(new String[] {"value", "", "v"}, dsl.iterator("value,   ,v  ").next());
	}


	@Test
	public void testTrimSpaceOnEscapedComa() throws IOException {
		final String[] strings = CsvParser.dsl().trimSpaces().iterator("value,\" my val, but oy\"").next();
		assertArrayEquals(new String[] {"value", " my val, but oy"}, strings);
	}

	@Test
	public void testEmptyString() throws IOException {
		assertArrayEquals(new Object[][]{{""}}, toObjects(CsvParser.reader("\n")));
		assertArrayEquals(new Object[][]{{""}}, toObjects(CsvParser.separator('|').reader("\n")));
		assertArrayEquals(new Object[][]{{""}}, toObjects(CsvParser.dsl().trimSpaces().reader("\n")));
		assertArrayEquals(new Object[][]{{""}}, toObjectsIt(CsvParser.reader("\n")));
		assertArrayEquals(new Object[][]{{""}}, toObjectsIt(CsvParser.separator('|').reader("\n")));
		assertArrayEquals(new Object[][]{{""}}, toObjectsIt(CsvParser.dsl().trimSpaces().reader("\n")));
	}

	private Object[][] toObjects(CsvReader reader) throws IOException {
		final List<Object[]> objects = new ArrayList<Object[]>();

		reader.read(new CheckedConsumer<String[]>() {
			@Override
			public void accept(String[] strings) {
				objects.add(Arrays.copyOf(strings, strings.length, Object[].class));
			}
		});

		return objects.toArray(new Object[0][]);

	}

	private Object[][] toObjectsIt(CsvReader reader) throws IOException {
		final List<Object[]> objects = new ArrayList<Object[]>();

		Iterator<String[]> it = reader.iterator();

		while(it.hasNext()) {
			String[] strings = it.next();
			objects.add(Arrays.copyOf(strings, strings.length, Object[].class));
		}
		return objects.toArray(new Object[0][]);
	}

	@Test
	public void testOnequote() throws IOException  {
		assertArrayEquals(new Object[][]{{""}}, toObjects(CsvParser.reader("\"")));
		assertArrayEquals(new Object[][]{{""}}, toObjects(CsvParser.separator('|').reader("\"")));
		assertArrayEquals(new Object[][]{{""}}, toObjects(CsvParser.dsl().trimSpaces().reader("\"")));

		assertArrayEquals(new Object[][]{{""}}, toObjectsIt(CsvParser.reader("\"")));
		assertArrayEquals(new Object[][]{{""}}, toObjectsIt(CsvParser.separator('|').reader("\"")));
		assertArrayEquals(new Object[][]{{""}}, toObjectsIt(CsvParser.dsl().trimSpaces().reader("\"")));
	}

	@Test
	public void testOneSeparator() throws IOException  {
		assertArrayEquals(new Object[][] {{"", ""}}, toObjects(CsvParser.reader(",")));
		assertArrayEquals(new Object[][] {{"", ""}}, toObjectsIt(CsvParser.reader(",")));

		assertArrayEquals(new Object[][] {{"", ""}}, toObjects(CsvParser.separator('|').reader("|")));
		assertArrayEquals(new Object[][] {{"", ""}}, toObjects(CsvParser.dsl().trimSpaces().reader(",")));

		assertArrayEquals(new Object[][] {{"", ""}}, toObjectsIt(CsvParser.separator('|').reader("|")));
		assertArrayEquals(new Object[][] {{"", ""}}, toObjectsIt(CsvParser.dsl().trimSpaces().reader(",")));

	}

	@Test
	public void testNoUnescaping() throws IOException {
		StringReader stringReader = new StringReader("test,\" \"\"hello\"\" \"\n# this a comment, not data" );

		List<String[]> data = CsvParser
				.dsl()
				.disableUnescaping()
				.forEach(stringReader, new ListCollector<String[]>()).getList();

		assertEquals(2, data.size());
		assertArrayEquals(new String[] {"test", "\" \"\"hello\"\" \""}, data.get(0));
		assertArrayEquals(new String[] {"# this a comment", " not data"}, data.get(1));

	}
	@Test
	public void testYamlCommentParser() throws IOException  {
		CsvParser.DSLYamlComment dsl = CsvParser
				.dsl()
				.withYamlComments();
		testYamlCommentParser(dsl);
		testYamlCommentParser(dsl.disableSpecialisedCharConsumer());
	}

	private void testYamlCommentParser(CsvParser.DSLYamlComment dsl) throws IOException {

		String data = "test,\" \"\"hello\"\" \"\n# this a comment, not data\none more";

		List<String[]> rows = new ArrayList<String[]>();

		Iterator<String[]> iterator = dsl.iterator(data);
		while(iterator.hasNext()) {
			rows.add(iterator.next());
		}
		checkYamlCommentParserRows(rows);

		//IFJAVA8_START
		checkYamlCommentParserRows(dsl.stream(data).collect(Collectors.toList()));
		//IFJAVA8_END
		checkYamlCommentParserRows(dsl.forEach(data, new ListCollector<String[]>()).getList());

		File csv = createTempCsv(data);

		rows = new ArrayList<String[]>();

		CloseableIterator<String[]> citerator = dsl.iterator(csv);
		while(citerator.hasNext()) {
			rows.add(citerator.next());
		}
		checkYamlCommentParserRows(rows);


		//IFJAVA8_START
		checkYamlCommentParserRows(dsl.stream(csv).collect(Collectors.toList()));
		//IFJAVA8_END
		checkYamlCommentParserRows(dsl.forEach(csv, new ListCollector<String[]>()).getList());


		rows = new ArrayList<String[]>();

		iterator = dsl.iterator(new StringReader(data));
		while(iterator.hasNext()) {
			rows.add(iterator.next());
		}
		checkYamlCommentParserRows(rows);

		//IFJAVA8_START
		checkYamlCommentParserRows(dsl.stream(new StringReader(data)).collect(Collectors.toList()));
		//IFJAVA8_END
		checkYamlCommentParserRows(dsl.forEach(new StringReader(data), new ListCollector<String[]>()).getList());


		rows = new ArrayList<String[]>();

		iterator = dsl.iterator(new StringBuilder(data));
		while(iterator.hasNext()) {
			rows.add(iterator.next());
		}
		checkYamlCommentParserRows(rows);

		//IFJAVA8_START
		checkYamlCommentParserRows(dsl.stream(new StringBuilder(data)).collect(Collectors.toList()));
		//IFJAVA8_END
		checkYamlCommentParserRows(dsl.forEach(new StringBuilder(data), new ListCollector<String[]>()).getList());
	}


	@Test
	public void testYamlCommentInlineParser() throws IOException  {
		String data = "test,\" \"\"hello\"\" \"\n# this a comment, not data\none more";


		CsvParser.DSLYamlComment dsl = CsvParser
				.dsl()
				.withYamlCommentsAsCell();

		List<String[]> rows = new ArrayList<String[]>();

		Iterator<String[]> iterator = dsl.iterator(data);
		while(iterator.hasNext()) {
			rows.add(iterator.next());
		}
		checkYamlCommentParserAsCellRows(rows);

		//IFJAVA8_START
		checkYamlCommentParserAsCellRows(dsl.stream(data).collect(Collectors.toList()));
		//IFJAVA8_END
		checkYamlCommentParserAsCellRows(dsl.forEach(data, new ListCollector<String[]>()).getList());

		File csv = createTempCsv(data);

		rows = new ArrayList<String[]>();

		CloseableIterator<String[]> citerator = dsl.iterator(csv);
		while(citerator.hasNext()) {
			rows.add(citerator.next());
		}
		checkYamlCommentParserAsCellRows(rows);


		//IFJAVA8_START
		checkYamlCommentParserAsCellRows(dsl.stream(csv).collect(Collectors.toList()));
		//IFJAVA8_END
		checkYamlCommentParserAsCellRows(dsl.forEach(csv, new ListCollector<String[]>()).getList());


		rows = new ArrayList<String[]>();

		iterator = dsl.iterator(new StringReader(data));
		while(iterator.hasNext()) {
			rows.add(iterator.next());
		}
		checkYamlCommentParserAsCellRows(rows);

		//IFJAVA8_START
		checkYamlCommentParserAsCellRows(dsl.stream(new StringReader(data)).collect(Collectors.toList()));
		//IFJAVA8_END
		checkYamlCommentParserAsCellRows(dsl.forEach(new StringReader(data), new ListCollector<String[]>()).getList());


		rows = new ArrayList<String[]>();

		iterator = dsl.iterator(new StringBuilder(data));
		while(iterator.hasNext()) {
			rows.add(iterator.next());
		}
		checkYamlCommentParserAsCellRows(rows);

		//IFJAVA8_START
		checkYamlCommentParserAsCellRows(dsl.stream(new StringBuilder(data)).collect(Collectors.toList()));
		//IFJAVA8_END
		checkYamlCommentParserAsCellRows(dsl.forEach(new StringBuilder(data), new ListCollector<String[]>()).getList());

	}


	@Test
	public void testYamlCommentParserForEachRowComment() throws IOException  {
		String data = "test,\" \"\"hello\"\" \"\n# this a comment, not data\none more";


		CsvParser.DSLYamlComment dsl = CsvParser
				.dsl()
				.withYamlComments();

		ListCollector<String[]> rowCollector = new ListCollector<String[]>();
		ListCollector<String> commentCollector = new ListCollector<String>();


		dsl.forEach(data, rowCollector, commentCollector);
		checkYamlCommentParserRows(rowCollector.getList());
		checkYamlComments(commentCollector.getList());


		rowCollector = new ListCollector<String[]>();
		commentCollector = new ListCollector<String>();


		dsl.forEach(createTempCsv(data), rowCollector, commentCollector);
		checkYamlCommentParserRows(rowCollector.getList());
		checkYamlComments(commentCollector.getList());

		rowCollector = new ListCollector<String[]>();
		commentCollector = new ListCollector<String>();


		dsl.forEach(new StringReader(data), rowCollector, commentCollector);
		checkYamlCommentParserRows(rowCollector.getList());
		checkYamlComments(commentCollector.getList());

		rowCollector = new ListCollector<String[]>();
		commentCollector = new ListCollector<String>();

		dsl.forEach(new StringBuilder(data), rowCollector, commentCollector);
		checkYamlCommentParserRows(rowCollector.getList());
		checkYamlComments(commentCollector.getList());

	}

	@Test
	public void testYamlCommentParserTrim() throws IOException {
		String data = "  test , \" hello\"\n# this a comment, not data\none more";

		CsvParser.DSLYamlComment dsl = CsvParser
				.dsl()
				.trimSpaces()
				.withYamlComments();

		ListCollector<String[]> rowCollector = new ListCollector<String[]>();
		ListCollector<String> commentCollector = new ListCollector<String>();


		dsl.forEach(data, rowCollector, commentCollector);

		assertArrayEquals(new String[] {"test", " hello"}, rowCollector.getList().get(0));
		assertArrayEquals(new String[] {"one more"}, rowCollector.getList().get(1));
		assertEquals(Arrays.asList("# this a comment, not data"), commentCollector.getList());
	}

	private void checkYamlComments(List<String> comments) {
		assertEquals(1, comments.size());
		assertEquals("# this a comment, not data", comments.get(0));
	}

	private void checkYamlCommentParserRows(List<String[]> rows) {
		assertEquals(2, rows.size());
		assertArrayEquals(new String[] {"test", " \"hello\" "}, rows.get(0));
		assertArrayEquals(new String[] {"one more"}, rows.get(1));
	}

	private void checkYamlCommentParserAsCellRows(List<String[]> rows) {
		assertEquals(3, rows.size());
		assertArrayEquals(new String[] {"test", " \"hello\" "}, rows.get(0));
		assertArrayEquals(new String[] {"# this a comment, not data"}, rows.get(1));
		assertArrayEquals(new String[] {"one more"}, rows.get(2));
	}

	@Test
	public void testWeirdQuote() throws IOException {
		String csv = "ddddd\",ddd,\"dd\", \"dd,dd\"";

		List<String[]> strings = CsvParser.forEach(csv, new ListCollector<String[]>()).getList();

		assertArrayEquals(new String[] { "ddddd\"", "ddd", "dd", " \"dd", "dd\""}, strings.get(0));


		strings = new ArrayList<String[]>();

		for(String[] row : CsvParser.reader(csv)) {
			strings.add(row);
		}

		assertArrayEquals(new String[] { "ddddd\"", "ddd", "dd", " \"dd", "dd\""}, strings.get(0));

	}


	@Test
	public void testWeirdQuoteWithTrim() throws IOException {
		String csv = "  ddddd\",ddd, \"dd,dd\"";

		List<String[]> strings = CsvParser.dsl().trimSpaces().forEach(csv, new ListCollector<String[]>()).getList();

		assertArrayEquals(new String[] { "ddddd\"", "ddd", "dd,dd"}, strings.get(0));


		strings = new ArrayList<String[]>();

		for(String[] row : CsvParser.dsl().trimSpaces().reader(csv)) {
			strings.add(row);
		}

		assertArrayEquals(new String[] { "ddddd\"", "ddd", "dd,dd"}, strings.get(0));

	}


	@Test
	public void testReadMultipleFormatOverrideDefault() throws  Exception {

		String data2 = "date1\n06-19-2016";


		final List<String> strings = new ArrayList<String>();
		CsvParser.forEach(new StringReader(data2), new CheckedConsumer<String[]>() {
			@Override
			public void accept(String[] s) throws Exception {
				strings.add(s[0]);
			}
		});

		assertArrayEquals(new String[] { "date1",  "06-19-2016" }, strings.toArray(new String[0]));

	}
	@Test
	public void testReadMultipleFormatOverrideDefault2() throws  Exception {

		String data2 = "date1\n06-19-2016";


		final List<String> strings = new ArrayList<String>();
		CsvParser.bufferSize(8).forEach(new StringReader(data2), new CheckedConsumer<String[]>() {
			@Override
			public void accept(String[] s) throws Exception {
				strings.add(s[0]);
			}
		});

		assertArrayEquals(new String[] { "date1",  "06-19-2016" }, strings.toArray(new String[0]));

	}
	
	
	@Test
	public void testOptMapRowIterator() throws Exception {
		Iterator<Row> rowIterator = CsvParser.dsl().rowIterator(ROW_DATA);
		validatorRows(rowIterator);
	}

	@Test
	public void testOptMapRowIteratorWithSkip() throws Exception {
		Iterator<Row> rowIterator = CsvParser.dsl().skip(1).rowIterator("abb,bbb\n" + ROW_DATA);
		validatorRows(rowIterator);
	}
	
	@Test
	public void testLotsOfHeaders() throws  Exception {
		StringBuilder sb = new StringBuilder();
		for(char c = 'a'; c <= 'z'; c++) {
			if (c != 'a') sb.append(",");
			sb.append(c);
		}

		Iterator<Row> rows = CsvParser.dsl().rowIterator(sb.toString() + "\n" + sb.toString());
		
		assertTrue(rows.hasNext());
		
		Row r = rows.next();

		for(char c = 'a'; c <= 'z'; c++) {
			String k = "" + c;
			assertEquals(k, r.get(k));
		}
		assertFalse(rows.hasNext());

	}

	private void validatorRows(Iterator<Row> rowIterator) {
		assertTrue(rowIterator.hasNext());

		Row r = rowIterator.next();

		assertEquals(2, r.size());
		assertEquals("v1", r.get("h1"));
		assertEquals("v2", r.get("h2"));
		assertEquals(null, r.get(3));
		assertEquals(null, r.get("h4"));
		assertFalse(r.isEmpty());
		assertEquals(new HashSet<String>(Arrays.asList("h1", "h2")), r.keySet());
		assertEquals(Arrays.asList("v1", "v2"), r.values());
		
		
		try {
			r.clear();
			fail();
		} catch (UnsupportedOperationException e) {
		}

		try {
			r.remove(null);
			fail();
		} catch (UnsupportedOperationException e) {
		}
		try {
			r.putAll(null);
			fail();
		} catch (UnsupportedOperationException e) {
		}

		try {
			r.put(null, null);
			fail();
		} catch (UnsupportedOperationException e) {
		}
		Map<String, String> expected = new HashMap<String, String>();
		expected.put("h1", "v1");
		expected.put("h2", "v2");
		
		for(Map.Entry<String, String> e : r.entrySet()) {
			assertEquals(expected.get(e.getKey()), e.getValue());
			expected.remove(e.getKey());
		}
		assertTrue(expected.isEmpty());

		assertTrue(r.containsKey("h1"));
		assertFalse(r.containsKey("h4"));
		assertFalse(r.containsKey(3));

		assertTrue(r.containsValue("v1"));
		assertFalse(r.containsValue("v4"));


		assertTrue(rowIterator.hasNext());
		
		

		r = rowIterator.next();

		assertEquals(2, r.size());
		assertEquals("v3", r.get("h1"));
		assertEquals(null, r.get("h2"));
		assertFalse(r.isEmpty());
		assertEquals(new HashSet<String>(Arrays.asList("h1", "h2")), r.keySet());
		assertEquals(Arrays.asList("v3", null), r.values());
		

		assertTrue(rowIterator.hasNext());

		r = rowIterator.next();

		assertEquals(2, r.size());
		assertEquals("v4", r.get("h1"));
		assertEquals("v5", r.get("h2"));
		assertFalse(r.isEmpty());
		assertEquals(new HashSet<String>(Arrays.asList("h1", "h2")), r.keySet());
		assertEquals(Arrays.asList("v4", "v5"), r.values());

		assertFalse(rowIterator.hasNext());
	}

	//IFJAVA8_START
	@Test
	public void testOptMapRowStream() throws Exception {
		Iterator<Row> rowIterator = CsvParser.dsl().rowStream(ROW_DATA).collect(Collectors.toList()).iterator();

		validatorRows(rowIterator);
	}
	//IFJAVA8_END
	
	
}
