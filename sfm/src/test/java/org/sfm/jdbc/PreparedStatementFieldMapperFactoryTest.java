package org.sfm.jdbc;

import org.junit.Before;
import org.junit.Test;
import org.sfm.map.FieldMapper;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.mapper.PropertyMapping;
import org.sfm.reflect.Getter;
import org.sfm.reflect.impl.NullGetter;
import org.sfm.reflect.impl.StaticBooleanGetter;
import org.sfm.reflect.impl.StaticByteGetter;
import org.sfm.reflect.impl.StaticGetter;
import org.sfm.reflect.meta.PropertyMeta;

import java.sql.PreparedStatement;
import java.sql.Types;

import static org.mockito.Mockito.*;

public class PreparedStatementFieldMapperFactoryTest {

    private PreparedStatementFieldMapperFactory factory;

    private PreparedStatement ps;

    private int index;
    @Before
    public void setUp() {
        factory = PreparedStatementFieldMapperFactory.instance();
        ps = mock(PreparedStatement.class);
        index = 1;
    }

    @Test
    public void testMapBoolean() throws Exception {
        newFieldMapperAndMapToPS(new StaticBooleanGetter<Object>(true), boolean.class);
        newFieldMapperAndMapToPS(new StaticGetter<Object, Boolean>(false), Boolean.class);
        newFieldMapperAndMapToPS(new NullGetter<Object, Boolean>(), Boolean.class);

        verify(ps).setBoolean(1, true);
        verify(ps).setBoolean(2, false);
        verify(ps).setNull(3, Types.BOOLEAN);
    }

    @Test
    public void testMapByte() throws Exception {
        newFieldMapperAndMapToPS(new StaticByteGetter<Object>((byte)2), byte.class);
        newFieldMapperAndMapToPS(new StaticGetter<Object, Byte>((byte) 3), Byte.class);
        newFieldMapperAndMapToPS(new NullGetter<Object, Byte>(), Byte.class);

        verify(ps).setByte(1, (byte) 2);
        verify(ps).setByte(2, (byte) 3);
        verify(ps).setNull(3, Types.TINYINT);
    }

    @Test
    public void testMapChar() {

    }

    @Test
    public void testMapShort() {

    }

    @Test
    public void testMapInt() {

    }

    @Test
    public void testMapLong() {

    }

    @Test
    public void testMapFloat() {

    }

    @Test
    public void testMapDouble() {

    }

    @Test
    public void testMapDate() {

    }

    @Test
    public void testMapCalendar() {

    }

    @Test
    public void testMapString() {

    }


    @Test
    public void testMapUUID() {

    }

    @Test
    public void testMapURL() {

    }



    protected <T, P> void newFieldMapperAndMapToPS(Getter<T, P> getter, Class<P> clazz) throws Exception {
        FieldMapper<T, PreparedStatement> fieldMapper = factory.newFieldMapperToSource(newPropertyMapping(getter, clazz), null);
        fieldMapper.mapTo(null, ps, null);
    }

    @SuppressWarnings("unchecked")
    private <T, P> PropertyMapping<T, P, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>> newPropertyMapping(Getter<T, P> getter, Class<P> clazz) {
        PropertyMeta<T, P> propertyMeta = mock(PropertyMeta.class);
        when(propertyMeta.getGetter()).thenReturn(getter);
        when(propertyMeta.getPropertyType()).thenReturn(clazz);
        return
                new PropertyMapping<T, P, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>>(
                        propertyMeta,
                        new JdbcColumnKey("col", index++),
                        FieldMapperColumnDefinition.<JdbcColumnKey>identity());
    }






}