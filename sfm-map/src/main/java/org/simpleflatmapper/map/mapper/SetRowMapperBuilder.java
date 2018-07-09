package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.SourceFieldMapper;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;

public interface SetRowMapperBuilder<M extends SetRowMapper<ROW, SET, T, E>, ROW, SET, T, K extends FieldKey<K>, E extends Exception> {
    M mapper();

    SourceFieldMapper<ROW, T> sourceFieldMapper();

    boolean hasJoin();

    void addMapper(FieldMapper<ROW, T> mapper);

    void addMapping(K key, FieldMapperColumnDefinition<K> columnDefinition);

    MapperConfig<K, FieldMapperColumnDefinition<K>> mapperConfig();

    MappingContextFactoryBuilder<? super ROW, K> getMappingContextFactoryBuilder();
}
