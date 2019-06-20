package org.simpleflatmapper.reflect.test.asm;

import org.junit.Assert;
import org.junit.Test;
import org.simpleflatmapper.ow2asm.MethodVisitor;
import org.simpleflatmapper.ow2asm.Opcodes;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.asm.AsmUtils;
import org.simpleflatmapper.tuple.Tuple2;
import org.simpleflatmapper.util.TypeHelper;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static org.simpleflatmapper.ow2asm.Opcodes.*;

public class AsmUtilsTest {

	
	@Test
	public void testAPI() {
		String version = System.getProperty("java.version");
		System.out.println("version = " + version);
		if (version.startsWith("1.6") || version.startsWith("1.7")) {
			assertEquals(ASM5, AsmUtils.API);
		} else {
			//IFJAVA8_START
			assertEquals(ASM7_EXPERIMENTAL, AsmUtils.API);
			if (true) return;
			//IFJAVA8_END
			fail("fail " + version);
		}
	}
	@Test
	public void testToClass() throws  Exception {
		assertEquals(int.class, AsmUtils.toGenericType("I", null, null));
		assertEquals(String.class, AsmUtils.toGenericType("Ljava/lang/String;", null, null));
	}

	@Test
	public void testToGenericType() throws ClassNotFoundException {

		ParameterizedType pt = (ParameterizedType) AsmUtils.toGenericType("Ljava/util/List<Ljava/util/List<Ljava/lang/String;>;>;", null, null);
		ParameterizedType pt2 = (ParameterizedType) pt.getActualTypeArguments()[0];
		assertEquals(List.class, pt.getRawType());
		assertEquals(List.class, pt2.getRawType());
		assertEquals(String.class, pt2.getActualTypeArguments()[0]);

		assertEquals(pt, pt);
		assertEquals(pt.hashCode(), pt.hashCode());
		assertNotEquals(pt, pt2);
		assertEquals("ParameterizedTypeImpl{rawType=interface java.util.List, types=[ParameterizedTypeImpl{rawType=interface java.util.List, types=[class java.lang.String]}]}", pt.toString());
	}

	@Test
	public void testToGenericType662() throws ClassNotFoundException {
		ParameterizedType pt = (ParameterizedType) AsmUtils.toGenericType("Ljava/util/Map<Ljava/lang/String;[B>;", null, null);
		assertEquals(Map.class, pt.getRawType());
		assertEquals(String.class, pt.getActualTypeArguments()[0]);
		assertEquals(byte[].class, pt.getActualTypeArguments()[1]);

	}



	@Test
	public void testToAsmType() {
		assertEquals("java/lang/String", AsmUtils.toAsmType(String.class));
		assertEquals("java/lang/String", AsmUtils.toGenericAsmType(String.class));
		assertEquals("java/util/HashMap", AsmUtils.toAsmType(new HashMap<String, Long>() {}.getClass().getGenericSuperclass()));
		assertEquals("java/util/HashMap<Ljava/lang/String;Ljava/lang/Long;>", AsmUtils.toGenericAsmType(new HashMap<String, Long>() {}.getClass().getGenericSuperclass()));
	}


	@Test
	public void testToTargetTypeDeclaration() {
		assertEquals("I", AsmUtils.toTargetTypeDeclaration(int.class));
		assertEquals("Ljava/lang/String;", AsmUtils.toTargetTypeDeclaration(String.class));
		assertEquals("[Ljava/lang/String;", AsmUtils.toTargetTypeDeclaration(String[].class));


		assertEquals("Ljava/lang/String;", AsmUtils.toTargetTypeDeclaration("java/lang/String"));
		assertEquals("[Ljava/lang/String;", AsmUtils.toTargetTypeDeclaration("[Ljava/lang/String;"));
	}

	@Test
	public void testGetClosestPublicImpl() {
		assertEquals(HashMap.class, TypeHelper.toClass(AsmUtils.findClosestPublicTypeExposing(new HashMap<Object, Object>() {}.getClass(), Map.class)));
		assertNull(AsmUtils.findClosestPublicTypeExposing(new HashMap<Object, Object>() {}.getClass(), Getter.class));
	}

	@Test
	public void testToGenericTypeArray() throws ClassNotFoundException {
		assertEquals(String[].class, AsmUtils.toGenericType("[Ljava/lang/String;", Collections.<String>emptyList(), Object.class));
		assertEquals(byte[].class, AsmUtils.toGenericType("[B", Collections.<String>emptyList(), Object.class));
	}

