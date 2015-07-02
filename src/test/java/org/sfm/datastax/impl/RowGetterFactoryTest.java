package org.sfm.datastax.impl;

import com.datastax.driver.core.Row;
import org.junit.Before;
import org.junit.Test;
import org.sfm.datastax.DatastaxColumnKey;
import org.sfm.reflect.Getter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class RowGetterFactoryTest {

    DatastaxColumnKey columnKey = new DatastaxColumnKey("na", 1);

    Row row;
    @Before
    public void setUp() {
        row = mock(Row.class);
        when(row.getInt(1)).thenReturn(12);
        when(row.getLong(1)).thenReturn(13l);
    }

    @Test
    public void testLongGetterOnNonNullValue() throws Exception {
        assertEquals(13l, new RowGetterFactory().newGetter(Long.class, columnKey, null).get(row));
    }

    @Test
    public void testLongGetterPrimtive() throws Exception {
        assertEquals(13l, new RowGetterFactory().newGetter(long.class, columnKey, null).get(row));
    }

    @Test
    public void testLongGetterOnNullValue() throws Exception {
        when(row.isNull(1)).thenReturn(true);
        assertEquals(null, new RowGetterFactory().newGetter(Long.class, columnKey, null).get(row));
    }

    @Test
    public void testIntGetterOnNonNullValue() throws Exception {
        assertEquals(12, new RowGetterFactory().newGetter(Integer.class, columnKey, null).get(row));
    }

    @Test
    public void testIntGetterPrimtive() throws Exception {
        assertEquals(12, new RowGetterFactory().newGetter(int.class, columnKey, null).get(row));
    }

    @Test
    public void testIntGetterOnNullValue() throws Exception {
        when(row.isNull(1)).thenReturn(true);
        assertEquals(null, new RowGetterFactory().newGetter(Integer.class, columnKey, null).get(row));
    }
}