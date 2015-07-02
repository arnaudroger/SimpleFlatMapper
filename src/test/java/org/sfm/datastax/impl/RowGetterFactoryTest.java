package org.sfm.datastax.impl;

import com.datastax.driver.core.Row;
import org.junit.Before;
import org.junit.Test;
import org.sfm.datastax.DatastaxColumnKey;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.DoubleGetter;
import org.sfm.reflect.primitive.FloatGetter;
import org.sfm.reflect.primitive.IntGetter;
import org.sfm.reflect.primitive.LongGetter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@SuppressWarnings("unchecked")
public class RowGetterFactoryTest {

    DatastaxColumnKey columnKey = new DatastaxColumnKey("na", 1);

    Row row;
    @Before
    public void setUp() {
        row = mock(Row.class);
        when(row.getInt(1)).thenReturn(12);
        when(row.getLong(1)).thenReturn(13l);
        when(row.getFloat(1)).thenReturn(14.4f);
        when(row.getDouble(1)).thenReturn(15.4);
    }

    @Test
    public void testLongGetterOnNonNullValue() throws Exception {
        assertEquals(13l, new RowGetterFactory().newGetter(Long.class, columnKey, null).get(row));
    }

    @Test
    public void testLongGetterPrimitive() throws Exception {
        assertEquals(13l, ((LongGetter<Row>)new RowGetterFactory().newGetter(long.class, columnKey, null)).getLong(row));
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
    public void testIntGetterPrimitive() throws Exception {
        assertEquals(12, ((IntGetter<Row>)new RowGetterFactory().newGetter(int.class, columnKey, null)).getInt(row));
    }

    @Test
    public void testIntGetterOnNullValue() throws Exception {
        when(row.isNull(1)).thenReturn(true);
        assertEquals(null, new RowGetterFactory().newGetter(Integer.class, columnKey, null).get(row));
    }

    @Test
    public void testFloatGetterOnNonNullValue() throws Exception {
        assertEquals(14.4f, new RowGetterFactory().newGetter(Float.class, columnKey, null).get(row));
    }

    @Test
    public void testFloatGetterPrimitive() throws Exception {
        assertEquals(14.4f, ((FloatGetter<Row>)new RowGetterFactory().newGetter(float.class, columnKey, null)).getFloat(row), 0.001);
    }

    @Test
    public void testFloatGetterOnNullValue() throws Exception {
        when(row.isNull(1)).thenReturn(true);
        assertEquals(null, new RowGetterFactory().newGetter(Float.class, columnKey, null).get(row));
    }

    @Test
    public void testDoubleGetterOnNonNullValue() throws Exception {
        assertEquals(15.4, new RowGetterFactory().newGetter(Double.class, columnKey, null).get(row));
    }

    @Test
    public void testDoubleGetterPrimitive() throws Exception {
        assertEquals(15.4, ((DoubleGetter<Row>) new RowGetterFactory().newGetter(double.class, columnKey, null)).getDouble(row), 0.001);
    }

    @Test
    public void testDoubleGetterOnNullValue() throws Exception {
        when(row.isNull(1)).thenReturn(true);
        assertEquals(null, new RowGetterFactory().newGetter(Double.class, columnKey, null).get(row));
    }
}