package org.sfm.jdbc.spring;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.reflect.ReflectionService;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import static org.junit.Assert.assertEquals;

public class SqlParameterSourceTest {

    @Test
    public void testParseSql() {
        String sql = "INSERT INTO table VALUES(:id, :name, :email)";

        ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(sql);

        SqlParameterSourceFactory<DbObject> sqlParameters =
                JdbcTemplateMapperFactory.newInstance().newSqlParameterSourceFactory(DbObject.class, parsedSql);

        testMapping(sqlParameters);
    }

    protected void testMapping(SqlParameterSourceFactory<DbObject> sqlParameterSourceFactory) {
        DbObject dbObject = getDbObject();

        SqlParameterSource parameterSource = sqlParameterSourceFactory.newSqlParameterSource(dbObject);

        assertEquals(12345l, parameterSource.getValue("id"));
        assertEquals("name", parameterSource.getValue("name"));
        assertEquals("email", parameterSource.getValue("email"));
    }

    protected DbObject getDbObject() {
        DbObject dbObject = new DbObject();
        dbObject.setId(12345);
        dbObject.setName("name");
        dbObject.setEmail("email");
        return dbObject;
    }


    @Test
    public void testDynamicParams(){
        SqlParameterSourceFactory<DbObject> parameterSourceFactory =
                JdbcTemplateMapperFactory.newInstance().newSqlParameterSourceFactory(DbObject.class);
        testMapping(parameterSourceFactory);
    }
}
