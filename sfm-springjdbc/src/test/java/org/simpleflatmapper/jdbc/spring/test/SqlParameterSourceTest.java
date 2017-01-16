package org.simpleflatmapper.jdbc.spring.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.simpleflatmapper.jdbc.spring.SqlParameterSourceFactory;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.map.property.ConstantValueProperty;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.sql.Types;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class SqlParameterSourceTest {

    @Test
    public void testParseSql() {
        String sql = "INSERT INTO table VALUES(:id, :name, :email)";

        SqlParameterSourceFactory<DbObject> sqlParameters =
                JdbcTemplateMapperFactory.newInstance().newSqlParameterSourceFactory(DbObject.class, sql);

        testMapping(sqlParameters);
    }

    protected void testMapping(SqlParameterSourceFactory<DbObject> sqlParameterSourceFactory) {
        DbObject dbObject = getDbObject();

        SqlParameterSource parameterSource = sqlParameterSourceFactory.newSqlParameterSource(dbObject);

        assertEquals(12345l, parameterSource.getValue("id"));
        assertEquals("name", parameterSource.getValue("name"));
        assertEquals("email", parameterSource.getValue("email"));

        assertEquals(Types.BIGINT, parameterSource.getSqlType("id"));
        assertEquals(Types.VARCHAR, parameterSource.getSqlType("name"));

        assertEquals(null, parameterSource.getTypeName("id"));
        assertEquals(null, parameterSource.getTypeName("name"));
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

    @Test
    public void testAlias(){
        SqlParameterSourceFactory<DbObject> parameterSourceFactory =
                JdbcTemplateMapperFactory.newInstance().addAlias("e", "email").newSqlParameterSourceFactory(DbObject.class);
        DbObject dbObject = getDbObject();

        SqlParameterSource parameterSource = parameterSourceFactory.newSqlParameterSource(dbObject);

        assertEquals(12345l, parameterSource.getValue("id"));
        assertEquals("name", parameterSource.getValue("name"));
        assertEquals("email", parameterSource.getValue("e"));
        assertEquals("email", parameterSource.getValue("email"));

    }


    @Test
    public void testSource() {
        SqlParameterSourceFactory<DbObject> parameterSourceFactory =
                JdbcTemplateMapperFactory.newInstance().newSqlParameterSourceFactory(DbObject.class);

        DbObject[] dbObjects = new DbObject[10];
        for(int i = 0; i < dbObjects.length; i++ ) {
            dbObjects[i] = new DbObject();
            dbObjects[i].setId(i);
        }

        validate(parameterSourceFactory.newSqlParameterSources(dbObjects));
        validate(parameterSourceFactory.newSqlParameterSources(Arrays.asList(dbObjects)));

    }

    private void validate(SqlParameterSource[] sqlParameterSources) {
        for(int i = 0; i < sqlParameterSources.length; i++) {
            assertEquals((long)i, sqlParameterSources[i].getValue("id"));
        }
    }

    @Test
    public void testConstantValue() {
        SqlParameterSourceFactory<DbObject> parameterSourceFactory =
                JdbcTemplateMapperFactory
                        .newInstance()
                        .addColumnProperty("id", new ConstantValueProperty<Long>(-3l, Long.class))
                        .newSqlParameterSourceFactory(DbObject.class);

        SqlParameterSource parameterSource = parameterSourceFactory.newSqlParameterSource(new DbObject());

        assertEquals(-3l, parameterSource.getValue("id"));

    }
}
