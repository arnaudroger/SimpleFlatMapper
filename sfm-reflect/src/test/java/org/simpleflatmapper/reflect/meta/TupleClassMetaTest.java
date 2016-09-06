package org.simpleflatmapper.reflect.meta;

import org.junit.Test;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.test.junit.LibrarySetsClassLoader;
import org.simpleflatmapper.tuple.Tuple2;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import static org.junit.Assert.*;

public class TupleClassMetaTest {

    @Test
    public void testRespecify() throws ClassNotFoundException, MalformedURLException {
        ClassLoader cl = new URLClassLoader(new URL[] {LibrarySetsClassLoader.findUrl(Tuple2.class, getClass().getClassLoader())}, null) {
            @Override
            public InputStream getResourceAsStream(String name) {
                return null;
            }
        };
        ReflectionService reflectionService = new ReflectionService(null) {
        };

        Class<?> tuple2Class = cl.loadClass(Tuple2.class.getName());

        TupleClassMeta classMeta = new TupleClassMeta(tuple2Class, reflectionService);

        List<InstantiatorDefinition> instantiatorDefinitions = classMeta.getInstantiatorDefinitions();

        assertEquals(1, instantiatorDefinitions.size());
        assertEquals("element0", instantiatorDefinitions.get(0).getParameters()[0].getName());
    }

}