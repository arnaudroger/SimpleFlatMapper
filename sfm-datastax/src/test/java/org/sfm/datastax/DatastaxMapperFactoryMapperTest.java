package org.sfm.datastax;

import com.datastax.driver.core.*;
import org.cassandraunit.AbstractCassandraUnit4TestCase;
import org.cassandraunit.CassandraUnit;
import org.cassandraunit.dataset.DataSet;
import org.cassandraunit.dataset.json.ClassPathJsonDataSet;
import org.cassandraunit.dataset.yaml.ClassPathYamlDataSet;
import org.junit.Test;
import org.sfm.beans.DbObject;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Iterator;

import static org.junit.Assert.*;


public class DatastaxMapperFactoryMapperTest extends AbstractCassandraUnit4TestCase {

    @Override
    public DataSet getDataSet() {
        return new ClassPathYamlDataSet("dbObjectDataset.yaml");
    }


    @Test
    public void testConnectWithDatastax() throws Exception {
        Cluster cluster = null;
        try {
            cluster =
                Cluster
                    .builder()
                    .addContactPointsWithPorts(
                            Arrays.asList(new InetSocketAddress(InetAddress.getLoopbackAddress(), 9142)))
                        .build();
            Metadata metadata = cluster.getMetadata();

            assertEquals("Test Cluster", metadata.getClusterName());

            for(KeyspaceMetadata km : metadata.getKeyspaces()) {
                if (!"system".equals(km.getName()) && !"system_traces".equals(km.getName())) {
                    for (TableMetadata t : km.getTables()) {
                        System.out.println(km.getName() + ":" + t.getName());
                    }
                }
            }

            Session session =  null;

            try {
                session = cluster.connect("sfm");

                if (cluster.getMetadata().getKeyspace("sfm").getTable("dbobjects") == null) {
                    session.execute("create table dbobjects (" +
                            "id bigint primary key, " +
                            "name varchar, " +
                            "email varchar," +
                            "creation_time timestamp," +
                            "type_ordinal int," +
                            "type_name varchar)");

                    session.execute("insert into dbobjects(id, name, email, creation_time, type_ordinal, type_name) values(1, 'Arnaud Roger', 'arnaud.roger@gmail.com', '2012-10-2 12:10:10', 1, 'type2')");
                }

                ResultSet rs = session.execute("select id, name, email, creation_time, type_ordinal, type_name from dbobjects");

                final DatastaxMapper<DbObject> mapper = DatastaxMapperFactory.newInstance().newMapper(DbObject.class);

                final Iterator<DbObject> iterator = mapper.iterator(rs);

                final DbObject next = iterator.next();

            } finally {
                if (session != null) session.close();
            }

        } finally {
            if (cluster != null)
                cluster.close();
        }

    }
}