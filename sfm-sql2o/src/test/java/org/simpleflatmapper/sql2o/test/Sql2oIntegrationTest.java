package org.simpleflatmapper.sql2o.test;

import org.junit.Test;
import org.simpleflatmapper.sql2o.SfmResultSetHandlerFactoryBuilder;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.sql2o.Query;
import org.sql2o.Sql2o;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class Sql2oIntegrationTest {


    @Test
    public void testSql2O() throws SQLException, ParseException {
        Connection connection = DbHelper.objectDb();
        try {
            SingleConnectionDataSource scds = new SingleConnectionDataSource(connection, true);
            Sql2o sql2o = new Sql2o(scds);

            Query query = sql2o.open().createQuery(DbHelper.TEST_DB_OBJECT_QUERY);
            query.setAutoDeriveColumnNames(true);
            query.setResultSetHandlerFactoryBuilder(new SfmResultSetHandlerFactoryBuilder());

            List<DbObject> dbObjects = query.executeAndFetch(DbObject.class);

            assertEquals(1, dbObjects.size());
            DbHelper.assertDbObjectMapping(dbObjects.get(0));

        } finally {
            connection.close();
        }
    }
}
