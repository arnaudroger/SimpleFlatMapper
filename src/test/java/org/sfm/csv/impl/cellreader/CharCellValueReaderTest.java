package org.sfm.csv.impl.cellreader;

import org.junit.Test;
import org.sfm.csv.impl.ParsingException;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class CharCellValueReaderTest {

	CharCellValueReaderImpl reader = new CharCellValueReaderImpl();
	@Test
	public void testReadChar() throws UnsupportedEncodingException {
		testReadShort(345);
		testReadShort(Character.MIN_VALUE);
		testReadShort(Character.MAX_VALUE);
	}
	
	@Test
	public void testInvalidChar() throws UnsupportedEncodingException {
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
		assertEquals(i, reader.read(Integer.toString(i), null).charValue());
	}

}
