package org.sfm.csv.impl.cellreader;

import org.junit.Test;
import org.sfm.csv.impl.ParsingException;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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

	private void testRead(int i) throws UnsupportedEncodingException {
		assertEquals(i, reader.read(Integer.toString(i), null).byteValue());
	}

}
