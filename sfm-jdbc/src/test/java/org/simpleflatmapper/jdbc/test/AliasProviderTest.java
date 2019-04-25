package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.impl.JpaAliasProvider;
import org.simpleflatmapper.jdbc.impl.JpaAliasProviderFactory;
import org.simpleflatmapper.reflect.getter.ConstantBooleanGetter;
import org.simpleflatmapper.reflect.meta.AliasProvider;
import org.simpleflatmapper.reflect.meta.AliasProviderService;
import org.simpleflatmapper.reflect.meta.ArrayAliasProvider;
import org.simpleflatmapper.reflect.meta.DefaultAliasProvider;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.ListCollector;

import javax.persistence.Column;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class AliasProviderTest {

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
        AliasProvider aliasProvider = AliasProviderService.getAliasProvider();
        if (aliasProvider instanceof ArrayAliasProvider) {
            ArrayAliasProvider arrayAliasProvider = (ArrayAliasProvider) aliasProvider;
            for(AliasProvider ap : arrayAliasProvider.providers()) {
                if (ap instanceof JpaAliasProvider) {
                    return;
                }
            }

            fail();

        } else {
            assertEquals(JpaAliasProvider.class, aliasProvider.getClass());
        }
    }
    @Test
    public void testFactoryJPANotPresent() throws Exception {
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
            Class<?> jpa = cl.loadClass(JpaAliasProviderFactory.class.getName());
            Class<?> consumerClass = cl.loadClass(Consumer.class.getName());

            Object consumer = cl.loadClass(ListCollector.class.getName()).newInstance();
            jpa.getMethod("produce", consumerClass).invoke(jpa.getConstructor().newInstance(), consumer);

            List<AliasProvider> list = (List<AliasProvider>) consumer.getClass().getMethod("getList").invoke(consumer);

            for(Object ap : list) {
                if (ap.getClass().getName().equals(JpaAliasProvider.class.getName())) fail();
            }

        } finally {
            Thread.currentThread().setContextClassLoader(original);
        }
    }
}
