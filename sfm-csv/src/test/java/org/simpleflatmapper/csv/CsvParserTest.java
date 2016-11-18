package org.simpleflatmapper.csv;

import org.junit.Test;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.csv.parser.BufferOverflowException;
import org.simpleflatmapper.csv.parser.CellConsumer;
import org.simpleflatmapper.tuple.Tuple2;
import org.simpleflatmapper.tuple.Tuple3;
import org.simpleflatmapper.tuple.Tuple4;
import org.simpleflatmapper.tuple.Tuple5;
import org.simpleflatmapper.tuple.Tuple6;
import org.simpleflatmapper.tuple.Tuple7;
import org.simpleflatmapper.tuple.Tuple8;
import org.simpleflatmapper.tuple.Tuples;
import org.simpleflatmapper.util.TypeReference;
import org.simpleflatmapper.util.CloseableIterator;
import org.simpleflatmapper.util.ListCollector;
import org.simpleflatmapper.util.Predicate;

import java.io.CharArrayReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
//IFJAVA8_START
import java.util.stream.Collectors;
import java.util.stream.Stream;
//IFJAVA8_END
import static org.junit.Assert.*;

public class CsvParserTest {


	private static String[][] SAMPLE_CSV_MIX_EXPECTATION =
			{
					{"cell1", "cell2", ""},
					{"cell\r\"value\"", "val2"},
					{"val3"},
					{"val4", ""}
			};
	@Test
	public void testReadCsvReaderLF() throws IOException {
		testCsvReader(SAMPLE_CSV_MIX_EXPECTATION, ',', '"', "\n");
	}


