package org.simpleflatmapper.csv.test.impl.cellreader;

import org.junit.Test;
import org.simpleflatmapper.csv.impl.cellreader.BooleanCellValueReaderImpl;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class BooleanCellValueReaderTest {

	BooleanCellValueReaderImpl reader = new BooleanCellValueReaderImpl();
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

	@Test
	public void testReadEmptyStringReturnNull() {
		assertNull(reader.read(new char[10], 2, 0, null));
	}

	private void testReadBoolean(Boolean expected, String str) throws UnsupportedEncodingException {
		final char[] chars = ("_" + str+ "_").toCharArray();
		assertEquals(expected, reader.read(chars, 1, chars.length-2, null));
	}



}
