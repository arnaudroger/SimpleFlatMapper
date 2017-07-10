package org.simpleflatmapper.reflect.test.meta;

import org.junit.Test;
import org.simpleflatmapper.reflect.meta.DefaultPropertyNameMatcher;
import org.simpleflatmapper.reflect.meta.PropertyNameMatcher;

import static org.junit.Assert.*;

public class DefaultPropertyNameMatcherTest {

	@Test
	public void testFullMatch() {
		PropertyNameMatcher matcher = new DefaultPropertyNameMatcher("my_Col", 0, false, false);
		assertTrue(matcher.matches("myCol"));
		assertTrue(matcher.matches("my_Col"));
		assertTrue(matcher.matches("my Col"));
		assertFalse(matcher.matches("myCo"));
		assertFalse(matcher.matches("my__Col"));
		assertFalse(matcher.matches("myCol2"));
	}

	@Test
	public void testFullMatchCaseSensitive() {
		PropertyNameMatcher matcher = new DefaultPropertyNameMatcher("my_col", 0, false, true);
		assertTrue(matcher.matches("myCol"));
		assertFalse(matcher.matches("mycol"));
	}

	@Test
	public void testFullMatchExactMath() {
		PropertyNameMatcher matcher = new DefaultPropertyNameMatcher("my_col", 0, true, false);
		assertTrue(matcher.matches("my_col"));
		assertTrue(matcher.matches("my_COL"));
		assertFalse(matcher.matches("myCol"));
	}
	
	@Test
	public void testStartOf() {
		PropertyNameMatcher matcher = new DefaultPropertyNameMatcher("my_Col_top_bottom", 0, false, false);
		assertTrue(matcher.partialMatch("myCol").getLeftOverMatcher().partialMatch("top").getLeftOverMatcher().matches("bottom"));
		assertTrue(matcher.partialMatch("my_Col").getLeftOverMatcher().partialMatch("tOp").getLeftOverMatcher().matches("bottom"));
		assertTrue(matcher.partialMatch("my Col").getLeftOverMatcher().partialMatch("tOp").getLeftOverMatcher().matches("bottom"));
		assertNull(matcher.partialMatch("my__Col"));
		assertNull(matcher.partialMatch("myCol2"));
	}
	
	@Test
	public void testMatchIndex() {
		assertEquals(2, new DefaultPropertyNameMatcher("elt2", 0, false, false).matchIndex().getIndexValue());
		assertEquals(2, new DefaultPropertyNameMatcher("elt_2", 0, false, false).matchIndex().getIndexValue());
		assertNull(new DefaultPropertyNameMatcher("elt_ipv2", 0, false, false).matchIndex());
		
	}
	

}
