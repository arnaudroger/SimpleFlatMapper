package org.simpleflatmapper.reflect.test.meta;

import org.junit.Test;
import org.simpleflatmapper.reflect.meta.AliasProvider;
import org.simpleflatmapper.reflect.meta.ArrayAliasProvider;
import org.simpleflatmapper.reflect.meta.DefaultAliasProvider;
import org.simpleflatmapper.reflect.meta.Table;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class ArrayAliasProviderTest {

    @Test
    public void testProvider() {
        final Table table = new Table(null, null, null);
        ArrayAliasProvider p = new ArrayAliasProvider(new DefaultAliasProvider(), new AliasProvider() {
            @Override
            public String getAliasForMethod(Method method) {
                return "getAliasForMethod";
            }

            @Override
            public String getAliasForField(Field field) {
                return "getAliasForField";
            }

            @Override
            public Table getTable(Class<?> target) {
                return table;
            }
        });

        assertEquals("getAliasForField", p.getAliasForField(null));
        assertEquals("getAliasForMethod", p.getAliasForMethod(null));
        assertSame(table, p.getTable(null));


        ArrayAliasProvider p2 = new ArrayAliasProvider(new DefaultAliasProvider());

        assertNull(p2.getAliasForField(null));
        assertNull(p2.getAliasForMethod(null));
        assertEquals(Table.NULL, p2.getTable(null));
    }

}