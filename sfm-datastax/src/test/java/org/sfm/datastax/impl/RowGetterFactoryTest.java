package org.sfm.datastax.impl;

import com.datastax.driver.core.GettableData;
import com.datastax.driver.core.Row;
import org.junit.Before;
import org.junit.Test;
import org.sfm.datastax.DatastaxColumnKey;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@SuppressWarnings("unchecked")
public class RowGetterFactoryTest {

    DatastaxColumnKey columnKey = new DatastaxColumnKey("na", 1);

    GettableData row;

    Date date = new Date();
    @Before
    public void setUp() throws UnknownHostException {
        row = mock(GettableData.class);
        when(row.getInt(1)).thenReturn(12);
        when(row.getLong(1)).thenReturn(13l);
        when(row.getFloat(1)).thenReturn(14.4f);
        when(row.getDouble(1)).thenReturn(15.4);
        when(row.getString(1)).thenReturn("str");
        when(row.getBool(1)).thenReturn(Boolean.TRUE);
        when(row.getDate(1)).thenReturn(date);
        when(row.getDecimal(1)).thenReturn(new BigDecimal("2.123"));
        when(row.getVarint(1)).thenReturn(new BigInteger("234"));
        when(row.getInet(1)).thenReturn(InetAddress.getByName("192.168.0.1"));
        when(row.getUUID(1)).thenReturn(new UUID(23, 23));
    }

    @Test
    public void testUUIDGetter() throws Exception {
        assertEquals(new UUID(23, 23), new RowGetterFactory().newGetter(UUID.class, columnKey, null).get(row));
    }

    @Test
    public void testInetAddressGetter() throws Exception {
        assertEquals(InetAddress.getByName("192.168.0.1"), new RowGetterFactory().newGetter(InetAddress.class, columnKey, null).get(row));
    }

    @Test
    public void testBigDecimalGetter() throws Exception {
        assertEquals(new BigDecimal("2.123"), new RowGetterFactory().newGetter(BigDecimal.class, columnKey, null).get(row));
    }

    @Test
    public void testBigIntegerGetter() throws Exception {
        assertEquals(new BigInteger("234"), new RowGetterFactory().newGetter(BigInteger.class, columnKey, null).get(row));
    }

    @Test
    public void testDateGetter() throws Exception {
        assertEquals(date, new RowGetterFactory().newGetter(Date.class, columnKey, null).get(row));
    }

    @Test
    public void testStringGetter() throws Exception {
        assertEquals("str", new RowGetterFactory().newGetter(String.class, columnKey, null).get(row));
    }

    @Test
    public void testBooleanGetterOnNonNullValue() throws Exception {
        assertEquals(true, new RowGetterFactory().newGetter(Boolean.class, columnKey, null).get(row));
    }

    @Test
    public void testBooleanGetterPrimitive() throws Exception {
        assertEquals(true, ((BooleanGetter<GettableData>) new RowGetterFactory().newGetter(boolean.class, columnKey, null)).getBoolean(row));
    }

    @Test
    public void testBooleanGetterOnNullValue() throws Exception {
        when(row.isNull(1)).thenReturn(true);
        assertEquals(null, new RowGetterFactory().newGetter(Boolean.class, columnKey, null).get(row));
    }

    @Test
    public void testLongGetterOnNonNullValue() throws Exception {
        assertEquals(13l, new RowGetterFactory().newGetter(Long.class, columnKey, null).get(row));
    }

    @Test
    public void testLongGetterPrimitive() throws Exception {
        assertEquals(13l, ((LongGetter<GettableData>) new RowGetterFactory().newGetter(long.class, columnKey, null)).getLong(row));
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
        assertEquals(12, ((IntGetter<GettableData>)new RowGetterFactory().newGetter(int.class, columnKey, null)).getInt(row));
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
        assertEquals(14.4f, ((FloatGetter<GettableData>)new RowGetterFactory().newGetter(float.class, columnKey, null)).getFloat(row), 0.001);
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
        assertEquals(15.4, ((DoubleGetter<GettableData>) new RowGetterFactory().newGetter(double.class, columnKey, null)).getDouble(row), 0.001);
    }

    @Test
    public void testDoubleGetterOnNullValue() throws Exception {
        when(row.isNull(1)).thenReturn(true);
        assertEquals(null, new RowGetterFactory().newGetter(Double.class, columnKey, null).get(row));
    }
}