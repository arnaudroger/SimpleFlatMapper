package org.simpleflatmapper.util.test;

import org.junit.Assert;
import org.junit.Test;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.util.TypeHelper;
import org.simpleflatmapper.util.TypeReference;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;

import static org.junit.Assert.*;

public class TypeHelperTest {


	@Test
	public void testToClass() {
		Assert.assertEquals(Map.class, TypeHelper.toClass(new TypeReference<Map<String, String>>() {}.getType()));

		ParameterizedType type = (ParameterizedType) new TypeReference<Map<? extends Number, String>>() {
		}.getType();
		assertEquals(Number.class, TypeHelper.toClass(type.getActualTypeArguments()[0]));

		assertEquals(Date.class, TypeHelper.toClass(MyComparable.class.getTypeParameters()[0]));

		assertEquals(Date.class, TypeHelper.toClass(Date.class));

		try {
			TypeHelper.toClass(new Type() {
				public String getTypeName() {
					return null;
				}
			});
			fail();
		} catch (UnsupportedOperationException  e) {

		}

		assertEquals(String[].class, TypeHelper.toClass(new GenericArrayType() {
			@Override
			public Type getGenericComponentType() {
				return String.class;
			}
		}));

	}

	@Test
	public void testIs() {
		assertTrue(TypeHelper.isJavaLang(Long.class));
		assertFalse(TypeHelper.isJavaLang(Date.class));
		assertTrue(TypeHelper.isPrimitive(long.class));
		assertFalse(TypeHelper.isPrimitive(Long.class));

		assertTrue(TypeHelper.isArray(new Object[0].getClass()));
		assertFalse(TypeHelper.isArray(new Object().getClass()));


		assertTrue(TypeHelper.isNumber(Long.class));
		assertTrue(TypeHelper.isNumber(BigDecimal.class));
		assertFalse(TypeHelper.isNumber(Date.class));

		assertTrue(TypeHelper.isEnum(E.class));
		assertFalse(TypeHelper.isEnum(BigDecimal.class));
	}


	@Test
	public void testIsAssignable() {
		assertTrue(TypeHelper.isAssignable((Type)int.class, (Type)Integer.class));
		assertTrue(TypeHelper.isAssignable((Type)Number.class, (Type)Integer.class));
		assertFalse(TypeHelper.isAssignable((Type)Integer.class, (Type)Number.class));
	}

	@Test
	public void testGetTypeMaps() {
		Type type = new TypeReference<Map<String, Number>>() {
		}.getType();
		Map<TypeVariable<?>, Type> typesMap = TypeHelper.getTypesMap(type);

		assertEquals(2, typesMap.size());
		assertEquals(String.class, typesMap.get(Map.class.getTypeParameters()[0]));
		assertEquals(Number.class,  typesMap.get(Map.class.getTypeParameters()[1]));
	}

	enum E {A, B}

	static class MyComparable<T extends Date> implements Comparable<T> {

		@Override
		public int compareTo(T o) {
			return 0;
		}
	}

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
		assertEquals(Object.class, TypeHelper.getComponentTypeOfListOrArray(List.class));
	}

	@Test
	public void testGetComponentTypeOfStringList() {
		assertEquals(String.class, TypeHelper.getComponentTypeOfListOrArray(StringList.class));
	}

	@Test
	public void testGetKeyValueTypeOfMapOnGenericInterface() {
		TypeHelper.MapEntryTypes typeOfMap = TypeHelper.getKeyValueTypeOfMap(new TypeReference<Map<String, Integer>>() {
		}.getType());
		assertEquals(new TypeHelper.MapEntryTypes(String.class, Integer.class),
				typeOfMap);
		assertEquals(String.class, typeOfMap.getKeyType());
		assertEquals(Integer.class, typeOfMap.getValueType());

		assertEquals(new TypeHelper.MapEntryTypes(Object.class, Object.class),
				TypeHelper.getKeyValueTypeOfMap(Map.class));
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
	public void testMapperEntryTypes() {
		assertEquals(new TypeHelper.MapEntryTypes(String.class, Integer.class), new TypeHelper.MapEntryTypes(String.class, Integer.class));
		assertNotEquals(new TypeHelper.MapEntryTypes(Number.class, Integer.class), new TypeHelper.MapEntryTypes(String.class, Integer.class));

		assertEquals(new TypeHelper.MapEntryTypes(String.class, Integer.class).hashCode(), new TypeHelper.MapEntryTypes(String.class, Integer.class).hashCode());
		assertNotEquals(new TypeHelper.MapEntryTypes(Number.class, Integer.class).hashCode(), new TypeHelper.MapEntryTypes(String.class, Integer.class).hashCode());

		new TypeHelper.MapEntryTypes(String.class, Integer.class).toString();
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
