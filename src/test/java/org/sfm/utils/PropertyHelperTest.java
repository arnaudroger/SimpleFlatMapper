package org.sfm.utils;

import static org.junit.Assert.*;

import org.junit.Test;
import org.sfm.utils.PropertyHelper;

public class PropertyHelperTest {

	@Test
	public void testToPropertyName() {
		assertEquals("mycolumnname", PropertyHelper.toPropertyName("my_column_name"));
	}

}
