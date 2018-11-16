package org.simpleflatmapper.csv.test;

import org.junit.Test;
import org.simpleflatmapper.csv.CsvParser;
import org.simpleflatmapper.lightningcsv.parser.CellConsumer;
import org.simpleflatmapper.util.ListCollector;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;

public class CsvParserRandomDataTest {
	private Random random = new Random();


	@Test
	public void testParse() throws IOException {

		for(int i = 0; i < 16; i++) {
			System.out.println("i = " + i);
			CsvParser.DSL dsl = CsvParser.dsl();
			TestData testData = createTestData();

			testParse(dsl, testData);
			testParse(dsl.disableSpecialisedCharConsumer(), testData);

			testData.carriageReturn = "\r";
			testParse(dsl, testData);
			testParse(dsl.disableSpecialisedCharConsumer(), testData);

			testData.carriageReturn = "\n";
			testParse(dsl, testData);
			testParse(dsl.disableSpecialisedCharConsumer(), testData);

			testData.separator = '|';
			testParse(dsl.separator(testData.separator), testData);
			testParse(dsl.separator(testData.separator).disableSpecialisedCharConsumer(), testData);

		}

	}

	private void testParse(CsvParser.DSL dsl, TestData testData) throws IOException {
		testDsl(testData, dsl.bufferSize(1));
		testDsl(testData, dsl.bufferSize(4));
		testDsl(testData, dsl.bufferSize(1).trimSpaces());
		testDsl(testData, dsl.bufferSize(4).trimSpaces());
		testDsl(testData, dsl);
		testDsl(testData, dsl.trimSpaces());
		testDsl(testData, dsl.parallelReader());
	}

	private TestData createTestData() {

		int nbRows = random.nextInt(32) + 10;

		String[][] expectations = new String[nbRows][];
		for(int i = 0; i < nbRows; i++) {
			int nbCols = random.nextInt(16) + 1;
			String[] row = new String[nbCols];
			for(int j = 0; j < row.length; j++) {
				row[j] = newString();
			}
			expectations[i] = row;
		}

		return new TestData(expectations);
	}

	private String newString() {
		int size = random.nextInt(128);

		StringBuilder sb = new StringBuilder(size);
		for(int i = 0; i < size; i++) {
			sb.append(nextChar());
		}
		return sb.toString();
	}

	String availables = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ01234567890!@£$%^&*()_+,./' \r\n\"|";
	private char nextChar() {
		return availables.charAt(random.nextInt(availables.length()));
	}

	private void testDsl(TestData testData, CsvParser.DSL dsl) throws IOException {

		char[] chars = toCSV(testData).toString().toCharArray();
		// reader call
		testParseAll(testData, dsl, chars);

		testSkipThenParseAll(testData, dsl, chars);

		testSkipThenParseRows(testData, dsl, chars);

		testSkipThenParseRow(testData, dsl, chars);

		// schema call
		testIterator(testData, dsl, chars);

		testSkipAndIterator(testData, dsl, chars);

		testReadRows(testData, dsl, chars);

		testReadRowsWithLimit(testData, dsl, chars);

		testParse(testData, dsl, chars);

		testParseWithLimit(testData, dsl, chars);
	}

	private void testParse(TestData testData, CsvParser.DSL dsl, char[] chars) throws IOException {
		String[][] rows =
				dsl.parse(createReader(chars), new AccumulateCellConsumer()).allValues();

		assertArrayEquals(testData.expectations, rows);
	}



	private void testParseWithLimit(TestData testData, CsvParser.DSL dsl, char[] chars) throws IOException {

		String[][] rows =
				dsl.limit(1).parse(createReader(chars), new AccumulateCellConsumer()).allValues();

		assertArrayEquals(toSubArray(testData.expectations, 0, 1), rows);
	}


	private void testReadRows(TestData testData, CsvParser.DSL dsl, char[] chars) throws IOException {
		List<String[]> rows =
				dsl.reader(createReader(chars)).read(new ListCollector<String[]>()).getList();

		assertArrayEquals(testData.expectations, rows.toArray(new String[0][]));
	}



