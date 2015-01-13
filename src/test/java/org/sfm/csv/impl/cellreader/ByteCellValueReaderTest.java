package org.sfm.csv.impl.cellreader;

import org.junit.Test;
import org.sfm.csv.impl.ParsingException;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ByteCellValueReaderTest {

	ByteCellValueReaderImpl reader = new ByteCellValueReaderImpl();
	@Test
	public void testReadByte() throws UnsupportedEncodingException {
		testRead(0);
		testRead(54);
		testRead(-55);
		testRead(Byte.MIN_VALUE);
		testRead(Byte.MAX_VALUE);
	}
	@Test
	public void testInvalid() throws UnsupportedEncodingException {
		final char[] chars = "Nan".toCharArray();
		try {
			reader.read(chars, 0, chars.length, null);
			fail("Expect exception");
		} catch(ParsingException e){
			// expected
		}
	
	}

	private void testRead(int i) throws UnsupportedEncodingException {
		final char[] chars = ("_" + Integer.toString(i) + "_").toCharArray();
		assertEquals(i, reader.read(chars, 1, chars.length-2, null).byteValue());
	}

}
