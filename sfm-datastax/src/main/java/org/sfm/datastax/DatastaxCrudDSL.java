package org.sfm.datastax;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.TableMetadata;
import org.sfm.datastax.impl.DatastaxCrudFactory;

import java.lang.reflect.Type;

public class DatastaxCrudDSL<T, K> {
    private final DatastaxMapperFactory datastaxMapperFactory;
    private final Type targetType;
    private final Type keyType;

    public DatastaxCrudDSL(Type targetType, Type keyType, DatastaxMapperFactory datastaxMapperFactory) {
        this.targetType = targetType;
        this.keyType = keyType;
        this.datastaxMapperFactory = datastaxMapperFactory;
    }

    public DatastaxCrud<T, K> to(Session session, String table) {
        String keyspace = session.getLoggedKeyspace();

        TableMetadata tableMetadata =
                session.getCluster().getMetadata().getKeyspace(keyspace).getTable(table);



        return DatastaxCrudFactory.<T, K>newInstance(targetType,
                keyType,
                tableMetadata,
                session,
                datastaxMapperFactory);
    }
}
