package org.simpleflatmapper.datastax.impl.mapping;

import org.simpleflatmapper.reflect.meta.AliasProvider;
import org.simpleflatmapper.reflect.meta.AliasProviderFactory;

public class DatastaxAliasProviderFactory implements AliasProviderFactory {
    @Override
    public AliasProvider newProvider() {
        return new DatastaxAliasProvider();
    }

    @Override
    public boolean isActive() {
        try {
            Class.forName("com.datastax.driver.mapping.annotations.Table");
            return true;
        } catch (Throwable e) {}
        return false;
    }
}
