package org.sfm.csv.impl.cellreader;

import org.junit.Test;
import org.sfm.csv.impl.ParsingException;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class ShortCellValueReaderTest {

	ShortCellValueReaderImpl reader = new ShortCellValueReaderImpl();
	@Test
	public void testReadShort() throws UnsupportedEncodingException {
		testReadShort(0);
		testReadShort(12345);
		testReadShort(-12345);
		testReadShort(Short.MIN_VALUE);
		testReadShort(Short.MAX_VALUE);
	}
	
	@Test
	public void testInvalidShort() throws UnsupportedEncodingException {
		try {
			reader.read("Nan", null);
			fail("Expect exception");
		} catch(ParsingException e){
			// expected
		}
	}

	@Test
	public void testReadEmptyStringReturnNull() {
		assertNull(reader.read("", null));
	}

	private void testReadShort(int i) throws UnsupportedEncodingException {
		assertEquals(i, reader.read(Integer.toString(i), null).shortValue());
	}

}
