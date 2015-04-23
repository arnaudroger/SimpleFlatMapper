package org.sfm.csv.impl.cellreader;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class DoubleCellValueReaderTest {

	DoubleCellValueReaderImpl reader = new DoubleCellValueReaderImpl();
	@Test
	public void testReadInt() throws UnsupportedEncodingException {
		testReadDouble(0);
		testReadDouble(12345.33);
		testReadDouble(-12345.33);
		testReadDouble(Double.MIN_VALUE);
		testReadDouble(Double.MAX_VALUE);
	}
	
	@Test
	public void testInvalidDouble() throws UnsupportedEncodingException {
		try {
			reader.read("ddd", null);
			fail("Expect exception");
		} catch(NumberFormatException e){
			// expected
		}
	}

	@Test
	public void testReadEmptyStringReturnNull() {
		assertNull(reader.read("", null));
	}

	private void testReadDouble(double i) throws UnsupportedEncodingException {
		assertEquals(i, reader.read(Double.toString(i), null).doubleValue(), 0);
	}

}
