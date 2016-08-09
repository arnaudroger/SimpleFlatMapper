package org.simpleflatmapper.reflect.meta;

public interface AliasProviderFactory {

    AliasProvider newProvider();
    boolean isActive();
}
