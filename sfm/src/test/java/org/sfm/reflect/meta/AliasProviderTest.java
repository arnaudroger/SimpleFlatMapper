package org.sfm.reflect.meta;

import org.junit.Test;

import javax.persistence.Column;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AliasProviderTest {

    @Test
    public void testDefaultAliasProvider() {
        DefaultAliasProvider p = new DefaultAliasProvider();
        assertNull(p.getAliasForField(null));
        assertNull(p.getAliasForMethod(null));
    }

    public class TestClass {
        @Column(name = "bar1")
        public String foo;

        @Column(name = "bar2")
        public String getFoo() {
            return null;
        }
    }
    @Test
    public void testJPAAliasProvider() throws NoSuchFieldException, NoSuchMethodException {
        JpaAliasProvider p = new JpaAliasProvider();

        assertEquals("bar1", p.getAliasForField(TestClass.class.getField("foo")));
        assertEquals("bar2", p.getAliasForMethod(TestClass.class.getMethod("getFoo")));

    }

    @Test
    public void testFactoryJPAPresent() {
        JpaAliasProvider.registers();
        assertEquals(JpaAliasProvider.class, AliasProviderFactory.getAliasProvider().getClass());
    }
    @Test
    public void testFactoryJPANotPresent() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final ClassLoader original = Thread.currentThread().getContextClassLoader();
        ClassLoader cl = new ClassLoader(ClassLoader.getSystemClassLoader().getParent()) {
            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException {
                if (!name.startsWith("javax.persistence")) {
                    InputStream resourceAsStream = original.getResourceAsStream(name.replace(".", "/") + ".class");
                    if (resourceAsStream == null) {
                        throw new ClassNotFoundException(name);
                    }
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    try {
                        int i;
                        while((i = resourceAsStream.read()) != -1) {
                            bos.write(i);
                        }
                        byte[] bytes = bos.toByteArray();
                        return defineClass(name, bytes, 0, bytes.length);
                    } catch (IOException e) {
                        throw new ClassNotFoundException(e.getMessage(), e);
                    } finally {
                        try {
                            resourceAsStream.close();
                        } catch (IOException e) {
                        }
                    }

                } else {
                   throw new ClassNotFoundException(name);
                }
            }
        };
        Thread.currentThread().setContextClassLoader(cl);
        try {
            Class<?> jpa = cl.loadClass(JpaAliasProvider.class.getName());
            jpa.getDeclaredMethod("registers").invoke(null);

            Class<?> factory = cl.loadClass(AliasProviderFactory.class.getName());

            Object provider = factory.getDeclaredMethod("getAliasProvider").invoke(factory);

            assertEquals(DefaultAliasProvider.class.getName(), provider.getClass().getName());
        } finally {
            Thread.currentThread().setContextClassLoader(original);
        }
    }
}
