package org.simpleflatmapper.jdbi.test;

import org.junit.Test;
import org.simpleflatmapper.jdbi.SfmResultSetMapperFactory;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

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
}
