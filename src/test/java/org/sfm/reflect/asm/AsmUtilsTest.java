package org.sfm.reflect.asm;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AsmUtilsTest {

	@Test
	public void testToClass() throws  Exception {
		assertEquals(int.class, AsmUtils.toGenericType("I", null, null));
		assertEquals(String.class, AsmUtils.toGenericType("java/lang/String", null, null));
		assertEquals(String.class, AsmUtils.toGenericType("Ljava/lang/String;", null, null));
	}

	@Test
	public void testToGenericType() throws ClassNotFoundException {

		ParameterizedType pt = (ParameterizedType) AsmUtils.toGenericType("java/util/List<Ljava/util/List<Ljava/lang/String;>;>", null, null);
		ParameterizedType pt2 = (ParameterizedType) pt.getActualTypeArguments()[0];
		assertEquals(List.class, pt.getRawType());
		assertEquals(List.class, pt2.getRawType());
		assertEquals(String.class, pt2.getActualTypeArguments()[0]);
	}

	@Test
	public void testToClassFromGeneric() throws  Exception {
		assertEquals(String.class, AsmUtils.toGenericType("TT1;", Arrays.asList("T0", "T1"), new ParameterizedType() {
			@Override
			public Type[] getActualTypeArguments() {
				return new Type[] {Long.class, String.class};
			}

			@Override
			public Type getRawType() {
				return null;
			}

			@Override
			public Type getOwnerType() {
				return null;
			}
		}));
	}

	@Test
	public void extractGenericTypeName() throws  Exception {
		Assert.assertArrayEquals(new String[] { "T1", "T2", "O"},
				AsmUtils.extractGenericTypeNames("<T1:Ljava.lang.Object;T2:Ljava.lang.Object;O:java.lang.Object>Ljava.lang.Object;").toArray(new String[] {}));
	}


	@Test
	public void extractContructorTypes() throws  Exception {
		String types = "([CILjava/util/List<Lorg/sfm/beans/DbObject;>;TT1;Ljava/lang/String;)V";
		assertEquals(Arrays.asList("[C", "I", "Ljava/util/List<Lorg/sfm/beans/DbObject;>;", "TT1;", "Ljava/lang/String;"), AsmUtils.extractConstructorTypeNames(types));
	}




}
