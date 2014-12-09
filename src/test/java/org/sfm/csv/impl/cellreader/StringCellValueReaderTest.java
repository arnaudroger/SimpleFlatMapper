package org.sfm.csv.impl.cellreader;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringCellValueReaderTest {

	StringCellValueReader reader = new StringCellValueReader();
	@Test
	public void testReadStringNoEscaping() {
		char[] chars = "Hello!".toCharArray();
		assertEquals("Hello!", reader.read(chars, 0, chars.length, null));

	
	}

}
