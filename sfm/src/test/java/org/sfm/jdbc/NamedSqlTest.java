package org.sfm.jdbc;

import org.junit.Test;

import static org.junit.Assert.*;

public class NamedSqlTest {

    @Test
    public void testParse() throws Exception {
        NamedSql namedSql = NamedSql.parse("INSERT INTO TABLE VALUES(:id, :name, :email);");

    }
}