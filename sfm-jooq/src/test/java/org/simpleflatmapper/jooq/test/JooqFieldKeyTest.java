package org.simpleflatmapper.jooq.test;

import org.jooq.Field;
import org.junit.Test;
import org.simpleflatmapper.jooq.JooqFieldKey;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JooqFieldKeyTest {

    @Test
    public void testKey() {
        Field field1 = mock(Field.class);
        when(field1.getName()).thenReturn("id");
        when(field1.getType()).thenReturn(long.class);
        when(field1.toString()).thenReturn("f1");

        JooqFieldKey key = new JooqFieldKey(field1, 1);

        final JooqFieldKey boo = key.alias("boo");

        assertEquals("id", key.getName());
        assertEquals("boo", boo.getName());
        assertEquals(1, key.getIndex());
        assertEquals(1, boo.getIndex());
        assertSame(field1, boo.getField());
        assertSame(key, boo.getParent());

        assertTrue(key.equals(key));
        assertFalse(key.equals(boo));

        assertNotNull(key.toString());
    }
}