package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.jdbc.MultiIndexFieldMapper;
import org.simpleflatmapper.map.setter.ContextualIndexedSetter;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.IndexedGetter;
import org.simpleflatmapper.reflect.IndexedSetter;
import org.simpleflatmapper.reflect.primitive.IntGetter;
import org.simpleflatmapper.util.ErrorHelper;

import java.sql.PreparedStatement;

public class CollectionIndexFieldMapper<T, C, P> implements MultiIndexFieldMapper<T> {
    private final ContextualIndexedSetter<PreparedStatement, P> setter;
    private final Getter<T, C> collectionGetter;
    private final IntGetter<? super C> sizeGetter;
    private final IndexedGetter<C, P> indexedGetter;

    public CollectionIndexFieldMapper(ContextualIndexedSetter<PreparedStatement, P> setter, Getter<T, C> collectionGetter, IntGetter<? super C> sizeGetter, IndexedGetter<C, P> indexedGetter) {
        this.setter = setter;
        this.collectionGetter = collectionGetter;
        this.sizeGetter = sizeGetter;
        this.indexedGetter = indexedGetter;
    }

    @Override
    public int map(PreparedStatement ps, T value, int columnIndex, Context context) throws Exception {
        C collection = collectionGetter.get(value);

        int size = sizeGetter.getInt(collection);

        for(int i = 0; i < size; i++) {
            setter.set(ps, indexedGetter.get(collection, i), columnIndex + i + 1, context);
        }

        return size;
    }

    @Override
    public int getSize(T value) {
        try {
            return sizeGetter.getInt(collectionGetter.get(value));
        } catch (Exception e) {
            ErrorHelper.rethrow(e);
            return 1;
        }
    }
}