	@Test
	public void testReadCsvReaderLFSINGLEQUOTE() throws IOException {
		testCsvReader(SAMPLE_CSV_MIX_EXPECTATION, ',', '\'', "\n");
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
    public void testReadCsvReaderOneChar() throws IOException {
        Iterator<String[]> iterator = CsvParser.iterator(new StringReader("0"));
        String[] strs = iterator.next();
        assertEquals("0", strs[0]);
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

	@Test
	public void testDSLWithMapper() throws IOException {
		Iterator<Tuple2<String, String>> iterator =  CsvParser.<Tuple2<String, String>>mapTo(Tuples.typeDef(String.class, String.class)).iterator(new StringReader("val0,val1\nvalue1,value2"));

		assertTrue(iterator.hasNext());
		Tuple2<String, String> tuple2 = iterator.next();
		assertEquals("value1", tuple2.first());
		assertEquals("value2", tuple2.second());
		assertFalse(iterator.hasNext());

		//assertEquals("value", CsvParser.mapTo(String.class).iterator(new StringReader("val\nvalue")).current());
	}

	@Test
	public void testDSLMapWith() throws IOException {
		CsvMapper<Tuple2<String, String>> mapper = CsvMapperFactory.newInstance().newMapper(Tuples.typeDef(String.class, String.class));
		Iterator<Tuple2<String, String>> iterator =  CsvParser.<Tuple2<String, String>>mapWith(mapper).iterator(new StringReader("val0,val1\nvalue1,value2"));

		assertTrue(iterator.hasNext());
		Tuple2<String, String> tuple2 = iterator.next();
		assertEquals("value1", tuple2.first());
		assertEquals("value2", tuple2.second());
		assertFalse(iterator.hasNext());
	}

	@Test
	public void testDSLWitStaticMapper() throws IOException {
		Iterator<Tuple2<String, String>> iterator =  CsvParser.<Tuple2<String, String>>mapTo(Tuples.typeDef(String.class, String.class)).headers("0", "1").iterator(new StringReader("value1,value2"));

		assertTrue(iterator.hasNext());
		Tuple2<String, String> tuple2 = iterator.next();
		assertEquals("value1", tuple2.first());
		assertEquals("value2", tuple2.second());
		assertFalse(iterator.hasNext());
	}

	@Test
	public void testDSLMapToString() throws IOException {
		Iterator<String> iterator = CsvParser.mapTo(String.class).headers("val").iterator(new StringReader("value1,value2"));
		assertTrue(iterator.hasNext());
		String tuple2 = iterator.next();
		assertEquals("value1", tuple2);
		assertFalse(iterator.hasNext());
	}

	@Test
	public void testDSLMapToLong() throws IOException {
		Iterator<Long> iterator = CsvParser.mapTo(Long.class).headers("val").iterator(new StringReader("123,value2"));
		assertTrue(iterator.hasNext());
		Long tuple2 = iterator.next();
		assertEquals(123l, tuple2.longValue());
		assertFalse(iterator.hasNext());
	}

	@Test
	public void testDSLMapToTuple2() throws IOException {
		Iterator<Tuple2<String, String>> iterator = CsvParser.mapTo(String.class, String.class).headers("0", "1").iterator(new StringReader("value1,value2"));
		assertTrue(iterator.hasNext());
		Tuple2<String, String> tuple2 = iterator.next();
		assertEquals("value1", tuple2.first());
		assertEquals("value2", tuple2.second());
		assertFalse(iterator.hasNext());
	}

	@Test
	public void testDSLMapToTuple2WithDefaultHeader() throws IOException {
		Iterator<Tuple2<String, String>> iterator = CsvParser.mapTo(String.class, String.class).defaultHeaders().iterator(new StringReader("value1,value2"));
		assertTrue(iterator.hasNext());
		Tuple2<String, String> tuple2 = iterator.next();
		assertEquals("value1", tuple2.first());
		assertEquals("value2", tuple2.second());
		assertFalse(iterator.hasNext());
	}


    @Test
    public void testDSLMapToTypeReference() throws IOException {
        Iterator<Tuple2<String, String>> iterator = CsvParser.mapTo(new TypeReference<Tuple2<String, String>>() {}).defaultHeaders().iterator(new StringReader("value1,value2"));
        assertTrue(iterator.hasNext());
        Tuple2<String, String> tuple2 = iterator.next();
        assertEquals("value1", tuple2.first());
        assertEquals("value2", tuple2.second());
        assertFalse(iterator.hasNext());
    }
	@Test
	public void testDSLMapToTuple2OverrideWithDefaultHeader() throws IOException {
		Iterator<Tuple2<String, String>> iterator = CsvParser.mapTo(String.class, String.class).overrideWithDefaultHeaders().iterator(new StringReader("key,value\nvalue1,value2"));
		assertTrue(iterator.hasNext());
		Tuple2<String, String> tuple2 = iterator.next();
		assertEquals("value1", tuple2.first());
		assertEquals("value2", tuple2.second());
		assertFalse(iterator.hasNext());
	}

	@Test
	public void testDSLMapToTuple3() throws IOException {
		Iterator<Tuple3<String, String, String>> iterator = CsvParser.mapTo(String.class, String.class, String.class)
                .defaultHeaders().iterator(new StringReader("value1,value2,value3"));
		assertTrue(iterator.hasNext());
		Tuple3<String, String, String> tuple2 = iterator.next();
		assertEquals("value1", tuple2.first());
		assertEquals("value2", tuple2.second());
		assertEquals("value3", tuple2.third());
		assertFalse(iterator.hasNext());
	}

	@Test
	public void testDSLMapToTuple4() throws IOException {
		Iterator<Tuple4<String, String, String, String>> iterator =
				CsvParser.mapTo(String.class, String.class, String.class, String.class)
                        .defaultHeaders().iterator(new StringReader("value1,value2,value3,value4"));
		assertTrue(iterator.hasNext());
		Tuple4<String, String, String, String> tuple2 = iterator.next();
		assertEquals("value1", tuple2.first());
		assertEquals("value2", tuple2.second());
		assertEquals("value3", tuple2.third());
		assertEquals("value4", tuple2.fourth());
		assertFalse(iterator.hasNext());
	}

	@Test
	public void testDSLMapToTuple5() throws IOException {
		Iterator<Tuple5<String, String, String, String, String>> iterator =
				CsvParser.mapTo(String.class, String.class, String.class, String.class, String.class)
						.defaultHeaders().iterator(new StringReader("value1,value2,value3,value4,value5"));
		assertTrue(iterator.hasNext());
		Tuple5<String, String, String, String, String> tuple2 = iterator.next();
		assertEquals("value1", tuple2.first());
		assertEquals("value2", tuple2.second());
		assertEquals("value3", tuple2.third());
		assertEquals("value4", tuple2.fourth());
		assertEquals("value5", tuple2.fifth());
		assertFalse(iterator.hasNext());
	}

    @Test
    public void testDSLMapToTuple6() throws IOException {
        Tuple6<String, String, String, String, String, String> tuple6 = CsvParser.mapTo(String.class, String.class, String.class,
                String.class, String.class, String.class)
                .defaultHeaders().iterator(new StringReader("value1,value2,value3,value4,value5,value6")).next();
        assertEquals("value1", tuple6.first());
        assertEquals("value2", tuple6.second());
        assertEquals("value3", tuple6.third());
        assertEquals("value4", tuple6.fourth());
        assertEquals("value5", tuple6.fifth());
        assertEquals("value6", tuple6.sixth());
    }

    @Test
    public void testDSLMapToTuple7() throws IOException {
        Tuple7<String, String, String, String, String, String, String> tuple = CsvParser.mapTo(String.class, String.class, String.class,
                String.class, String.class, String.class,
                String.class)
                .defaultHeaders().iterator(new StringReader("value1,value2,value3,value4,value5,value6,value7")).next();
        assertEquals("value1", tuple.first());
        assertEquals("value2", tuple.second());
        assertEquals("value3", tuple.third());
        assertEquals("value4", tuple.fourth());
        assertEquals("value5", tuple.fifth());
        assertEquals("value6", tuple.sixth());
        assertEquals("value7", tuple.seventh());
    }

    @Test
    public void testDSLMapToTuple8() throws IOException {
        Tuple8<String, String, String, String, String, String, String, String> tuple = CsvParser.mapTo(String.class, String.class, String.class,
                String.class, String.class, String.class,
                String.class, String.class)
                .defaultHeaders().iterator(new StringReader("value1,value2,value3,value4,value5,value6,value7,value8")).next();
        assertEquals("value1", tuple.first());
        assertEquals("value2", tuple.second());
        assertEquals("value3", tuple.third());
        assertEquals("value4", tuple.fourth());
        assertEquals("value5", tuple.fifth());
        assertEquals("value6", tuple.sixth());
        assertEquals("value7", tuple.seventh());
        assertEquals("value8", tuple.eighth());
    }

    @Test
    public void testDSLMapToForEach() throws IOException {
        List<Tuple2<String, String>> list = CsvParser.mapTo(String.class, String.class)
                .headers("0", "1").forEach(new StringReader("value1,value2\nvalue3"), new ListCollector<Tuple2<String, String>>()).getList();

        assertArrayEquals(new Object[] { new Tuple2<String, String>("value1", "value2"), new Tuple2<String, String>("value3", null)}, list.toArray());
    }

	@Test
	public void testDSLMapToForEachWithLimit() throws IOException {
		List<Tuple2<String, String>> list = CsvParser.limit(1).mapTo(String.class, String.class)
				.headers("0", "1").forEach(new StringReader("value1,value2\nvalue3"), new ListCollector<Tuple2<String, String>>()).getList();

		assertArrayEquals(new Object[] { new Tuple2<String, String>("value1", "value2")}, list.toArray());
	}
	@Test
	public void testDSLMapToForEachFromFile() throws IOException {
		List<Tuple2<String, String>> list = CsvParser.mapTo(String.class, String.class)
				.headers("0", "1").forEach(createTempCsv("value1,value2\n" +
						"value3"), new ListCollector<Tuple2<String, String>>()).getList();

		assertArrayEquals(new Object[] { new Tuple2<String, String>("value1", "value2"), new Tuple2<String, String>("value3", null)}, list.toArray());
	}
	@Test
	public void testDSLMapToForEachFromString() throws IOException {
		List<Tuple2<String, String>> list = CsvParser.mapTo(String.class, String.class)
				.headers("0", "1").forEach("value1,value2\n" +
						"value3", new ListCollector<Tuple2<String, String>>()).getList();

		assertArrayEquals(new Object[] { new Tuple2<String, String>("value1", "value2"), new Tuple2<String, String>("value3", null)}, list.toArray());
	}
	//IFJAVA8_START

	@Test
	public void testDSLMapToStream() throws IOException {
		List<Tuple2<String, String>> list = CsvParser.mapTo(String.class, String.class)
				.headers("0", "1").stream(new StringReader("value1,value2\nvalue3")).collect(Collectors.toList());

		assertArrayEquals(new Object[] { new Tuple2<String, String>("value1", "value2"), new Tuple2<String, String>("value3", null)}, list.toArray());
	}

	@Test
	public void testDSLMapToStreamFromFile() throws IOException {
		final Stream<Tuple2<String, String>> stream = CsvParser.mapTo(String.class, String.class)
				.headers("0", "1").stream(createTempCsv("value1,value2\nvalue3"));
		List<Tuple2<String, String>> list = stream.collect(Collectors.toList());

		stream.close();
		assertArrayEquals(new Object[] { new Tuple2<String, String>("value1", "value2"), new Tuple2<String, String>("value3", null)}, list.toArray());
	}

	@Test
	public void testDSLMapToStreamFromFileFunction() throws IOException {
		List<Tuple2<String, String>> list = CsvParser.mapTo(String.class, String.class)
				.headers("0", "1").stream(createTempCsv("value1,value2\nvalue3"), (s) -> s.collect(Collectors.toList()) );

		assertArrayEquals(new Object[] { new Tuple2<String, String>("value1", "value2"), new Tuple2<String, String>("value3", null)}, list.toArray());
	}


	@Test
	public void testDSLMapToStreamFromString() throws IOException {
		final Stream<Tuple2<String, String>> stream = CsvParser.mapTo(String.class, String.class)
				.headers("0", "1").stream("value1,value2\nvalue3");
		List<Tuple2<String, String>> list = stream.collect(Collectors.toList());

		stream.close();
		assertArrayEquals(new Object[] { new Tuple2<String, String>("value1", "value2"), new Tuple2<String, String>("value3", null)}, list.toArray());
	}
	//IFJAVA8_END


	@Test
	public void testDSLMapWithCustomDefinition() throws  Exception {
		Iterator<Tuple2<String, String>> iterator = CsvParser.mapTo(String.class, String.class).columnDefinition("1", CsvColumnDefinition.customReaderDefinition(new CellValueReader<String>() {
			@Override
			public String read(char[] chars, int offset, int length, ParsingContext parsingContext) {
				return "c1";
			}
		})).iterator(new StringReader("0,1\nv0,v1"));

		Tuple2<String, String> tuple = iterator.next();

		assertEquals("v0", tuple.first());
		assertEquals("c1", tuple.second());
	}

	@Test
	public void testDSLMapWithCustomDefinitionOnStaticMapper() throws  Exception {
		Iterator<Tuple2<String, String>> iterator = CsvParser.mapTo(String.class, String.class)
				.addMapping("0")
				.addMapping("1", CsvColumnDefinition.customReaderDefinition(new CellValueReader<String>() {
					@Override
					public String read(char[] chars, int offset, int length, ParsingContext parsingContext) {
						return "c1";
					}
				})).iterator(new StringReader("0,1\nv0,v1"));

		Tuple2<String, String> tuple = iterator.next();

		assertEquals("v0", tuple.first());
		assertEquals("c1", tuple.second());
	}

    @Test
    public void testDSLIgnoreField() throws Exception {
        Iterator<Tuple2<String, String>> iterator = CsvParser.mapTo(String.class, String.class)
                .columnDefinition(new Predicate<CsvColumnKey>() {
                    @Override
                    public boolean test(CsvColumnKey csvColumnKey) {
                        return csvColumnKey.getIndex() != 1 && csvColumnKey.getIndex() != 2;
                    }
                }, CsvColumnDefinition.ignoreDefinition())

                .iterator(new StringReader("-1,0,1,2\nv0,v1,v2,v3"));

        Tuple2<String, String> tuple = iterator.next();

        assertEquals("v1", tuple.first());
        assertEquals("v2", tuple.second());
    }

	private Reader getOneRowReader() {
		return new StringReader("value");
	}

	private void testCsvReader(String[][] expectations, char separator, char quote, String cr) throws IOException {
		CsvParser.DSL dsl = CsvParser
				.bufferSize(4)
				.separator(separator)
				.quote(quote);

		CsvParser.DSL dslTrim = dsl.trimSpaces();

		testDsl(expectations, separator, quote, cr, dsl);
		testDsl(expectations, separator, quote, cr, dslTrim);


	}

	private void testDsl(String[][] expectations, char separator, char quote, String cr, CsvParser.DSL dsl) throws IOException {
		// reader call
		testParseAll(expectations, separator, quote, cr, dsl);

		testSkipThenParseAll(expectations, separator, quote, cr, dsl);

		testSkipThenParseRows(expectations, separator, quote, cr, dsl);

		testSkipThenParseRow(expectations, separator, quote, cr, dsl);

		// schema call
		testIterator(expectations, separator, quote, cr, dsl);

		testSkipAndIterator(expectations, separator, quote, cr, dsl);

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
				dsl.reader(createReader(expectations, separator, quote, cr)).read(new ListCollector<String[]>()).getList();

		assertArrayEquals(expectations, rows.toArray(new String[0][]));
	}



	private void testReadRowsWithLimit(String[][] expectations, char separator, char quote, String cr, CsvParser.DSL dsl) throws IOException {
		List<String[]> rows =
				dsl.reader(createReader(expectations, separator, quote, cr)).read(new ListCollector<String[]>(), 1).getList();

		assertArrayEquals(toSubArray(expectations, 0, 1), rows.toArray(new String[0][]));
	}

	private void testIterator(String[][] expectations, char separator, char quote, String cr, CsvParser.DSL dsl) throws IOException {

		List<String[]> rows = new ArrayList<String[]>();
		for(String[] row : dsl.reader(createReader(expectations, separator, quote, cr))) {
			rows.add(row);
		}

		assertArrayEquals(expectations, rows.toArray(new String[0][]));
	}

	private void testSkipAndIterator(String[][] expectations, char separator, char quote, String cr, CsvParser.DSL dsl) throws IOException {

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
		cells = dsl
				.skip(1)
				.reader(createReader(expectations, separator, quote, cr))
				.parseRows(new AccumulateCellConsumer(), 2)
				.allValues();

		assertArrayEquals(toSubArray(expectations, 1, 2), cells);
	}

	private void testSkipThenParseAll(String[][] expectations, char separator, char quote, String cr, CsvParser.DSL dsl) throws IOException {
		String[][] cells;
		cells =
				dsl
				.skip(1)
				.reader(createReader(expectations, separator, quote, cr))
				.parseAll(new AccumulateCellConsumer()).allValues();

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
				if (needEscape(cell, quoteChar, separator)) {
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
	public void testIssue84() throws IOException {
		String str = "my_field,second_field\n" +
				",,";

		Iterator<MyScalaClass> iterator = CsvParser.mapTo(MyScalaClass.class).iterator(new StringReader(str));

		while(iterator.hasNext()) {
			System.out.println(iterator.next());
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
		String str = "f1,long field";

		Iterator<String[]> iterator = CsvParser.maxBufferSize(4).bufferSize(2).iterator(new StringReader(str));
		try {
			iterator.next();
			fail("Expect BufferOverflowException");
		} catch (Exception e) {
			if (!(e instanceof BufferOverflowException)) {
				fail("Expect BufferOverflowException");
			}
			// expected
		}

		iterator = CsvParser.maxBufferSize(9).bufferSize(2).iterator(new StringReader(str));
		try {
			iterator.next();
			fail("Expect BufferOverflowException");
		} catch (Exception e) {
			// expected
			if (!(e instanceof BufferOverflowException)) {
				fail("Expect BufferOverflowException");
			}
		}

		iterator = CsvParser.maxBufferSize(11).bufferSize(2).iterator(new StringReader(str));

		String[] row = iterator.next();
		assertEquals("f1", row[0]);
		assertEquals("long field", row[1]);

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

	@Test
	public void testIterateObjectFromFile() throws IOException {
		File file = createTempCsv("value\n1");

		CloseableIterator<Long> iterator = CsvParser.mapTo(Long.class).iterator(file);
		try {
			assertEquals(1l, iterator.next().longValue());
		} finally {
			iterator.close();
		}
	}

	@Test
	public void testIterateObjectFromString() throws IOException {
		Iterator<Long> iterator = CsvParser.mapTo(Long.class).iterator("value\n1");
		assertEquals(1l, iterator.next().longValue());
		iterator = CsvParser.mapTo(Long.class).iterator(new StringBuilder("value\n1"));
		assertEquals(1l, iterator.next().longValue());
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
		String data = "test,\" \"\"hello\"\" \"\n# this a comment, not data\none more";


		CsvParser.DSLYamlComment dsl = CsvParser
				.dsl()
				.withYamlComments();

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

	@Test
	public void testYamlCommentMapper() throws IOException  {
		String data = "# comment who cares\n" +
				"id,name\n" +
				"# what's up wih all the comments\n" +
				"1,\"n\"\n" +
				"# if you need that many comment ....";


		CsvParser.MapToDSL<DbObject> dbObjectMapToDSL = CsvParser
				.dsl()
				.withYamlComments()
				.mapTo(DbObject.class);


		checkYamlCommentMapperResult(dbObjectMapToDSL.forEach(data, new ListCollector<DbObject>()).getList());

		List<DbObject> dbObjects = new ArrayList<DbObject>();
		Iterator<DbObject> iterator = dbObjectMapToDSL.iterator(data);
		while(iterator.hasNext()) {
			dbObjects.add(iterator.next());
		}
		checkYamlCommentMapperResult(dbObjects);

		//IFJAVA8_START
		checkYamlCommentMapperResult(dbObjectMapToDSL.stream(data).collect(Collectors.toList()));
		//IFJAVA8_END

		File csv = createTempCsv(data);

		checkYamlCommentMapperResult(dbObjectMapToDSL.forEach(csv, new ListCollector<DbObject>()).getList());

		dbObjects = new ArrayList<DbObject>();
		CloseableIterator<DbObject> citerator = dbObjectMapToDSL.iterator(csv);
		while(citerator.hasNext()) {
			dbObjects.add(citerator.next());
		}
		checkYamlCommentMapperResult(dbObjects);

		//IFJAVA8_START
		checkYamlCommentMapperResult(dbObjectMapToDSL.stream(csv).collect(Collectors.toList()));
		//IFJAVA8_END


		checkYamlCommentMapperResult(dbObjectMapToDSL.forEach(new StringReader(data), new ListCollector<DbObject>()).getList());

		dbObjects = new ArrayList<DbObject>();
		iterator = dbObjectMapToDSL.iterator(new StringReader(data));
		while(iterator.hasNext()) {
			dbObjects.add(iterator.next());
		}
		checkYamlCommentMapperResult(dbObjects);

		//IFJAVA8_START
		checkYamlCommentMapperResult(dbObjectMapToDSL.stream(new StringReader(data)).collect(Collectors.toList()));
		//IFJAVA8_END


		checkYamlCommentMapperResult(dbObjectMapToDSL.forEach(new StringBuilder(data), new ListCollector<DbObject>()).getList());

		dbObjects = new ArrayList<DbObject>();
		iterator = dbObjectMapToDSL.iterator(new StringBuilder(data));
		while(iterator.hasNext()) {
			dbObjects.add(iterator.next());
		}
		checkYamlCommentMapperResult(dbObjects);

		//IFJAVA8_START
		checkYamlCommentMapperResult(dbObjectMapToDSL.stream(new StringBuilder(data)).collect(Collectors.toList()));
		//IFJAVA8_END

	}

	private void checkYamlCommentMapperResult(List<DbObject> dbObjects) {
		assertEquals(1, dbObjects.size());
		assertEquals(1, dbObjects.get(0).getId());
		assertEquals("n", dbObjects.get(0).getName());
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
}
