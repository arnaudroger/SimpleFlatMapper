package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.SourceFieldMapper;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.reflect.meta.ArrayClassMeta;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.ObjectClassMeta;

import static org.simpleflatmapper.util.ErrorDoc.CSFM_GETTER_NOT_FOUND;

public interface ConstantSourceMapperBuilder<S, T, K extends FieldKey<K>> {

    @SuppressWarnings("unchecked")
    ConstantSourceMapperBuilder<S, T, K> addMapping(K key, ColumnDefinition<K, ?> columnDefinition);

    @SuppressWarnings("unchecked")
    ContextualSourceFieldMapperImpl<S, T> mapper();

    SourceFieldMapper<S, T> sourceFieldMapper();

    boolean isRootAggregate();

    MappingContextFactory<? super S> contextFactory();

    void addMapper(FieldMapper<S, T> mapper);
}
