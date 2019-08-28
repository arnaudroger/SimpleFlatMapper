package org.simpleflatmapper.datastax;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.TableMetadata;
import org.simpleflatmapper.datastax.impl.DatastaxCrudFactory;
import org.simpleflatmapper.reflect.meta.AliasProviderService;
import org.simpleflatmapper.reflect.meta.Table;
import org.simpleflatmapper.reflect.meta.DefaultPropertyNameMatcher;
import org.simpleflatmapper.util.TypeHelper;

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
        Table table =
                AliasProviderService
                        .getAliasProvider()
                        .getTable(TypeHelper.toClass(targetType));
        return to(session, keyspace(session, table), table(session, table, targetType));
    }

    private String table(Session session, Table keyspaceTable, Type targetType) {
        String table = keyspaceTable.table();

        if (table == null) {
            final String className = TypeHelper.toClass(targetType).getSimpleName();
            for(TableMetadata metadata : session.getCluster().getMetadata().getKeyspace(keyspace(session, keyspaceTable)).getTables()) {
                if (DefaultPropertyNameMatcher.of(metadata.getName()).matches(className) != null) {
                    return metadata.getName();
                }
            }
        } else {
            return table;
        }

        throw new IllegalArgumentException("No table define on type " + targetType);
    }

    private String keyspace(Session session, Table table) {
        String keyspace = table.schema();
        if (keyspace == null) {
            keyspace = session.getLoggedKeyspace();
        }
        return keyspace;
    }

    public DatastaxCrud<T, K> to(Session session, String table) {
        Table keyspaceTable =
                AliasProviderService
                        .getAliasProvider()
                        .getTable(TypeHelper.toClass(targetType));

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
