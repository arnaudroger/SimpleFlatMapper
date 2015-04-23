package org.sfm.csv.impl.cellreader;

import org.junit.Test;

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
		assertNull(reader.read("", null));
	}

	private void testReadBoolean(Boolean expected, String str) throws UnsupportedEncodingException {
		assertEquals(expected, reader.read(str, null));
	}



}
