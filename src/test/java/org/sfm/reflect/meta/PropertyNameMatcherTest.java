package org.sfm.reflect.meta;

import static org.junit.Assert.*;

import org.junit.Test;

public class PropertyNameMatcherTest {

	@Test
	public void testFullMatch() {
		PropertyNameMatcher matcher = new PropertyNameMatcher("my_Col");
		assertTrue(matcher.matches("myCol"));
		assertTrue(matcher.matches("my_Col"));
		assertTrue(matcher.matches("my Col"));
		assertFalse(matcher.matches("myCo"));
		assertFalse(matcher.matches("my__Col"));
		assertFalse(matcher.matches("myCol2"));
	}
	
	@Test
	public void testStartOf() {
		PropertyNameMatcher matcher = new PropertyNameMatcher("my_Col_top_bottom");
		assertTrue(matcher.partialMatch("myCol").partialMatch("top").matches("bottom"));
		assertTrue(matcher.partialMatch("my_Col").partialMatch("tOp").matches("bottom"));
		assertTrue(matcher.partialMatch("my Col").partialMatch("tOp").matches("bottom"));
		assertNull(matcher.partialMatch("my__Col"));
		assertNull(matcher.partialMatch("myCol2"));
	}
	

}
