package org.simpleflatmapper.jdbi;

import org.junit.Test;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

public class ResultSetMapperFactoryTest {


    @Test
    public void testMapToDbObject() throws Exception {
        DBI dbi = new DBI(DbHelper.getHsqlDataSource());
        dbi.registerMapper(new SfmResultSetMapperFactory());
        Handle handle = dbi.open();
        try {
            DbObject dbObject = handle.createQuery(DbHelper.TEST_DB_OBJECT_QUERY).mapTo(DbObject.class).first();
            DbHelper.assertDbObjectMapping(dbObject);
        } finally {
            handle.close();
        }
    }
}
