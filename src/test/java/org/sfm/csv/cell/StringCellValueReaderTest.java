package org.sfm.csv.cell;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.junit.Test;
import org.sfm.csv.DecoderContext;

public class StringCellValueReaderTest {

	StringCellValueReader reader = new StringCellValueReader();
	@Test
	public void testReadString() throws UnsupportedEncodingException {
		byte[] bytes = "Hello!".getBytes("UTF-8");
		char[] chars = "Hello!".toCharArray();
		assertEquals("Hello!", reader.read(bytes, 0, bytes.length, DecoderContext.forCharset("UTF-8")));
		assertEquals("Hello!", reader.read(chars, 0, chars.length));

		bytes = "HHH\"Hello!\"HHH".getBytes("UTF-8");
		chars = "HHH\"Hello!\"HHH".toCharArray();
		assertEquals("Hello!", reader.read(bytes, 3, bytes.length - 6, DecoderContext.forCharset("UTF-8")));
		assertEquals("Hello!", reader.read(chars, 3, chars.length - 6));
		
		bytes = "\"Hello!\"\"Sir\"\"\"".getBytes("UTF-8");
		chars = "\"Hello!\"\"Sir\"\"\"".toCharArray();
		assertEquals("Hello!\"Sir\"", reader.read(bytes, 0, bytes.length, DecoderContext.forCharset("UTF-8")));
		assertEquals("Hello!\"Sir\"", reader.read(chars, 0, chars.length));
		
		bytes = "\"Hello!\"\"Sir".getBytes("UTF-8");
		chars = "\"Hello!\"\"Sir".toCharArray();
		assertEquals("Hello!\"Sir", reader.read(bytes, 0, bytes.length, DecoderContext.forCharset("UTF-8")));
		assertEquals("Hello!\"Sir", reader.read(chars, 0, chars.length));
	}
	

}
