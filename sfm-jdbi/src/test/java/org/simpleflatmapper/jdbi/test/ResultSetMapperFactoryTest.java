package org.simpleflatmapper.jdbi.test;

import org.junit.Test;
import org.simpleflatmapper.jdbi.SfmResultSetMapperFactory;
import org.simpleflatmapper.map.SourceFieldMapper;
import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.simpleflatmapper.util.UnaryFactory;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.sql.ResultSet;

import static org.junit.Assert.assertTrue;

public class ResultSetMapperFactoryTest {


    @Test
    public void testMapToDbObject() throws Exception {
        DBI dbi = new DBI(DbHelper.getHsqlDataSource());
        dbi.registerMapper(new SfmResultSetMapperFactory());
        Handle handle = dbi.open();
        try {
            DbObject dbObject = handle.createQuery(DbHelper.TEST_DB_OBJECT_QUERY).mapTo(DbObject.class).first();
            DbHelper.assertDbObjectMapping(dbObject);

            SfmBindTest.SfmBindExample attach = handle.attach(SfmBindTest.SfmBindExample.class);
            attach.insert(DbObject.newInstance());
            assertTrue(handle.createQuery("select * from TEST_DB_OBJECT").mapTo(DbObject.class).list().size() > 1);
        } finally {
            handle.close();
        }
    }


    @Test
    public void testMapToDbObjectStatic() throws Exception {
        DBI dbi = new DBI(DbHelper.getHsqlDataSource());
        dbi.registerMapper(new SfmResultSetMapperFactory(new UnaryFactory<Class<?>, SourceMapper< ResultSet, ?>>() {

            @Override
            public SourceFieldMapper<ResultSet, ?> newInstance(Class<?> aClass) {
                return new SourceFieldMapper<ResultSet, DbObject>() {
                    @Override
                    public DbObject map(ResultSet source) throws MappingException {
                        return map(source, null);
                    }

                    @Override
                    public DbObject map(ResultSet source, MappingContext<? super ResultSet> context) throws MappingException {
                        DbObject dbObject = new DbObject();
                        try {
                            mapTo(source, dbObject, context);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return dbObject;
                    }

                    @Override
                    public void mapTo(ResultSet source, DbObject target, MappingContext<? super ResultSet> context) throws Exception {
                        target.setId(source.getInt("id"));
                        target.setCreationTime(source.getTimestamp("creation_time"));
                        target.setEmail(source.getString("email"));
                        target.setName(source.getString("name"));
                        String type_name = source.getString("type_name");
                        if (type_name != null) {
                            target.setTypeName(DbObject.Type.valueOf(type_name));
                        }
                        target.setTypeOrdinal(DbObject.Type.values()[source.getInt("type_ordinal")]);
                    }
                };
            }
        } ));
        Handle handle = dbi.open();
        try {
            DbObject dbObject = handle.createQuery(DbHelper.TEST_DB_OBJECT_QUERY).mapTo(DbObject.class).first();
            DbHelper.assertDbObjectMapping(dbObject);

            SfmBindTest.SfmBindExample attach = handle.attach(SfmBindTest.SfmBindExample.class);
            attach.insert(DbObject.newInstance());
            assertTrue(handle.createQuery("select * from TEST_DB_OBJECT").mapTo(DbObject.class).list().size() > 1);
        } finally {
            handle.close();
        }
    }
}