    @Test
    public void testToGenericTypeTuples() throws ClassNotFoundException {

        ParameterizedType pt = (ParameterizedType) AsmUtils.toGenericType("Lorg/simpleflatmapper/tuple/Tuple2<TT1;TT2;>;", Arrays.asList("T1", "T2"), new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[] { String.class, Long.class};
            }

            @Override
            public Type getRawType() {
                return Tuple2.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        });
        assertEquals(String.class, TypeHelper.toClass(pt.getActualTypeArguments()[0]));
        assertEquals(Long.class, TypeHelper.toClass(pt.getActualTypeArguments()[1]));
    }

	@Test
	public void testToClassFromGeneric() throws  Exception {
		Type actual = AsmUtils.toGenericType("TT1;", Arrays.asList("T0", "T1"), new ParameterizedType() {
			@Override
			public Type[] getActualTypeArguments() {
				return new Type[]{Long.class, String.class};
			}

			@Override
			public Type getRawType() {
				return Object.class;
			}

			@Override
			public Type getOwnerType() {
				return null;
			}
		});
		assertEquals(String.class, actual);
	}

	@Test
	public void extractGenericTypeName() throws  Exception {
		Assert.assertArrayEquals(new String[]{"T1", "T2", "O"},
				AsmUtils.extractGenericTypeNames("<T1:Ljava.lang.Object;T2:Ljava.lang.Object;O:java.lang.Object>Ljava.lang.Object;").toArray(new String[]{}));


	}


	@Test
	public void extractConstructorTypes() throws  Exception {
		String types = "([CILjava/util/List<Lorg/simpleflatmapper/beans/DbObject;>;TT1;Ljava/lang/String;)V";
		assertEquals(Arrays.asList("[C", "I", "Ljava/util/List<Lorg/simpleflatmapper/beans/DbObject;>;", "TT1;", "Ljava/lang/String;"),
				AsmUtils.extractTypeNamesFromSignature(types));
	}
	@Test
	public void extractConstructorTypesIssue472() throws  Exception {
		List<String> types = AsmUtils.extractTypeNamesFromSignature("(Ljava/lang/String;Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;)V");
		Assert.assertArrayEquals(new String[] {"Ljava/lang/String;", "Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;"}, 
				types.toArray(new String[0]));
	}

	@Test
	public void extractConstructorTypesIssue488() throws  Exception {
		List<String> types = AsmUtils.extractTypeNamesFromSignature("(Lorg/jooq/lambda/tuple/Tuple2<TT1;TT2;>;)V");
		System.out.println("types = " + types);
		Assert.assertArrayEquals(new String[] {"Lorg/jooq/lambda/tuple/Tuple2<TT1;TT2;>;"},
				types.toArray(new String[0]));
	}

	@Test
	public void extractMethodTypesOptional() throws  Exception {
		String types = "<T:Ljava/lang/Object;>(TT;)Ljava/util/Optional<TT;>;";
		assertEquals(Arrays.asList("TT;", "Ljava/util/Optional<TT;>;"),AsmUtils.extractTypeNamesFromSignature(types));
	}

	@Test
	public void writeToClass() throws Exception {
		File dir = new File("target/asm-classes-dump-test");
		File f = new File(dir, "test/MyClass.class");
		if (f.exists()) f.delete();

		AsmUtils.writeClassToFileInDir("test.MyClass", new byte[]{0, 1, 2, 3}, dir);

		assertTrue(f.exists());
		assertTrue(f.length() == 4);
	}

	@Test
	public void testAddIndex() {
		MethodVisitor mv = mock(MethodVisitor.class);

		AsmUtils.addIndex(mv, 0);
		verify(mv).visitInsn(ICONST_0);

		AsmUtils.addIndex(mv, 1);
		verify(mv).visitInsn(ICONST_1);

		AsmUtils.addIndex(mv, 2);
		verify(mv).visitInsn(ICONST_2);

		AsmUtils.addIndex(mv, 3);
		verify(mv).visitInsn(ICONST_3);

		AsmUtils.addIndex(mv, 4);
		verify(mv).visitInsn(ICONST_4);

		AsmUtils.addIndex(mv, 5);
		verify(mv).visitInsn(ICONST_5);


		AsmUtils.addIndex(mv, 123);
		verify(mv).visitIntInsn(BIPUSH, 123);

		AsmUtils.addIndex(mv, 512);
		verify(mv).visitIntInsn(SIPUSH, 512);

		AsmUtils.addIndex(mv, Short.MAX_VALUE + 1);
		verify(mv).visitLdcInsn(Short.MAX_VALUE + 1);

	}

}
