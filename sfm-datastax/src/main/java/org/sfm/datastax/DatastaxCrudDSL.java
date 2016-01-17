package org.sfm.datastax;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.TableMetadata;
import org.sfm.datastax.impl.DatastaxCrudFactory;
import org.sfm.datastax.impl.KeyspaceTable;
import org.sfm.datastax.impl.DatastaxMappingFactory;
import org.sfm.reflect.TypeHelper;
import org.sfm.reflect.meta.DefaultPropertyNameMatcher;
import org.sfm.reflect.meta.PropertyNameMatcher;

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


    public DatastaxCrud<T, K> to(Session session) {
        KeyspaceTable keyspaceTable =
                DatastaxMappingFactory
                    .getDatastaxMapping()
                        .lookForKeySpaceTable(TypeHelper.toClass(targetType));
        return to(session, keyspace(session, keyspaceTable), table(session, keyspaceTable, targetType));
    }

    private String table(Session session, KeyspaceTable keyspaceTable, Type targetType) {
        String table = keyspaceTable.table();

        if (table == null) {
            PropertyNameMatcher pm = DefaultPropertyNameMatcher.of(TypeHelper.toClass(targetType).getSimpleName());
            for(TableMetadata metadata : session.getCluster().getMetadata().getKeyspace(keyspace(session, keyspaceTable)).getTables()) {
                if (pm.matches(metadata.getName())) {
                    return metadata.getName();
                }
            }
        } else {
            return table;
        }

        throw new IllegalArgumentException("No table define on type " + targetType);
    }

    private String keyspace(Session session, KeyspaceTable keyspaceTable) {
        String keyspace = keyspaceTable.keyspace();
        if (keyspace == null) {
            keyspace = session.getLoggedKeyspace();
        }
        return keyspace;
    }

    public DatastaxCrud<T, K> to(Session session, String table) {
        KeyspaceTable keyspaceTable =
                DatastaxMappingFactory
                        .getDatastaxMapping()
                        .lookForKeySpaceTable(TypeHelper.toClass(targetType));

        return to(session, keyspace(session, keyspaceTable), table);
    }

    public DatastaxCrud<T, K> to(Session session, String keyspace,  String table) {
        TableMetadata tableMetadata =
                session.getCluster().getMetadata().getKeyspace(keyspace).getTable(table);

        return DatastaxCrudFactory.<T, K>newInstance(targetType,
                keyType,
                tableMetadata,
                session,
                datastaxMapperFactory);
    }
}
