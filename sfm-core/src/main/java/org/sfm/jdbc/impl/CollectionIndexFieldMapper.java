package org.sfm.jdbc.impl;

import org.sfm.jdbc.MultiIndexFieldMapper;
import org.sfm.jdbc.impl.setter.PreparedStatementIndexSetter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.IndexedGetter;
import org.sfm.reflect.IndexedSetter;
import org.sfm.reflect.primitive.IntGetter;
import org.sfm.utils.ErrorHelper;

import java.sql.PreparedStatement;

public class CollectionIndexFieldMapper<T, C, P> implements MultiIndexFieldMapper<T> {
    private final IndexedSetter<PreparedStatement, P> setter;
    private final Getter<T, C> collectionGetter;
    private final IntGetter<? super C> sizeGetter;
    private final IndexedGetter<C, P> indexedGetter;

    public CollectionIndexFieldMapper(IndexedSetter<PreparedStatement, P> setter, Getter<T, C> collectionGetter, IntGetter<? super C> sizeGetter, IndexedGetter<C, P> indexedGetter) {
        this.setter = setter;
        this.collectionGetter = collectionGetter;
        this.sizeGetter = sizeGetter;
        this.indexedGetter = indexedGetter;
    }

    @Override
    public int map(PreparedStatement ps, T value, int columnIndex) throws Exception {
        C collection = collectionGetter.get(value);

        int size = sizeGetter.getInt(collection);

        for(int i = 0; i < size; i++) {
            setter.set(ps, indexedGetter.get(collection, i), columnIndex + i + 1);
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
