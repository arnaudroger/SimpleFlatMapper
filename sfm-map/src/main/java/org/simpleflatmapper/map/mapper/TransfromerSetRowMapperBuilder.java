package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.ContextualSourceFieldMapper;
import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.SourceFieldMapper;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.util.Function;

import java.util.List;

public class TransfromerSetRowMapperBuilder<MO extends SetRowMapper<ROW, SET, O, E>, MI extends SetRowMapper<ROW, SET, I, E>, ROW, SET, I, O, K extends FieldKey<K>, E extends Exception> 
        implements SetRowMapperBuilder<MO, ROW, SET, O, K, E> {
    
    private final SetRowMapperBuilder<MI, ROW, SET, I, K, E> delegate;
    private final Function<MI, MO> transformer;
    

    public TransfromerSetRowMapperBuilder(SetRowMapperBuilder<MI, ROW, SET, I, K, E> delegate, Function<MI, MO> transformer) {
        this.delegate = delegate;
        this.transformer = transformer;
    }

    @Override
    public final MO mapper() {
        return transformer.apply(delegate.mapper());
    }

    @Override
    public ContextualSourceFieldMapper<ROW, O> sourceFieldMapper() {
        throw new UnsupportedOperationException();
    }
    @Override
    public final boolean isRootAggregate() {
        return delegate.isRootAggregate();
    }

    public final void addMapper(FieldMapper<ROW, O> mapper) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void addMapping(K key, ColumnDefinition<K, ?> columnDefinition) {
        delegate.addMapping(key, columnDefinition);
    }

    @Override
    public final MapperConfig<K, ROW> mapperConfig() {
        return delegate.mapperConfig();
    }

    @Override
    public final MappingContextFactoryBuilder<? super ROW, K> getMappingContextFactoryBuilder() {
        return delegate.getMappingContextFactoryBuilder();
    }

    @Override
    public List<K> getKeys() {
        return delegate.getKeys();
    }
}
