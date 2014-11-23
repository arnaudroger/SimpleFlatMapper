package org.sfm.csv;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.junit.Before;
import org.junit.Test;
import org.sfm.csv.parser.CellConsumer;

public class CsvParserPerfTest {

	
	private static final int NB = 1000000;
	private static final int ITERATION = 10;

	private static final class ValidateConsumer implements CellConsumer {
		long c;
		@Override
		public void newCell( char[] chars, int offset,
				int length) {
			c++;
		}
		@Override
		public void endOfRow() {

		}
		@Override
		public void end() {
		}
	}


	String content;
	byte[] bytes;
	@Before
	public void setUp() {
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < NB; i++) {
			sb.append("cell1,cell2,cell3\n");
		}
		
		content = sb.toString();
		bytes = content.getBytes();
	}
	
	@Test
	public void testReadCsv() throws IOException {
		for(int i = 0; i < ITERATION; i++) {
			executeReader();
		}
	}

	private void executeReader() throws IOException {
		Reader sr = new StringReader(content);
		ValidateConsumer handler = new ValidateConsumer();
		long start = System.nanoTime();
		CsvParser.parse(sr, handler);
		long elapsed = System.nanoTime() - start;
		assertEquals(3 * NB, handler.c);
		System.out.println("Reader Took " + elapsed + "ns " + (elapsed/NB) + " ns per row");
	}

}
