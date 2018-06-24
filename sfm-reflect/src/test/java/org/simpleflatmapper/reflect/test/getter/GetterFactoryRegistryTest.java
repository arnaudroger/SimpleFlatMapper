package org.simpleflatmapper.reflect.test.getter;

import org.junit.Before;
import org.junit.Test;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.reflect.getter.GetterFactoryRegistry;

import java.lang.reflect.Type;

import static org.junit.Assert.*;

public class GetterFactoryRegistryTest {
    GetterFactoryRegistry<Object, Object> getterFactoryRegistry;
    GetterFactory<Object, Object> gf1;

    @Before
    public void setUp() {
        getterFactoryRegistry = new GetterFactoryRegistry<Object, Object>();

        gf1 = new GetterFactory<Object, Object>() {
            @Override
            public <P> Getter<Object, P> newGetter(Type target, Object key, Object... properties) {
                return null;
            }
        };

        getterFactoryRegistry.put(Long.class, gf1);
    }

//    @Test
//    public void testIndirectLookup() {
//        assertSame(gf1, getterFactoryRegistry.findFactoryFor(Number.class));
//    }

    @Test
    public void testDirectLookup() {
        assertSame(gf1, getterFactoryRegistry.findFactoryFor(Long.class));
    }

    @Test
    public void testNoMatch() {
        assertNull(getterFactoryRegistry.findFactoryFor(String.class));
    }

}