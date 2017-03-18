package org.simpleflatmapper.jdbc.test.impl;

import org.junit.Test;
import org.simpleflatmapper.jdbc.impl.DatabaseMeta;

import static org.junit.Assert.*;

public class DatabaseMetaTest {
    @Test
    public void isMysql() throws Exception {
        assertFalse(new DatabaseMeta("foo", 3, 4).isMysql());
        assertTrue(new DatabaseMeta("MySQL", 3, 4).isMysql());


    }

    @Test
    public void isPostgresSql() throws Exception {
        assertFalse(new DatabaseMeta("foo", 3, 4).isPostgresSql());
        assertTrue(new DatabaseMeta("PostgreSQL", 3, 4).isPostgresSql());
    }

    @Test
    public void isVersionMet() throws Exception {
        DatabaseMeta databaseMeta = new DatabaseMeta("foo", 3, 4);
        assertTrue(databaseMeta.isVersionMet(2, 9));
        assertTrue(databaseMeta.isVersionMet(3, 3));
        assertTrue(databaseMeta.isVersionMet(3, 4));
        assertFalse(databaseMeta.isVersionMet(3, 5));
        assertFalse(databaseMeta.isVersionMet(4, 1));
    }

}