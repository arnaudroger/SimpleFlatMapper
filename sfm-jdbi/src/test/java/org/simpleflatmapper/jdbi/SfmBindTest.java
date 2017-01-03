package org.simpleflatmapper.jdbi;

import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.sql.Types;

import static org.junit.Assert.assertEquals;

public class SfmBindTest {


    @Test
    public void testBindDbObject() throws Exception {
        DBI dbi = new DBI(DbHelper.getHsqlDataSource());
        dbi.registerMapper(new SfmResultSetMapperFactory());

        Handle handle = dbi.open();

        try {
            SfmBindExample attach = handle.attach(SfmBindExample.class);

            DbObject dbObject = DbObject.newInstance();

            attach.insert(dbObject);

            checkObjectInserted(handle, dbObject);

        } finally {
            handle.close();
        }
    }

    public void checkObjectInserted(Handle handle, DbObject dbObject) {
        SfmResultSetMapper<DbObject> resultSetMapper = new SfmResultSetMapper<DbObject>(JdbcMapperFactory.newInstance().newMapper(DbObject.class));
        DbObject o = handle.createQuery("SELECT * from TEST_DB_OBJECT WHERE id = :id")
                .bind("id", dbObject.getId())
                .map(resultSetMapper)
                .first();

        assertEquals(dbObject, o);
    }

    public interface SfmBindExample
    {
        @SqlUpdate("insert into TEST_DB_OBJECT (id, name, email, creation_time, type_ordinal, type_name) values (:id, :name, :email, :creation_time, :type_ordinal, :type_name)")
        void insert(@SfmBind(sqlTypes = {@SqlType(name ="type_ordinal", type=Types.NUMERIC)}) DbObject s);

    }


}
