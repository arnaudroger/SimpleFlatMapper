package org.sfm.map;

import org.sfm.map.context.MappingContextFactoryBuilder;
import org.sfm.map.mapper.ColumnDefinition;
import org.sfm.map.mapper.PropertyMapping;

public interface FieldMapperToSourceFactory<S, K extends FieldKey<K>> {
    <T, P> FieldMapper<T, S> newFieldMapperToSource(
            PropertyMapping<T, P, K, ? extends ColumnDefinition<K, ?>> pm,
            MappingContextFactoryBuilder builder);
}
