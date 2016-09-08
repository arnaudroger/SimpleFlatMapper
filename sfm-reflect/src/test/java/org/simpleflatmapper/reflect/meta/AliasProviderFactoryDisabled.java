package org.simpleflatmapper.reflect.meta;

public class AliasProviderFactoryDisabled implements AliasProviderFactory {
    @Override
    public AliasProvider newProvider() {
        return null;
    }

    @Override
    public boolean isActive() {
        return false;
    }
}
