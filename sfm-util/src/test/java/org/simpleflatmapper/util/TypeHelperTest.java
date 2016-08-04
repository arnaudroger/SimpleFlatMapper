package org.simpleflatmapper.util;

import org.junit.Test;
import org.simpleflatmapper.test.beans.DbObject;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;

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
	public void testAreCompatible() {
		assertTrue(TypeHelper.areCompatible(Integer.class, int.class));
		assertTrue(TypeHelper.areCompatible(Integer.class, Integer.class));
		assertTrue(TypeHelper.areCompatible(Number.class, int.class));
		assertTrue(TypeHelper.areCompatible(Number.class, Integer.class));
		assertTrue(TypeHelper.areCompatible(Object.class, int.class));
		assertTrue(TypeHelper.areCompatible(Object.class, Integer.class));
		assertFalse(TypeHelper.areCompatible(Double.class, Integer.class));
	}
	
	@Test
	public void testWrap() {
		assertEquals(Character.class, TypeHelper.wrap(char.class));
	}

	@Test
	public void testGetComponentTypeOfArray() {
		assertEquals(String.class, TypeHelper.getComponentTypeOfListOrArray(String[].class));
	}

	@Test
	public void testGetComponentTypeOfList() {
		assertEquals(String.class, TypeHelper.getComponentTypeOfListOrArray(new TypeReference<List<String>>(){}.getType()));
	}

	@Test
	public void testGetComponentTypeOfStringList() {
		assertEquals(String.class, TypeHelper.getComponentTypeOfListOrArray(StringList.class));
	}

	@Test
	public void testGetKeyValueTypeOfMapOnGenericInterface() {
		assertEquals(new TypeHelper.MapEntryTypes(String.class, Integer.class),
				TypeHelper.getKeyValueTypeOfMap(new TypeReference<Map<String, Integer>>() {
				}.getType()));
	}
	@Test
	public void testGetKeyValueTypeOfMapOnSpecifiedClass() {
		assertEquals(new TypeHelper.MapEntryTypes(String.class, Integer.class),
				TypeHelper.getKeyValueTypeOfMap(StringIntegerMap.class));
	}

	@Test
	public void testGetKeyValueTypeOfMapOnConcurrentMap() {
		assertEquals(new TypeHelper.MapEntryTypes(String.class, Integer.class),
				TypeHelper.getKeyValueTypeOfMap(new TypeReference<ConcurrentMap<String, Integer>>(){}.getType()));
	}


	@Test
	public void testGetGenericParametersForInterface() {
		Type type = new TypeReference<MyClass<DbObject, Integer>>() {
		}.getType();

		assertArrayEquals(new Type[] {String.class, Integer.class}, TypeHelper.getGenericParameterForClass(type, Map.class));
		assertArrayEquals(new Type[]{DbObject.class}, TypeHelper.getGenericParameterForClass(type, Callable.class));


	}

	@Test
	public void testGetGenericParametersForInterfaceNotImplement() {
		Type type = new TypeReference<MyClass<DbObject, String>>() {
		}.getType();

		try {
			TypeHelper.getGenericParameterForClass(type, Collection.class);
			fail("expect exception");
		} catch(IllegalArgumentException e) {
			//expected
		}
	}


	public static class MyClass<T, K> implements Map<String, K>, Callable<T>  {

		@Override
		public T call() throws Exception {
			return null;
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public boolean containsKey(Object key) {
			return false;
		}

		@Override
		public boolean containsValue(Object value) {
			return false;
		}

		@Override
		public K get(Object key) {
			return null;
		}

		@Override
		public K put(String key, K value) {
			return null;
		}

		@Override
		public K remove(Object key) {
			return null;
		}

		@Override
		public void putAll(Map<? extends String, ? extends K> m) {

		}

		@Override
		public void clear() {

		}

		@Override
		public Set<String> keySet() {
			return null;
		}

		@Override
		public Collection<K> values() {
			return null;
		}

		@Override
		public Set<Entry<String, K>> entrySet() {
			return null;
		}
	}

	public static class StringList extends MyList<String> {
	}

	public static class MyList<P> extends ArrayList<P> {

	}

	public static class StringIntegerMap extends HashMap<String, Integer> {
	}

}
