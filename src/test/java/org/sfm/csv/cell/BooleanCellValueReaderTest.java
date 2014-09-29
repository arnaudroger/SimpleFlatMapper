package org.sfm.csv.cell;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

public class BooleanCellValueReaderTest {

	BooleanCellValueReader reader = new BooleanCellValueReader();
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
		final byte[] bytes = ("_" + str+ "_").getBytes("UTF-8");
		final char[] chars = ("_" + str+ "_").toCharArray();
		assertEquals(expected, reader.read(bytes, 1, bytes.length-2, null).booleanValue());
		assertEquals(expected, reader.read(chars, 1, chars.length-2).booleanValue());
	}

}
