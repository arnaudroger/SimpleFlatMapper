package org.simpleflatmapper.jdbc.spring.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.SqlTypeColumnProperty;
import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.simpleflatmapper.jdbc.spring.SqlParameterSourceFactory;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.map.property.ConstantValueProperty;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Types;

//IFJAVA8_START
import java.time.Instant;
import java.time.ZonedDateTime;
//IFJAVA8_END
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class SqlParameterSourceTest {

    //IFJAVA8_START
    @Test
    public void testInstantShouldBeTimestampType() {
        String sql = "INSERT INTO table VALUES(:instant)";

        SqlParameterSourceFactory<ObjectWithInstant> sqlParameters =
                JdbcTemplateMapperFactory
                        .newInstance()
                        .newSqlParameterSourceFactory(ObjectWithInstant.class, sql);

        
        ObjectWithInstant data = new ObjectWithInstant(Instant.now());
        
        SqlParameterSource parameterSource = sqlParameters.newSqlParameterSource(data);

        assertEquals(Types.TIMESTAMP, parameterSource.getSqlType("instant"));
        assertEquals(new Timestamp(data.instant.toEpochMilli()), parameterSource.getValue("instant"));

    }

    @Test
    public void testSpecifySqlType() {
        String sql = "INSERT INTO table VALUES(:instant)";

        SqlParameterSourceFactory<ObjectWithObject> sqlParameters =
                JdbcTemplateMapperFactory
                        .newInstance()
                        .addColumnProperty("instant", SqlTypeColumnProperty.of(Types.TIMESTAMP))
                        .newSqlParameterSourceFactory(ObjectWithObject.class, sql);


        ObjectWithObject data = new ObjectWithObject(Instant.now());

        SqlParameterSource parameterSource = sqlParameters.newSqlParameterSource(data);

        assertEquals(Types.TIMESTAMP, parameterSource.getSqlType("instant"));
        assertEquals(new Timestamp(((Instant)data.instant).toEpochMilli()), parameterSource.getValue("instant"));

    }
    
    @Test
    public void testIssue589() {
        SqlParameterSourceFactory<Issue589> parameterSourceFactory = JdbcTemplateMapperFactory.newInstance()
                .ignorePropertyNotFound()
                .addAlias("timestamp_", "timestamp")
                .addColumnProperty("timestamp_", SqlTypeColumnProperty.of(Types.TIMESTAMP))
                .newSqlParameterSourceFactory(Issue589.class);
        
        ZonedDateTime now = ZonedDateTime.now();
        Issue589 issue589 = new Issue589(now);

        SqlParameterSource sqlParameterSource = parameterSourceFactory.newSqlParameterSource(issue589);
        
        assertEquals(Types.TIMESTAMP, sqlParameterSource.getSqlType("timestamp_"));
        assertEquals(new Timestamp(Date.from(now.toInstant()).getTime()), sqlParameterSource.getValue("timestamp_"));
    }
    
    public static class Issue589 {
        public final ZonedDateTime timestamp;

        public Issue589(ZonedDateTime timestamp) {
            this.timestamp = timestamp;
        }
    }
    
    public class ObjectWithInstant {
        public final Instant instant;

        public ObjectWithInstant(Instant instant) {
            this.instant = instant;
        }
    }

    public class ObjectWithObject {
        public final Object instant;

        public ObjectWithObject(Object instant) {
            this.instant = instant;
        }
    }
    //IFJAVA8_END
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


    @Test
    public void test591_MapStrategy() {
        String sql = "INSERT INTO table VALUES(:id, :1, :2, :3, :4, :5, :6, :7, :8, :9, :unknown)";

        SqlParameterSourceFactory<DbObject> sqlParameters =
                JdbcTemplateMapperFactory
                        .newInstance()
                        .ignorePropertyNotFound()
                        .newSqlParameterSourceFactory(DbObject.class, sql);


        DbObject dbObject = DbObject.newInstance();

        SqlParameterSource parameterSource = sqlParameters.newSqlParameterSource(dbObject);

        assertEquals(dbObject.getId(), parameterSource.getValue("id"));
        assertFalse(parameterSource.hasValue("unknown"));
        
    }

    @Test
    public void test591_ArrayStrategy() {
        String sql = "INSERT INTO table VALUES(:id, :unknown)";

        SqlParameterSourceFactory<DbObject> sqlParameters =
                JdbcTemplateMapperFactory
                        .newInstance()
                        .ignorePropertyNotFound()
                        .newSqlParameterSourceFactory(DbObject.class, sql);


        DbObject dbObject = DbObject.newInstance();

        SqlParameterSource parameterSource = sqlParameters.newSqlParameterSource(dbObject);

        assertEquals(dbObject.getId(), parameterSource.getValue("id"));
        assertFalse(parameterSource.hasValue("unknown"));

    }
    
    @Test
    public void test595() {
        SqlParameterSourceFactory<DbObject> sqlParameters =
                JdbcTemplateMapperFactory
                        .newInstance()
                        .ignorePropertyNotFound()
                        .newSqlParameterSourceFactory(DbObject.class);

        DbObject dbObject = DbObject.newInstance();

        SqlParameterSource parameterSource = sqlParameters.newSqlParameterSource(dbObject);

        assertEquals(dbObject.getId(), parameterSource.getValue("id"));
        assertFalse(parameterSource.hasValue("unknown"));
        assertFalse(parameterSource.hasValue("foo"));
    }
    
    public static class I591 {
        private final long id;

        public I591(long id) {
            this.id = id;
        }
    }
}
