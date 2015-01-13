package org.sfm.reflect;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class TypeHelperTest {

	@Test
	public void testIsNumber() {
		assertTrue(TypeHelper.isNumber(byte.class));
		assertTrue(TypeHelper.isNumber(Byte.class));
		assertTrue(TypeHelper.isNumber(BigDecimal.class));
		assertFalse(TypeHelper.isNumber(char.class));
		assertFalse(TypeHelper.isNumber(String.class));
	}
	
	@Test
	public void testWrap() {
		assertEquals(Character.class, TypeHelper.wrap(char.class));
	}

}
