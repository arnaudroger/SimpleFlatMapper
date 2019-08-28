package org.simpleflatmapper.reflect.test.meta;

import org.junit.Test;
import org.simpleflatmapper.reflect.meta.DefaultPropertyNameMatcher;
import org.simpleflatmapper.reflect.meta.PropertyNameMatcher;

import static org.junit.Assert.*;

public class DefaultPropertyNameMatcherTest {

	@Test
	public void testFullMatch() {
		PropertyNameMatcher matcher = new DefaultPropertyNameMatcher("my_Col", 0, false, false);
		assertNotNull(matcher.matches("myCol"));
		assertNotNull(matcher.matches("my_Col"));
		assertNotNull(matcher.matches("my Col"));
		assertNull(matcher.matches("myCo"));
		assertNotNull(matcher.matches("my__Col"));
		assertNull(matcher.matches("myCol2"));
	}

	@Test
	public void testFullMatchCaseSensitive() {
		PropertyNameMatcher matcher = new DefaultPropertyNameMatcher("my_col", 0, false, true);
		assertNotNull(matcher.matches("myCol"));
		assertNull(matcher.matches("mycol"));
	}

	@Test
	public void testFullMatchExactMath() {
		PropertyNameMatcher matcher = new DefaultPropertyNameMatcher("my_col", 0, true, false);
		assertNotNull(matcher.matches("my_col"));
		assertNotNull(matcher.matches("my_COL"));
		assertNull(matcher.matches("myCol"));
	}
	
	@Test
	public void testStartOf() {
		PropertyNameMatcher matcher = new DefaultPropertyNameMatcher("my_Col_top_bottom", 0, false, false);
		assertNull(matcher.partialMatch("myCo2"));
		assertNotNull(matcher.partialMatch("myCol").getLeftOverMatcher().partialMatch("top").getLeftOverMatcher().matches("bottom"));
		assertNotNull(matcher.partialMatch("my_Col").getLeftOverMatcher().partialMatch("tOp").getLeftOverMatcher().matches("bottom"));
		assertNotNull(matcher.partialMatch("my Col").getLeftOverMatcher().partialMatch("tOp").getLeftOverMatcher().matches("bottom"));
		assertNotNull(matcher.partialMatch("my__Col").getLeftOverMatcher().partialMatch("tOp").getLeftOverMatcher().matches("bottom"));
	}

	@Test
	public void testStartOfPartial() {
		PropertyNameMatcher matcher = new DefaultPropertyNameMatcher("_id", 0, false, false);
		assertNotNull(matcher.partialMatch("idTest"));
		assertNull(matcher.partialMatch("object"));
		assertNull(matcher.partialMatch("idtest"));
	}
	
	@Test
	public void testMatchIndex() {
		assertEquals(2, new DefaultPropertyNameMatcher("elt2", 0, false, false).matchIndex().getIndexValue());
		assertEquals(2, new DefaultPropertyNameMatcher("elt_2", 0, false, false).matchIndex().getIndexValue());
		assertNull(new DefaultPropertyNameMatcher("elt_ipv2", 0, false, false).matchIndex());
		
	}
	
	@Test
	public void test577() {
		assertNotNull(new DefaultPropertyNameMatcher("start_date  ", 0, false, false).matches("startDate"));
	}
	

}
