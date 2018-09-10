package org.simpleflatmapper.converter;

import org.simpleflatmapper.util.Supplier;

public interface ContextFactoryBuilder {
    int addSupplier(Supplier<?> supplier);
    ContextFactory build();
}
