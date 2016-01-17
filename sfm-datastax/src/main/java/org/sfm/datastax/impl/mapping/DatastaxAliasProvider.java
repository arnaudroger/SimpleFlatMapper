package org.sfm.datastax.impl.mapping;


import com.datastax.driver.mapping.annotations.Column;
import org.sfm.reflect.meta.AliasProvider;
import org.sfm.reflect.meta.AliasProviderFactory;
import org.sfm.reflect.meta.Table;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class DatastaxAliasProvider implements AliasProvider {

    @Override
    public Table getTable(Class<?> target) {
        Table keyspaceTable = Table.NULL;
        com.datastax.driver.mapping.annotations.Table table = target.getAnnotation(com.datastax.driver.mapping.annotations.Table.class);
        if (table != null) {
            keyspaceTable = new Table(null, table.keyspace(), table.name());
        }
        return keyspaceTable;
    }

    @Override
    public String getAliasForMethod(Method method) {
        String alias = null;
        Column col = method.getAnnotation(Column.class);
        if (col != null) {
            alias = col.name();
        }
        return alias;
    }

    @Override
    public String getAliasForField(Field field) {
        String alias = null;
        Column col = field.getAnnotation(Column.class);
        if (col != null) {
            alias = col.name();
        }
        return alias;
    }

    public static void registers() {
        if (_isMappingPresent()) {
            AliasProviderFactory.register(new DatastaxAliasProvider());
        }
    }

    private static boolean _isMappingPresent() {
        try {
            return Column.class != null;
        } catch(Throwable e) {
            return false;
        }
    }
}
