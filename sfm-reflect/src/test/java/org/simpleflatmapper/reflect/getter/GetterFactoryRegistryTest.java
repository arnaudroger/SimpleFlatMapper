package org.simpleflatmapper.reflect.getter;

import org.junit.Before;
import org.junit.Test;
import org.simpleflatmapper.reflect.Getter;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Date;

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

        getterFactoryRegistry.put(Timestamp.class, gf1);
    }

    @Test
    public void testIndirectLookup() {
        assertSame(gf1, getterFactoryRegistry.findFactoryFor(Date.class));
    }

    @Test
    public void testDirectLookup() {
        assertSame(gf1, getterFactoryRegistry.findFactoryFor(Timestamp.class));
    }

    @Test
    public void testNoMatch() {
        assertNull(getterFactoryRegistry.findFactoryFor(java.sql.Date.class));
    }

}