	private void testReadRowsWithLimit(TestData testData, CsvParser.DSL dsl, char[] chars) throws IOException {
		List<String[]> rows =
				dsl.reader(createReader(chars)).read(new ListCollector<String[]>(), 1).getList();

		assertArrayEquals(toSubArray(testData.expectations, 0, 1), rows.toArray(new String[0][]));
	}

	private void testIterator(TestData testData, CsvParser.DSL dsl, char[] chars) throws IOException {

		List<String[]> rows = new ArrayList<String[]>();
		for(String[] row : dsl.reader(createReader(chars))) {
			rows.add(row);
		}

		assertArrayEquals(testData.expectations, rows.toArray(new String[0][]));
	}

	private void testSkipAndIterator(TestData testData, CsvParser.DSL dsl, char[] chars) throws IOException {

		List<String[]> rows = new ArrayList<String[]>();
		for(String[] row : dsl.skip(1).reader(createReader(chars))) {
			rows.add(row);
		}

		assertArrayEquals(toSubArray(testData.expectations, 1), rows.toArray(new String[0][]));
	}

	private void testSkipThenParseRow(TestData testData, CsvParser.DSL dsl, char[] chars) throws IOException {
		AccumulateCellConsumer cellConsumer = new AccumulateCellConsumer();
		dsl.skip(1).reader(createReader(chars)).parseRow(cellConsumer);

		assertArrayEquals(toSubArray(testData.expectations, 1, 1), cellConsumer.allValues());
	}

	private void testSkipThenParseRows(TestData testData, CsvParser.DSL dsl, char[] chars) throws IOException {
		String[][] cells;
		cells = dsl.skip(1).reader(createReader(chars)).parseRows(new AccumulateCellConsumer(), 2).allValues();

		assertArrayEquals(toSubArray(testData.expectations, 1, 2), cells);
	}

	private void testSkipThenParseAll(TestData testData, CsvParser.DSL dsl, char[] chars) throws IOException {
		String[][] cells;
		cells = dsl.skip(1).reader(createReader(chars)).parseAll(new AccumulateCellConsumer()).allValues();

		assertArrayEquals(toSubArray(testData.expectations, 1, testData.expectations.length - 1), cells);
	}

	private String[][] toSubArray(String[][] expectations, int fromIndex) {
		return toSubArray(expectations, fromIndex, expectations.length - fromIndex);
	}
	private String[][] toSubArray(String[][] expectations, int fromIndex, int length) {
		return Arrays.asList(expectations).subList(fromIndex, fromIndex + length).toArray(new String[0][]);
	}

	private void testParseAll(TestData testData, CsvParser.DSL dsl, char[] chars) throws IOException {
		String[][] cells;
		cells =
				dsl.reader(createReader(chars)).parseAll(new AccumulateCellConsumer()).allValues();
		assertArrayEquals(testData.expectations, cells);
	}

	private Reader createReader(char[] chars) {
		return new CharArrayReader(chars);
	}

	private CharSequence toCSV(TestData testData) {
		String[][] cells = testData.expectations;
		char separator = testData.separator;
		char quoteChar = testData.quoteChar;
		String carriageReturn = testData.carriageReturn;

		StringBuilder sb = new StringBuilder();

		for(int rowIndex = 0; rowIndex < cells.length; rowIndex++) {
			String[] row = cells[rowIndex];

			for (int colIndex = 0; colIndex < row.length; colIndex++) {
				String cell = row[colIndex];
				if (colIndex > 0) {
					sb.append(separator);
				}
				if (needEscape(cell, testData)) {
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

	private boolean needEscape(String cell, TestData testData) {
		for(int i = 0; i < cell.length(); i++) {
			char c = cell.charAt(i);
			if (c == '\r' || c == '\n' || c == ' ') {
				return true;
			}
			if (testData.separator == c || testData.quoteChar == c) {
				return true;
			}

		}
		return false;
	}

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


	static class TestData {
		String[][] expectations;

		char quoteChar = '"';
		char separator = ',';
		String carriageReturn = "\r\n";

		public TestData(String[][] expectations) {
			this.expectations = expectations;
		}
	}



}
