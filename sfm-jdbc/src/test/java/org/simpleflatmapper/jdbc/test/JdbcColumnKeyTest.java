package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcColumnKey;

import java.sql.Types;

import static org.junit.Assert.*;

public class JdbcColumnKeyTest {

    @Test
    public void testHashCode() throws Exception {
        assertEquals(new JdbcColumnKey("col", 1, Types.ARRAY).hashCode(), new JdbcColumnKey("col", 1, Types.ARRAY).hashCode());
        assertNotEquals(new JdbcColumnKey("col", 1, Types.ARRAY).hashCode(), new JdbcColumnKey("col", 1, Types.VARCHAR).hashCode());
        assertNotEquals(new JdbcColumnKey("col", 1, Types.ARRAY).hashCode(), new JdbcColumnKey("col", 2, Types.ARRAY).hashCode());
        assertNotEquals(new JdbcColumnKey("col", 1, Types.ARRAY).hashCode(), new JdbcColumnKey("col1", 1, Types.ARRAY).hashCode());
    }

    @Test
    public void testEquals() throws Exception {
        assertEquals(new JdbcColumnKey("col", 1, Types.ARRAY), new JdbcColumnKey("col", 1, Types.ARRAY));
        assertNotEquals(new JdbcColumnKey("col", 1, Types.ARRAY), new JdbcColumnKey("col", 1, Types.VARCHAR));
        assertNotEquals(new JdbcColumnKey("col", 1, Types.ARRAY), new JdbcColumnKey("col", 2, Types.ARRAY));
        assertNotEquals(new JdbcColumnKey("col", 1, Types.ARRAY), new JdbcColumnKey("col1", 1, Types.ARRAY));


    }

    @Test
    public void testAlias() throws Exception {
        assertNotNull(new JdbcColumnKey("col", 1, Types.ARRAY).alias("h").getParent());
    }

    @Test
    public void testToString() throws Exception {
        assertEquals("ColumnKey [columnName=col, columnIndex=1, sqlType=2003]", new JdbcColumnKey("col", 1, Types.ARRAY).toString());
    }
}