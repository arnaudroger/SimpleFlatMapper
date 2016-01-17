package org.sfm.datastax.impl.mapping;

import org.sfm.datastax.impl.KeyspaceTable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class DefaultDatastaxMapping implements DatastaxMapping {
    @Override
    public KeyspaceTable lookForKeySpaceTable(Class<?> target) {
        return KeyspaceTable.NULL;
    }

    @Override
    public String getAliasForMethod(Method method) {
        return null;
    }

    @Override
    public String getAliasForField(Field field) {
        return null;
    }
}
