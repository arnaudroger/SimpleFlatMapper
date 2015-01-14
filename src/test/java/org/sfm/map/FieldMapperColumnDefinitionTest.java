package org.sfm.map;

import org.junit.Test;
import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.map.impl.FieldMapperColumnDefinition;
import org.sfm.reflect.Getter;

import java.sql.ResultSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FieldMapperColumnDefinitionTest {


    @Test
    public void testCompose() throws Exception {
        Getter<ResultSet, Integer> getter = new Getter<ResultSet, Integer>() {
            @Override
            public Integer get(ResultSet target) throws Exception {
                return 3;
            }
        };
        FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> compose =
                FieldMapperColumnDefinition.compose(
                        FieldMapperColumnDefinition.<JdbcColumnKey, ResultSet>renameDefinition("blop"),
                        FieldMapperColumnDefinition.<JdbcColumnKey, ResultSet>customGetter(getter));

        assertEquals("blop", compose.rename(new JdbcColumnKey("bar", -1)).getName());
        assertEquals(new Integer(3), compose.getCustomGetter().get(null));

        assertTrue(compose.hasCustomSource());
        assertEquals(Integer.class, compose.getCustomSourceReturnType());
    }
}
