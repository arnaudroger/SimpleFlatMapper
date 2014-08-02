package org.sfm.text;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Before;
import org.junit.Test;

public class CsvParserPerfTest {

	
	private static final int NB = 1000000;

	private static final class ValidateHandler implements CellHandler {
		long c;
		@Override
		public void cell(long row, long col, char[] chars, int offset, int length) {
			c++;
		}
	}


	String content;
	@Before
	public void setUp() {
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < NB; i++) {
			sb.append("cell1,cell2,cell3\n");
		}
		
		content = sb.toString();
	}
	
	@Test
	public void testReadCsv() throws IOException {
		for(int i = 0; i < 10; i++) {
			execute();
		}
	}

	private void execute() throws IOException {
		StringReader sr = new StringReader(content);
		ValidateHandler handler = new ValidateHandler();
		long start = System.nanoTime();
		new CsvParser().parse(sr, handler);
		long elapsed = System.nanoTime() - start;
		assertEquals(3 * NB, handler.c);
		System.out.println("Took " + elapsed + "ns " + (elapsed/NB) + " ns per row");
	}
}
