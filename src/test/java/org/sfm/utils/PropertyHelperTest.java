package org.sfm.utils;

import static org.junit.Assert.*;

import org.junit.Test;
import org.sfm.utils.PropertyNameMatcher;

public class PropertyHelperTest {

	@Test
	public void testToPropertyName() {
		assertEquals("myColumnName", PropertyNameMatcher.toPropertyName("my_column_name"));
	}

}
