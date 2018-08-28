package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.mapper.FieldMapperGetterAdapter;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.getter.GetterFactory;

import java.lang.reflect.Type;

public class FieldMapperGetterFactoryAdapter<T, K> implements FieldMapperGetterFactory<T, K> {
    
    private final GetterFactory<T, K> delegate;

    public FieldMapperGetterFactoryAdapter(GetterFactory<T, K> delegate) {
        this.delegate = delegate;
    }

    @Override
    public <P> FieldMapperGetter<T, P> newGetter(Type target, K key, MappingContextFactoryBuilder<?, K> mappingContextFactoryBuilder, Object... properties) {
        Getter<T, P> getter = delegate.newGetter(target, key, properties);
        if (getter == null) return null;
        return FieldMapperGetterAdapter.of(getter);
    }
}
