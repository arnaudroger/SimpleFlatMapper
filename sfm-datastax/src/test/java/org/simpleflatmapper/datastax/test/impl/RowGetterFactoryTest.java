package org.simpleflatmapper.datastax.test.impl;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.GettableData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.simpleflatmapper.datastax.impl.RowGetterFactory;
import org.simpleflatmapper.reflect.primitive.BooleanGetter;
import org.simpleflatmapper.reflect.primitive.ByteGetter;
import org.simpleflatmapper.reflect.primitive.DoubleGetter;
import org.simpleflatmapper.reflect.primitive.FloatGetter;
import org.simpleflatmapper.reflect.primitive.IntGetter;
import org.simpleflatmapper.reflect.primitive.LongGetter;
import org.simpleflatmapper.reflect.primitive.ShortGetter;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.datastax.DatastaxColumnKey;

import java.math.BigDecimal;
import java.math.BigInteger;
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
    DatastaxColumnKey columnKeyInt = new DatastaxColumnKey("na", 2, DataType.bigint());
    DatastaxColumnKey columnKeyString = new DatastaxColumnKey("na", 2, DataType.varchar());
    DatastaxColumnKey columnKey3 = new DatastaxColumnKey("na", 3);
    DatastaxColumnKey columnKey4 = new DatastaxColumnKey("na", 4);

    GettableData row;

    Date date = new Date();
    @Before
    public void setUp() throws UnknownHostException {
        row = mock(GettableData.class);
        when(row.getInt(1)).thenReturn(12);
        when(row.getInt(2)).thenReturn(2);
        when(row.getLong(1)).thenReturn(13l);
        when(row.getFloat(1)).thenReturn(14.4f);
        when(row.getDouble(1)).thenReturn(15.4);
        when(row.getByte(1)).thenReturn((byte)16);
        when(row.getShort(1)).thenReturn((short)17);
        when(row.getString(1)).thenReturn("str");
        when(row.getString(2)).thenReturn("type2");
        when(row.getString(3)).thenReturn(new UUID(23, 24).toString());
        when(row.getBool(1)).thenReturn(Boolean.TRUE);
        when(row.getTimestamp(1)).thenReturn(date);
        when(row.getDecimal(1)).thenReturn(new BigDecimal("2.123"));
        when(row.getVarint(1)).thenReturn(new BigInteger("234"));
        when(row.getInet(1)).thenReturn(InetAddress.getByName("192.168.0.1"));
        when(row.getUUID(1)).thenReturn(new UUID(23, 23));
        when(row.getObject(3)).thenReturn(2);
        when(row.getObject(4)).thenReturn("type2");
    }

    @Test
    public void testUUIDGetter() throws Exception {
        Assert.assertEquals(new UUID(23, 23), new RowGetterFactory(null).newGetter(UUID.class, columnKey).get(row));
    }
    @Test
    public void testUUIDGetterOnString() throws Exception {
        assertEquals(new UUID(23, 24), new RowGetterFactory(null).newGetter(UUID.class, columnKey(3, DataType.text())).get(row));
    }

    @Test
    public void testInetAddressGetter() throws Exception {
        assertEquals(InetAddress.getByName("192.168.0.1"), new RowGetterFactory(null).newGetter(InetAddress.class, columnKey).get(row));
    }

    @Test
    public void testBigDecimalGetter() throws Exception {
        assertEquals(new BigDecimal("2.123"), new RowGetterFactory(null).newGetter(BigDecimal.class, columnKey).get(row));
    }

    @Test
    public void testBigIntegerGetter() throws Exception {
        assertEquals(new BigInteger("234"), new RowGetterFactory(null).newGetter(BigInteger.class, columnKey).get(row));
    }

    @Test
    public void testDateGetter() throws Exception {
        assertEquals(date, new RowGetterFactory(null).newGetter(Date.class, columnKey).get(row));
    }

    @Test
    public void testStringGetter() throws Exception {
        assertEquals("str", new RowGetterFactory(null).newGetter(String.class, columnKey).get(row));
    }

    @Test
    public void testStringGetterOnUUID() throws Exception {
        assertEquals(new UUID(23, 23).toString(), new RowGetterFactory(null).newGetter(String.class, columnKey(DataType.uuid())).get(row));
    }

    @Test
    public void testBooleanGetterOnNonNullValue() throws Exception {
        assertEquals(true, new RowGetterFactory(null).newGetter(Boolean.class, columnKey).get(row));
    }

    @Test
    public void testBooleanGetterPrimitive() throws Exception {
        assertEquals(true, ((BooleanGetter<GettableData>) new RowGetterFactory(null).newGetter(boolean.class, columnKey)).getBoolean(row));
    }

    @Test
    public void testBooleanGetterOnNullValue() throws Exception {
        when(row.isNull(1)).thenReturn(true);
        assertEquals(null, new RowGetterFactory(null).newGetter(Boolean.class, columnKey).get(row));
    }

    @Test
    public void testLongGetterOnNonNullValue() throws Exception {
        assertEquals(13l, new RowGetterFactory(null).newGetter(Long.class, columnKey).get(row));
    }

    @Test
    public void testLongGetterPrimitive() throws Exception {
        assertEquals(13l, ((LongGetter<GettableData>) new RowGetterFactory(null).newGetter(long.class, columnKey)).getLong(row));
    }

    @Test
    public void testLongGetterOnNullValue() throws Exception {
        when(row.isNull(1)).thenReturn(true);
        assertEquals(null, new RowGetterFactory(null).newGetter(Long.class, columnKey).get(row));
    }

    @Test
    public void testIntGetterOnNonNullValue() throws Exception {
        assertEquals(12, new RowGetterFactory(null).newGetter(Integer.class, columnKey).get(row));
    }

    @Test
    public void testIntOnLongDatatype() throws  Exception {
        assertEquals(13, new RowGetterFactory(null).newGetter(Integer.class, columnKey.datatype(DataType.bigint())).get(row));
    }

    @Test
    public void testIntGetterPrimitive() throws Exception {
        assertEquals(12, ((IntGetter<GettableData>)new RowGetterFactory(null).newGetter(int.class, columnKey)).getInt(row));
    }

    @Test
    public void testIntGetterOnNullValue() throws Exception {
        when(row.isNull(1)).thenReturn(true);
        assertEquals(null, new RowGetterFactory(null).newGetter(Integer.class, columnKey).get(row));
    }

    //
    @Test
    public void testShortGetterShouldFailOnDatastax2() throws Exception {
        assertEquals((short)17, new RowGetterFactory(null).newGetter(Short.class, columnKey).get(row));
    }

    @Test
    public void testShortOnLongDatatype() throws  Exception {
        assertEquals((short)13, new RowGetterFactory(null).newGetter(Short.class, columnKey.datatype(DataType.bigint())).get(row));
    }

    @Test
    public void testShortGetterPrimitive() throws Exception {
        assertEquals((short)13, ((ShortGetter<GettableData>)new RowGetterFactory(null).newGetter(short.class, columnKey.datatype(DataType.bigint()))).getShort(row));
    }

    @Test
    public void testShortGetterOnNullValue() throws Exception {
        when(row.isNull(1)).thenReturn(true);
        assertEquals(null, new RowGetterFactory(null).newGetter(Short.class, columnKey.datatype(DataType.bigint())).get(row));
    }

    @Test
    public void testByteGetterShouldFailOnDatastax2() throws Exception {
        assertEquals((byte)16, new RowGetterFactory(null).newGetter(Byte.class, columnKey).get(row));
    }

    @Test
    public void testByteOnLongDatatype() throws  Exception {
        assertEquals((byte)13, new RowGetterFactory(null).newGetter(Byte.class, columnKey.datatype(DataType.bigint())).get(row));
    }

    @Test
    public void testByteGetterPrimitive() throws Exception {
        assertEquals((byte)13, ((ByteGetter<GettableData>)new RowGetterFactory(null).newGetter(byte.class, columnKey.datatype(DataType.bigint()))).getByte(row));
    }

    @Test
    public void testByteGetterOnNullValue() throws Exception {
        when(row.isNull(1)).thenReturn(true);
        assertEquals(null, new RowGetterFactory(null).newGetter(Byte.class, columnKey.datatype(DataType.bigint())).get(row));
    }


    @Test
    public void testFloatGetterOnNonNullValue() throws Exception {
        assertEquals(14.4f, new RowGetterFactory(null).newGetter(Float.class, columnKey).get(row));
    }

    @Test
    public void testFloatGetterPrimitive() throws Exception {
        assertEquals(14.4f, ((FloatGetter<GettableData>)new RowGetterFactory(null).newGetter(float.class, columnKey)).getFloat(row), 0.001);
    }

    @Test
    public void testFloatGetterOnNullValue() throws Exception {
        when(row.isNull(1)).thenReturn(true);
        assertEquals(null, new RowGetterFactory(null).newGetter(Float.class, columnKey).get(row));
    }

    @Test
    public void testDoubleGetterOnNonNullValue() throws Exception {
        assertEquals(15.4, new RowGetterFactory(null).newGetter(Double.class, columnKey).get(row));
    }

    @Test
    public void testDoubleGetterPrimitive() throws Exception {
        assertEquals(15.4, ((DoubleGetter<GettableData>) new RowGetterFactory(null).newGetter(double.class, columnKey)).getDouble(row), 0.001);
    }

    @Test
    public void testDoubleGetterOnNullValue() throws Exception {
        when(row.isNull(1)).thenReturn(true);
        assertEquals(null, new RowGetterFactory(null).newGetter(Double.class, columnKey).get(row));
    }

    @Test
    public void testEnumGetterOnSpecifiedIntType() throws Exception {
        assertEquals(DbObject.Type.type3, new RowGetterFactory(null).newGetter(DbObject.Type.class, columnKeyInt).get(row));
    }
    @Test
    public void testEnumGetterOnSpecifiedStringType() throws Exception {
        assertEquals(DbObject.Type.type2, new RowGetterFactory(null).newGetter(DbObject.Type.class, columnKeyString).get(row));
    }
    @Test
    public void testEnumGetterOnUnspecifiedIntType() throws Exception {
        assertEquals(DbObject.Type.type3, new RowGetterFactory(null).newGetter(DbObject.Type.class, columnKey3).get(row));
    }
    @Test
    public void testEnumGetterOnUnspecifiedStringType() throws Exception {
        assertEquals(DbObject.Type.type2, new RowGetterFactory(null).newGetter(DbObject.Type.class, columnKey4).get(row));
    }


    private DatastaxColumnKey columnKey(DataType type) {
        return new DatastaxColumnKey(columnKey.getName(), columnKey.getIndex(), type);
    }
    private DatastaxColumnKey columnKey(int index, DataType type) {
        return new DatastaxColumnKey(columnKey.getName(), index, type);
    }

}