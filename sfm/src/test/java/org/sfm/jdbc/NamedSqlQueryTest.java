package org.sfm.jdbc;

import org.junit.Test;
import org.sfm.jdbc.named.NamedSqlQuery;

public class NamedSqlQueryTest {

    @Test
    public void testParse() throws Exception {
        NamedSqlQuery namedSqlQuery = NamedSqlQuery.parse("INSERT INTO TABLE VALUES(:id, :name, :email);");

    }
}