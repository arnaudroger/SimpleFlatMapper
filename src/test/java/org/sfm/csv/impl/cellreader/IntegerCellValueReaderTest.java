package org.sfm.csv.impl.cellreader;

import org.junit.Test;
import org.sfm.csv.impl.ParsingException;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class IntegerCellValueReaderTest {

	IntegerCellValueReaderImpl reader = new IntegerCellValueReaderImpl();
	@Test
	public void testReadInt() throws UnsupportedEncodingException {
		testReadInt(0);
		testReadInt(12345);
		testReadInt(-12345);
		testReadInt(Integer.MIN_VALUE);
		testReadInt(Integer.MAX_VALUE);
	}
	
	@Test
	public void testInvalidInt() throws UnsupportedEncodingException {
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

	private void testReadInt(int i) throws UnsupportedEncodingException {
		assertEquals(i, reader.read(Integer.toString(i), null).intValue());
	}

}
