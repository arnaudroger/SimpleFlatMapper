package org.sfm.datastax.impl;

import com.datastax.driver.core.BoundStatement;
import org.junit.Before;
import org.junit.Test;
import org.sfm.datastax.DatastaxColumnKey;
import org.sfm.map.column.ColumnProperty;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.mapper.ColumnDefinition;
import org.sfm.map.mapper.PropertyMapping;
import org.sfm.reflect.Setter;
import org.sfm.reflect.meta.PropertyMeta;
import org.sfm.reflect.primitive.LongSetter;

import java.util.Date;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BoundStatementSetterFactoryTest {


    BoundStatementSetterFactory factory = new BoundStatementSetterFactory();
    private int index;

    BoundStatement statement;
    @Before
    public void setUp() {
        statement = mock(BoundStatement.class);
        index = 0;
    }

    @Test
    public void testLongSetter() throws Exception {
        Setter<BoundStatement, Long> setter = factory.getSetter(newPM(long.class));
        assertTrue(setter instanceof LongSetter);

        setter.set(statement, 3l);
        setter.set(statement, null);

        verify(statement).setLong(0, 3l);
        verify(statement).setToNull(0);
    }


    @Test
    public void testStringSetter() throws Exception {
        Setter<BoundStatement, String> setter = factory.getSetter(newPM(String.class));

        setter.set(statement, "str");
        setter.set(statement, null);

        verify(statement).setString(0, "str");
        verify(statement).setToNull(0);
    }

    @Test
    public void tesDate() throws Exception {
        Setter<BoundStatement, Date> setter = factory.getSetter(newPM(Date.class));

        Date date = new Date();

        setter.set(statement, date);
        setter.set(statement, null);

        verify(statement).setDate(0, date);
        verify(statement).setToNull(0);
    }

    private <T, P> PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> newPM(Class<P> clazz, ColumnProperty... properties) {
        PropertyMeta<T, P> propertyMeta = mock(PropertyMeta.class);
        when(propertyMeta.getPropertyType()).thenReturn(clazz);
        return
                new PropertyMapping<T, P, DatastaxColumnKey, FieldMapperColumnDefinition<DatastaxColumnKey>>(
                        propertyMeta,
                        new DatastaxColumnKey("col", index++),
                        FieldMapperColumnDefinition.<DatastaxColumnKey>of(properties));
    }
}