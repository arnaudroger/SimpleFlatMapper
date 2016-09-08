package org.simpleflatmapper.reflect.meta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AliasProviderFactory2 implements AliasProviderFactory {
    @Override
    public AliasProvider newProvider() {
        return new AliasProvider2();
    }

    @Override
    public boolean isActive() {
        return true;
    }

    public static class AliasProvider2 implements AliasProvider {
        @Override
        public String getAliasForMethod(Method method) {
            return null;
        }

        @Override
        public String getAliasForField(Field field) {
            return null;
        }

        @Override
        public Table getTable(Class<?> target) {
            return null;
        }
    }
}
