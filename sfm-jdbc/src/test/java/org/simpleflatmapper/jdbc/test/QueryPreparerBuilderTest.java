package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.jdbc.PreparedStatementMapperBuilder;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.map.property.GetterProperty;
import org.simpleflatmapper.map.property.ConstantValueProperty;
import org.simpleflatmapper.reflect.Getter;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class QueryPreparerBuilderTest {

    @Test
    public void testMapDbObjectToStatement() throws Exception {
        FieldMapper<DbObject, PreparedStatement> mapper =
                JdbcMapperFactory.newInstance().buildFrom(DbObject.class)
                        .addColumn("id")
                        .addColumn("name")
                        .addColumn("email")
                        .addColumn("creation_time")
                        .mapper();

        PreparedStatement ps = mock(PreparedStatement.class);

        DbObject dbObject = new DbObject();
        dbObject.setId(123);
        dbObject.setName("name");
        dbObject.setEmail("email");
        dbObject.setCreationTime(new Date());

        mapper.mapTo(dbObject, ps, null);

        verify(ps).setLong(1, 123);
        verify(ps).setString(2, "name");
        verify(ps).setString(3, "email");
        verify(ps).setTimestamp(4, new Timestamp(dbObject.getCreationTime().getTime()));
    }


    public static class DMClass {
        private Date time;

        public Date getTime() {
            return time;
        }

        public void setTime(Date time) {
            this.time = time;
        }

        public String getValue() {
            return "value";
        }
    }

    @Test
    public void testDirectMeta() throws Exception {
        PreparedStatementMapperBuilder<DMClass> mapperBuilder = JdbcMapperFactory.newInstance().buildFrom(DMClass.class);
        mapperBuilder.addColumn("time");

        FieldMapper<DMClass, PreparedStatement> mapper = mapperBuilder.mapper();

        DMClass dmClass = new DMClass();
        dmClass.setTime(new Date());

        PreparedStatement ps = mock(PreparedStatement.class);

        mapper.mapTo(dmClass, ps, null);

        verify(ps).setTimestamp(1, new Timestamp(dmClass.getTime().getTime()));
    }
    @Test
    public void testGetterOnlyMethod() throws Exception {
        PreparedStatementMapperBuilder<DMClass> mapperBuilder = JdbcMapperFactory.newInstance().buildFrom(DMClass.class);
        mapperBuilder.addColumn("value");

        FieldMapper<DMClass, PreparedStatement> mapper = mapperBuilder.mapper();

        DMClass dmClass = new DMClass();

        PreparedStatement ps = mock(PreparedStatement.class);

        mapper.mapTo(dmClass, ps, null);

        verify(ps).setString(1, "value");
    }

    @Test
    public void testRename() throws Exception {

        PreparedStatementMapperBuilder<DMClass> mapperBuilder = JdbcMapperFactory.newInstance()
                .addAlias("val", "value")
                .buildFrom(DMClass.class);
        mapperBuilder.addColumn("val");

        FieldMapper<DMClass, PreparedStatement> mapper = mapperBuilder.mapper();

        DMClass dmClass = new DMClass();

        PreparedStatement ps = mock(PreparedStatement.class);

        mapper.mapTo(dmClass, ps, null);

        verify(ps).setString(1, "value");

    }


    @Test
         public void testCustomGetter() throws Exception {

        PreparedStatementMapperBuilder<DMClass> mapperBuilder = JdbcMapperFactory.newInstance()
                .addColumnProperty("value", new GetterProperty(new Getter<Object, String>() {
                    @Override
                    public String get(Object target) throws Exception {
                        return "value2";
                    }
                }))
                .buildFrom(DMClass.class);
        mapperBuilder.addColumn("value");

        FieldMapper<DMClass, PreparedStatement> mapper = mapperBuilder.mapper();

        DMClass dmClass = new DMClass();

        PreparedStatement ps = mock(PreparedStatement.class);

        mapper.mapTo(dmClass, ps, null);

        verify(ps).setString(1, "value2");

    }

    @Test
    public void testCustomGetterOnNonExistantProp() throws Exception {
        PreparedStatementMapperBuilder<Object> mapperBuilder = JdbcMapperFactory.newInstance()
                .addColumnProperty("text", new ConstantValueProperty<String>("value2", String.class))
                .buildFrom(Object.class);
        mapperBuilder.addColumn("text");

        FieldMapper<Object, PreparedStatement> mapper = mapperBuilder.mapper();


        PreparedStatement ps = mock(PreparedStatement.class);

        mapper.mapTo(new Object(), ps, null);

        verify(ps).setString(1, "value2");

    }
}