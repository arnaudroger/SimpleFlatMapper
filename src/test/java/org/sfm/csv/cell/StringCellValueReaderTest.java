package org.sfm.csv.cell;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

public class StringCellValueReaderTest {

	StringCellValueReader reader = new StringCellValueReader();
	@Test
	public void testReadString() throws UnsupportedEncodingException {
		char[] chars = "Hello!".toCharArray();
		assertEquals("Hello!", reader.read(chars, 0, chars.length, null));

		chars = "HHH\"Hello!\"HHH".toCharArray();
		assertEquals("Hello!", reader.read(chars, 3, chars.length - 6, null));
		
		chars = "\"Hello!\"\"Sir\"\"\"".toCharArray();
		assertEquals("Hello!\"Sir\"", reader.read(chars, 0, chars.length, null));
		
		chars = "\"Hello!\"\"Sir".toCharArray();
		assertEquals("Hello!\"Sir", reader.read(chars, 0, chars.length, null));
	}
	

}
