package org.sfm.csv.impl.cellreader;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringCellValueReaderTest {

	StringCellValueReader reader = new StringCellValueReader();
	@Test
	public void testReadStringNoEscaping() {
		assertEquals("Hello!", reader.read("Hello!", null));
	}

}
