package org.simpleflatmapper.sql2o.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.sql2o.SfmResultSetHandlerFactoryBuilder;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.sql2o.Query;
import org.sql2o.Sql2o;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
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


    //IFJAVA8_START
    @Test
    public void testDiscriminator608() throws SQLException {
        Connection connection = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);
        if ( connection == null) return;
        try {
            SingleConnectionDataSource scds = new SingleConnectionDataSource(connection, true);
            Sql2o sql2o = new Sql2o(scds);

            Query query = sql2o.open().createQuery("with t(id, type, name) as (values(1, 's', 'solar'), (2, 'e', 'electric')) select * from t" +
                    "");
            query.setAutoDeriveColumnNames(true);

            JdbcMapperFactory jdbcMapperFactory = JdbcMapperFactory
                    .newInstance()
                    .discriminator(Device.class,
                            "type",
                            ResultSet::getString,
                            b ->
                                    b.when("e", ElectricDevice.class)
                                            .when("s", SolarDevice.class));

            query.setResultSetHandlerFactoryBuilder(new SfmResultSetHandlerFactoryBuilder(jdbcMapperFactory));


            List<Device> devices = query.executeAndFetch(Device.class);


            assertEquals(2, devices.size());

            assertEquals(new SolarDevice(1, "s", "solar"), devices.get(0));
            assertEquals(new ElectricDevice(2, "e", "electric"), devices.get(1));



        } finally {
            connection.close();
        }
    }
    //IFJAVA8_END


    public static abstract class Device {
        public final int id;
        public final String type;
        public final String name;

        protected Device(int id, String type, String name) {
            this.id = id;
            this.type = type;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Device device = (Device) o;

            if (id != device.id) return false;
            if (type != null ? !type.equals(device.type) : device.type != null) return false;
            return name != null ? name.equals(device.name) : device.name == null;
        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + (type != null ? type.hashCode() : 0);
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }
    }

    public static class ElectricDevice extends Device {
        public ElectricDevice(int id, String type, String name) {
            super(id, type, name);
        }



    }

    public static class SolarDevice extends Device {
        public SolarDevice(int id, String type, String name) {
            super(id, type, name);
        }
    }

}
