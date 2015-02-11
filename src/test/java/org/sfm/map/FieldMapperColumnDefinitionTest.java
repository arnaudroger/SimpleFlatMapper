package org.sfm.map;

import org.junit.Test;
import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.map.impl.FieldMapper;
import org.sfm.map.impl.FieldMapperColumnDefinition;
import org.sfm.reflect.Getter;

import java.lang.reflect.Type;
import java.sql.ResultSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FieldMapperColumnDefinitionTest {


    @Test
    public void testCompose() throws Exception {
        GetterFactory<ResultSet, JdbcColumnKey> getterFactory = new GetterFactory<ResultSet, JdbcColumnKey>() {
            @Override
            public <P> Getter<ResultSet, P> newGetter(Type target, JdbcColumnKey key) {
                return null;
            }
        };
        Getter<ResultSet, Integer> getter = new Getter<ResultSet, Integer>() {
            @Override
            public Integer get(ResultSet target) throws Exception {
                return 3;
            }
        };
        FieldMapper<ResultSet, Object> fieldMapper = new FieldMapper<ResultSet, Object>() {
            @Override
            public void map(ResultSet source, Object target) throws Exception {
            }
        };
        FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> compose =
                FieldMapperColumnDefinition.<JdbcColumnKey,ResultSet>identity().addRename("blop").addGetter(getter).addFieldMapper(fieldMapper).addGetterFactory(getterFactory);

        assertEquals("blop", compose.rename(new JdbcColumnKey("bar", -1)).getName());
        assertEquals(fieldMapper, compose.getCustomFieldMapper());
        assertEquals(getterFactory, compose.getCustomGetterFactory());
        assertEquals(new Integer(3), compose.getCustomGetter().get(null));

        assertTrue(compose.hasCustomSource());
        assertTrue(compose.hasCustomFactory());
        assertFalse(compose.ignore());
        assertEquals(Integer.class, compose.getCustomSourceReturnType());

        assertTrue(FieldMapperColumnDefinition.<JdbcColumnKey,ResultSet>identity().addIgnore().ignore());
    }
}
