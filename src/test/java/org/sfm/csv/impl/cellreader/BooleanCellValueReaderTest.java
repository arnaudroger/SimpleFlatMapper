package org.sfm.csv.impl.cellreader;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;

public class BooleanCellValueReaderTest {

	BooleanCellValueReaderImpl reader = new BooleanCellValueReaderImpl();
	@Test
	public void testRead() throws UnsupportedEncodingException {
		testReadBoolean(false, "");
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
	

	private void testReadBoolean(boolean expected, String str) throws UnsupportedEncodingException {
		final char[] chars = ("_" + str+ "_").toCharArray();
		assertEquals(expected, reader.read(chars, 1, chars.length-2, null).booleanValue());
	}

}
