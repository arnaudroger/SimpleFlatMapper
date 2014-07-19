package org.flatmap.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class PropertyHelperTest {

	@Test
	public void testToPropertyName() {
		assertEquals("mycolumnname", PropertyHelper.toPropertyName("my_column_name"));
	}

}
