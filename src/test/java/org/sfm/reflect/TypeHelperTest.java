package org.sfm.reflect;

import org.junit.Test;
import org.sfm.tuples.Tuple2;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	@Test
	public void testGetComponentTypeOfListOrArray() {
		assertEquals(String.class, TypeHelper.getComponentTypeOfListOrArray(String[].class));
		assertEquals(String.class, TypeHelper.getComponentTypeOfListOrArray(new TypeReference<List<String>>(){}.getType()));
		assertEquals(String.class, TypeHelper.getComponentTypeOfListOrArray(StringList.class));
	}

	@Test
	public void testGetKeyValueTypeOfMap() {
		assertEquals(new Tuple2<Type, Type>(String.class, Integer.class),
				TypeHelper.getKeyValueTypeOfMap(new TypeReference<Map<String, Integer>>() {
				}.getType()));
		assertEquals(new Tuple2<Type, Type>(String.class, Integer.class),
				TypeHelper.getKeyValueTypeOfMap(StringIntegerMap.class));
	}

	public static class StringList extends ArrayList<String> {
	}

	public static class StringIntegerMap extends HashMap<String, Integer> {
	}

}
