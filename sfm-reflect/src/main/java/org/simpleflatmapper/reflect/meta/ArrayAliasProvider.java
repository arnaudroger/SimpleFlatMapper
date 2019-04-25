package org.simpleflatmapper.reflect.meta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ArrayAliasProvider implements AliasProvider {
    private final AliasProvider[] providers;

    public ArrayAliasProvider(AliasProvider... providers) {
        this.providers = providers;
    }

    @Override
    public String getAliasForMethod(Method method) {
        for(AliasProvider ap : providers) {
            String alias = ap.getAliasForMethod(method);
            if (alias != null) return alias;
        }
        return null;
    }

    @Override
    public String getAliasForField(Field field) {
        for(AliasProvider ap : providers) {
            String alias = ap.getAliasForField(field);
            if (alias != null) return alias;
        }
        return null;
    }

    @Override
    public Table getTable(Class<?> target) {
        for(AliasProvider ap : providers) {
            Table table = ap.getTable(target);
            if (! Table.isNull(table)) return table;
        }
        return Table.NULL;
    }

    public AliasProvider[] providers() {
        return Arrays.copyOf(providers, providers.length);
    }
}
