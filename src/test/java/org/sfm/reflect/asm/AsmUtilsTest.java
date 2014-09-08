package org.sfm.reflect.asm;

import static org.junit.Assert.*;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.junit.Test;

public class AsmUtilsTest {

	@Test
	public void testToGenericType() throws ClassNotFoundException {
		assertEquals(int.class, AsmUtils.toGenericType("I"));
		assertEquals(String.class, AsmUtils.toGenericType("java/lang/String"));
		assertEquals(String.class, AsmUtils.toGenericType("Ljava/lang/String;"));
		
		
		ParameterizedType pt = (ParameterizedType) AsmUtils.toGenericType("java/util/List<Ljava/util/List<Ljava/lang/String;>;>");
		ParameterizedType pt2 = (ParameterizedType) pt.getActualTypeArguments()[0];
		assertEquals(List.class, pt.getRawType());
		assertEquals(List.class, pt2.getRawType());
		assertEquals(String.class, pt2.getActualTypeArguments()[0]);
	}

}
