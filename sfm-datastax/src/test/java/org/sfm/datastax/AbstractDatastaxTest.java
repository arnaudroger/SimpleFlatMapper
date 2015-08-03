package org.sfm.datastax;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Session;
import org.cassandraunit.AbstractCassandraUnit4TestCase;
import org.cassandraunit.dataset.DataSet;
import org.cassandraunit.dataset.yaml.ClassPathYamlDataSet;

import java.net.InetSocketAddress;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class AbstractDatastaxTest extends AbstractCassandraUnit4TestCase {
    @Override
    public DataSet getDataSet() {
        return new ClassPathYamlDataSet("dbObjectDataset.yaml");
    }

    protected void testInSession(Callback callback) throws Exception {
        Cluster cluster = null;
        try {
            cluster =
                    Cluster
                            .builder()
                            .addContactPointsWithPorts(
                                    Arrays.asList(new InetSocketAddress("localhost", 9142)))
                            .build();
            Metadata metadata = cluster.getMetadata();

            assertEquals("Test Cluster", metadata.getClusterName());


            Session session =  null;

            try {
                session = cluster.connect("sfm");

                KeyspaceMetadata sfm = cluster.getMetadata().getKeyspace("sfm");
                if (sfm.getTable("dbobjects") == null) {
                    session.execute("create table dbobjects (" +
                            "id bigint primary key, " +
                            "name varchar, " +
                            "email varchar," +
                            "creation_time timestamp," +
                            "type_ordinal int," +
                            "type_name varchar)");

                    session.execute("insert into dbobjects(id, name, email, creation_time, type_ordinal, type_name) values(1, 'Arnaud Roger', 'arnaud.roger@gmail.com', '2012-10-2 12:10:10', 1, 'type3')");
                }

                if (sfm.getTable("dbobjects_set") == null) {
                    session.execute("create table dbobjects_set(id bigint primary key, emails set<text>)");
                    session.execute("insert into dbobjects_set(id, emails) values(1, {'a@a', 'b@b'})");
                }

                callback.call(session);
            } finally {
                if (session != null) session.close();
            }

        } finally {
            if (cluster != null)
                cluster.close();
        }
    }

    interface Callback {
        void call(Session session) throws Exception;
    }
}
