package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.reflect.meta.AliasProvider;
import org.simpleflatmapper.reflect.meta.AliasProviderFactory;

public class JpaAliasProviderFactory implements AliasProviderFactory {
    @Override
    public AliasProvider newProvider() {
        return new JpaAliasProvider();
    }

    @Override
    public boolean isActive() {
        try {
            Class.forName("javax.persistence.Column");
            return true;
        } catch (Throwable e) {
        }
        return false;
    }
}
