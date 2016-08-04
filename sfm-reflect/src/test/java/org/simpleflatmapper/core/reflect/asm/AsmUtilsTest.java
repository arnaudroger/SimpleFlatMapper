package org.simpleflatmapper.core.reflect.asm;

import org.junit.Assert;
import org.junit.Test;
import org.simpleflatmapper.util.TypeHelper;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    public void testToGenericTypeTuples() throws ClassNotFoundException {

        ParameterizedType pt = (ParameterizedType) AsmUtils.toGenericType("Lorg/simpleflatmapper/core/tuples/Tuple2<TT1;TT2;>;", Arrays.asList("T1", "T2"), new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[] { String.class, Long.class};
            }

            @Override
            public Type getRawType() {
                return null;
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
		assertEquals(String.class, AsmUtils.toGenericType("TT1;", Arrays.asList("T0", "T1"), new ParameterizedType() {
			@Override
			public Type[] getActualTypeArguments() {
				return new Type[]{Long.class, String.class};
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
		Assert.assertArrayEquals(new String[]{"T1", "T2", "O"},
				AsmUtils.extractGenericTypeNames("<T1:Ljava.lang.Object;T2:Ljava.lang.Object;O:java.lang.Object>Ljava.lang.Object;").toArray(new String[]{}));
	}


	@Test
	public void extractConstructorTypes() throws  Exception {
		String types = "([CILjava/util/List<Lorg/simpleflatmapper/beans/DbObject;>;TT1;Ljava/lang/String;)V";
		assertEquals(Arrays.asList("[C", "I", "Ljava/util/List<Lorg/simpleflatmapper/beans/DbObject;>;", "TT1;", "Ljava/lang/String;"),
				AsmUtils.extractTypeNames(types));
	}

	@Test
	public void extractMethodTypesOptional() throws  Exception {
		String types = "<T:Ljava/lang/Object;>(TT;)Ljava/util/Optional<TT;>;";
		assertEquals(Arrays.asList("TT;", "Ljava/util/Optional<TT;>;"),AsmUtils.extractTypeNames(types));
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

}
