package org.simpleflatmapper.csv.test.impl.cellreader;

import org.junit.Test;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.csv.CsvRow;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;

public class BooleanCellValueReaderTest {

	CsvRow reader = new CsvRow(new CsvColumnKey[1], 1);
	@Test
	public void testRead() throws UnsupportedEncodingException {
		testReadBoolean(null, "");
		testReadBoolean(false, "False");
		testReadBoolean(false, "0");
		testReadBoolean(false, "N");
		testReadBoolean(false, "n");
		testReadBoolean(false, "f");
		testReadBoolean(false, "No");
		testReadBoolean(false, "nO");
		testReadBoolean(true, "else");
		testReadBoolean(true, "1");
	}


	private void testReadBoolean(Boolean expected, String str) throws UnsupportedEncodingException {
		final char[] chars = ("_" + str+ "_").toCharArray();
		reader.reset();
		reader.addValue(chars, 1, chars.length - 2);
		assertEquals(expected, reader.getBoxedBoolean(0));
	}



}
