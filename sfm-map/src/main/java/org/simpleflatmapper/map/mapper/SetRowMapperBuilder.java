package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.ContextualSourceFieldMapper;
import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.SourceFieldMapper;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;

import java.util.List;

public interface SetRowMapperBuilder<M extends SetRowMapper<ROW, SET, T, E>, ROW, SET, T, K extends FieldKey<K>, E extends Exception> {
    M mapper();

    ContextualSourceFieldMapper<ROW, T> sourceFieldMapper();
    
    boolean isRootAggregate();

    void addMapper(FieldMapper<ROW, T> mapper);

    void addMapping(K key, ColumnDefinition<K, ?> columnDefinition);

    MapperConfig<K, ROW> mapperConfig();

    MappingContextFactoryBuilder<? super ROW, K> getMappingContextFactoryBuilder();

    List<K> getKeys();
}
