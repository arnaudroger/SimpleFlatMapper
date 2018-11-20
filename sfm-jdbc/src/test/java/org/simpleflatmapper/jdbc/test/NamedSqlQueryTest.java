package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.named.NamedSqlQuery;

import static org.junit.Assert.assertNotNull;

public class NamedSqlQueryTest {

    @Test
    public void testParse() throws Exception {
        NamedSqlQuery namedSqlQuery = NamedSqlQuery.parse("INSERT INTO TABLE VALUES(:id, :name, :email);");
        assertNotNull(namedSqlQuery);

    }
}