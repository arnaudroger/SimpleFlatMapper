package org.sfm.jdbc;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.map.Mapper;
import org.sfm.map.column.RenameProperty;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PreparedStatementMapperBuilderTest {

    @Test
    public void testMapDbObjectToStatement() throws Exception {
        Mapper<DbObject, PreparedStatement> mapper =
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
        mapperBuilder.addColumn("time_bucket");

        Mapper<DMClass, PreparedStatement> mapper = mapperBuilder.mapper();

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

        Mapper<DMClass, PreparedStatement> mapper = mapperBuilder.mapper();

        DMClass dmClass = new DMClass();

        PreparedStatement ps = mock(PreparedStatement.class);

        mapper.mapTo(dmClass, ps, null);

        verify(ps).setString(1, "value");
    }

    @Test
    public void testGlobalColumnProperty() throws Exception {

        PreparedStatementMapperBuilder<DMClass> mapperBuilder = JdbcMapperFactory.newInstance()
                .addColumnProperty("val", new RenameProperty("value"))
                .buildFrom(DMClass.class);
        mapperBuilder.addColumn("val");

        Mapper<DMClass, PreparedStatement> mapper = mapperBuilder.mapper();

        DMClass dmClass = new DMClass();

        PreparedStatement ps = mock(PreparedStatement.class);

        mapper.mapTo(dmClass, ps, null);

        verify(ps).setString(1, "value");

    }
}