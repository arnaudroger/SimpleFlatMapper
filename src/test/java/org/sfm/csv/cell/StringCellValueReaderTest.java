package org.sfm.csv.cell;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

public class StringCellValueReaderTest {

	StringCellValueReader reader = new StringCellValueReader();
	@Test
	public void testReadString() throws UnsupportedEncodingException {
		byte[] bytes = "Hello!".getBytes("UTF-8");
		assertEquals("Hello!", reader.read(bytes, 0, bytes.length));

		bytes = "HHH\"Hello!\"HHH".getBytes("UTF-8");
		assertEquals("Hello!", reader.read(bytes, 3, bytes.length - 6));

		bytes = "\"Hello!\"\"Sir\"\"\"".getBytes("UTF-8");
		assertEquals("Hello!\"Sir\"", reader.read(bytes, 0, bytes.length));
		bytes = "\"Hello!\"\"Sir".getBytes("UTF-8");
		assertEquals("Hello!\"Sir", reader.read(bytes, 0, bytes.length));
	}
	

}
