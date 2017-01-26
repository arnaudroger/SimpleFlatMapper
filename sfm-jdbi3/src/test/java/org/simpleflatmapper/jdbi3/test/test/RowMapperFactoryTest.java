package org.simpleflatmapper.jdbi3.test.test;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.junit.Test;
import org.simpleflatmapper.jdbi3.SfmRowMapperFactory;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.jdbc.DbHelper;


public class RowMapperFactoryTest {


    @Test
    public void testMapToDbObject() throws Exception {
        Jdbi dbi = Jdbi.create(DbHelper.getHsqlDataSource());
        dbi.installPlugins();
        Handle handle = dbi.open();
        try {
            DbObject dbObject = handle.createQuery(DbHelper.TEST_DB_OBJECT_QUERY).mapTo(DbObject.class).findFirst().get();
            DbHelper.assertDbObjectMapping(dbObject);
        } finally {
            handle.close();
        }
    }
}
