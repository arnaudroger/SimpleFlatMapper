package org.simpleflatmapper.reflect.test.meta;

import org.junit.Test;
import org.simpleflatmapper.reflect.meta.DefaultPropertyNameMatcher;
import org.simpleflatmapper.reflect.meta.PropertyNameMatch;
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
	public void singularColumnToRegularPluralProperties() {
		PropertyNameMatcher matcher = new DefaultPropertyNameMatcher("foo", 0, false, false);
		PropertyNameMatch m = matcher.matches("foos", true);
		assertNotNull(m);


		matcher = new DefaultPropertyNameMatcher("foo_bar", 0, false, false);
		assertNull(matcher.matches("foos", true));
		PropertyNameMatch pm = matcher.partialMatch("foos", true);
		assertNotNull(pm);
		assertEquals("_bar", pm.getLeftOverMatcher().toString());


		matcher = new DefaultPropertyNameMatcher("foo", 0, false, false);
		m = matcher.matches("foosses", true);
		assertNull(m);
		matcher = new DefaultPropertyNameMatcher("foo_bar", 0, false, false);
		pm = matcher.partialMatch("foosses", true);
		assertNull(pm);


	}

	// es


	@Test
	public void singularColumnToPluralPropertiesBusES() {
		PropertyNameMatcher matcher = new DefaultPropertyNameMatcher("bus", 0, false, false);
		PropertyNameMatch m = matcher.matches("buses", true);
		assertNotNull(m);


		matcher = new DefaultPropertyNameMatcher("bus_bar", 0, false, false);
		assertNull(matcher.matches("buses", true));
		PropertyNameMatch pm = matcher.partialMatch("buses", true);
		assertNotNull(pm);
		assertEquals("_bar", pm.getLeftOverMatcher().toString());


		matcher = new DefaultPropertyNameMatcher("bus", 0, false, false);
		m = matcher.matches("busess", true);
		assertNull(m);
		matcher = new DefaultPropertyNameMatcher("bus_bar", 0, false, false);
		pm = matcher.partialMatch("busess", true);
		assertNull(pm);

	}

	// [z]es
	@Test
	public void singularColumnToPluralPropertiesfezzes() {
		PropertyNameMatcher matcher = new DefaultPropertyNameMatcher("fez", 0, false, false);
		PropertyNameMatch m = matcher.matches("fezzes", true);
		assertNotNull(m);


		matcher = new DefaultPropertyNameMatcher("fez_bar", 0, false, false);
		assertNull(matcher.matches("fezzes", true));
		PropertyNameMatch pm = matcher.partialMatch("fezzes", true);
		assertNotNull(pm);
		assertEquals("_bar", pm.getLeftOverMatcher().toString());


		matcher = new DefaultPropertyNameMatcher("fez", 0, false, false);
		m = matcher.matches("fezzess", true);
		assertNull(m);
		matcher = new DefaultPropertyNameMatcher("fez_bar", 0, false, false);
		pm = matcher.partialMatch("fezzess", true);
		assertNull(pm);
	}

	// [s]es
	@Test
	public void singularColumnToPluralPropertiesgasses() {
		PropertyNameMatcher matcher = new DefaultPropertyNameMatcher("gas", 0, false, false);
		PropertyNameMatch m = matcher.matches("gasses", true);
		assertNotNull(m);


		matcher = new DefaultPropertyNameMatcher("gas_bar", 0, false, false);
		assertNull(matcher.matches("gasses", true));
		PropertyNameMatch pm = matcher.partialMatch("gasses", true);
		assertNotNull(pm);
		assertEquals("_bar", pm.getLeftOverMatcher().toString());


		matcher = new DefaultPropertyNameMatcher("gas", 0, false, false);
		m = matcher.matches("gassess", true);
		assertNull(m);
		matcher = new DefaultPropertyNameMatcher("gas_bar", 0, false, false);
		pm = matcher.partialMatch("gassess", true);
		assertNull(pm);
	}

	// (f)ves
	@Test
	public void singularColumnToPluralPropertieswolves() {
		PropertyNameMatcher matcher = new DefaultPropertyNameMatcher("wolf", 0, false, false);
		PropertyNameMatch m = matcher.matches("wolves", true);
		assertNotNull(m);


		matcher = new DefaultPropertyNameMatcher("wolf_bar", 0, false, false);
		assertNull(matcher.matches("wolves", true));
		PropertyNameMatch pm = matcher.partialMatch("wolves", true);
		assertNotNull(pm);
		assertEquals("_bar", pm.getLeftOverMatcher().toString());


		matcher = new DefaultPropertyNameMatcher("wolf", 0, false, false);
		m = matcher.matches("wolvess", true);
		assertNull(m);
		matcher = new DefaultPropertyNameMatcher("wolf_bar", 0, false, false);
		pm = matcher.partialMatch("wolvess", true);
		assertNull(pm);
	}


	// (y)ies
	@Test
	public void singularColumnToPluralPropertiescities() {
		PropertyNameMatcher matcher = new DefaultPropertyNameMatcher("city", 0, false, false);
		PropertyNameMatch m = matcher.matches("cities", true);
		assertNotNull(m);


		matcher = new DefaultPropertyNameMatcher("city_bar", 0, false, false);
		assertNull(matcher.matches("cities", true));
		PropertyNameMatch pm = matcher.partialMatch("cities", true);
		assertNotNull(pm);
		assertEquals("_bar", pm.getLeftOverMatcher().toString());


		matcher = new DefaultPropertyNameMatcher("city", 0, false, false);
		m = matcher.matches("citiess", true);
		assertNull(m);
		matcher = new DefaultPropertyNameMatcher("city_bar", 0, false, false);
		pm = matcher.partialMatch("citiess", true);
		assertNull(pm);
	}


	// (i)es
	@Test
	public void singularColumnToPluralPropertiesanalyses() {
		PropertyNameMatcher matcher = new DefaultPropertyNameMatcher("analysis", 0, false, false);
		PropertyNameMatch m = matcher.matches("analyses", true);
		assertNotNull(m);


		matcher = new DefaultPropertyNameMatcher("analysis_bar", 0, false, false);
		assertNull(matcher.matches("analyses", true));
		PropertyNameMatch pm = matcher.partialMatch("analyses", true);
		assertNotNull(pm);
		assertEquals("_bar", pm.getLeftOverMatcher().toString());


		matcher = new DefaultPropertyNameMatcher("analysis", 0, false, false);
		m = matcher.matches("analysess", true);
		assertNull(m);
		matcher = new DefaultPropertyNameMatcher("analysis_bar", 0, false, false);
		pm = matcher.partialMatch("analysess", true);
		assertNull(pm);
	}


	@Test
	public void test577() {
		assertNotNull(new DefaultPropertyNameMatcher("start_date  ", 0, false, false).matches("startDate"));
	}
	

}
