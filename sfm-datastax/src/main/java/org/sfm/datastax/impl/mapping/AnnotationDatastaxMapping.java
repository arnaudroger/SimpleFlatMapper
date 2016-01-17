package org.sfm.datastax.impl.mapping;


import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Table;
import org.sfm.datastax.impl.KeyspaceTable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AnnotationDatastaxMapping implements DatastaxMapping {

    @Override
    public KeyspaceTable lookForKeySpaceTable(Class<?> target) {
        KeyspaceTable keyspaceTable = KeyspaceTable.NULL;
        Table table = target.getAnnotation(Table.class);
        if (table != null) {
            keyspaceTable = new KeyspaceTable(table.keyspace(), table.name());
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
}
