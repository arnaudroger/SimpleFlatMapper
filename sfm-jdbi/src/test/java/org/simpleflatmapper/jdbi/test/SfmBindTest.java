package org.simpleflatmapper.jdbi.test;

import org.junit.Test;
import org.simpleflatmapper.jdbi.SfmBind;
import org.simpleflatmapper.jdbi.SfmResultSetMapperFactory;
import org.simpleflatmapper.jdbi.SqlType;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import java.sql.Types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class SfmBindTest {


    @Test
    public void testBindDbObject() throws Exception {
        DBI dbi = new DBI(DbHelper.getHsqlDataSource());
        //dbi.registerMapper(new SfmResultSetMapperFactory());

        Handle handle = dbi.open();

        try {
            SfmBindExample attach = handle.attach(SfmBindExample.class);

            DbObject dbObject1 = DbObject.newInstance();
            DbObject dbObject2 = DbObject.newInstance();
            checkObjectNotThere(handle, dbObject1);
            checkObjectNotThere(handle, dbObject2);

            attach.insert(dbObject1);

            checkObjectInserted(attach, handle, dbObject1);

            attach.insert(dbObject2);

            checkObjectInserted(attach, handle, dbObject2);


        } finally {
            handle.close();
        }
    }

    public void checkObjectNotThere(Handle handle, DbObject dbObject) {
        assertFalse(
                handle.createQuery("SELECT 1 from TEST_DB_OBJECT WHERE id = :id")
                .bind("id", dbObject.getId())
                .map(Integer.class)
                .list().size() == 1);
    }

    public void checkObjectInserted(SfmBindExample attach, Handle handle, DbObject dbObject) {
        DbObject o = attach.selectOne(dbObject.getId());

        assertEquals(dbObject, o);
    }

    @RegisterMapperFactory(value = {SfmResultSetMapperFactory.class})
    public interface SfmBindExample
    {
        @SqlUpdate("insert into TEST_DB_OBJECT (id, name, email, creation_time, type_ordinal, type_name) values (:id, :name, :email, :creation_time, :type_ordinal, :type_name)")
        void insert(@SfmBind(sqlTypes = {@SqlType(name ="type_ordinal", type=Types.NUMERIC)}) DbObject s);

        @SqlQuery("SELECT * FROM TEST_DB_OBJECT where id = :id")
        DbObject selectOne(@Bind("id") long id);

    }


}
