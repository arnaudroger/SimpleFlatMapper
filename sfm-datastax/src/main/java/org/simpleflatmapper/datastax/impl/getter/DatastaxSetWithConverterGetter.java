package org.simpleflatmapper.datastax.impl.getter;

import com.datastax.driver.core.GettableByIndexData;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.converter.ContextualConverter;

import java.util.HashSet;
import java.util.Set;

public class DatastaxSetWithConverterGetter<I, T> implements Getter<GettableByIndexData, Set<T>> {

    private final int index;
    private final Class<I> type;
    private final ContextualConverter<I, T> converter;

    public DatastaxSetWithConverterGetter(int index, Class<I> type, ContextualConverter<I, T> converter) {
        this.index = index;
        this.type = type;
        this.converter = converter;
    }

    @Override
    public Set<T> get(GettableByIndexData target) throws Exception {
        Set<I> set = target.getSet(index, type);

        if (set == null) return null;

        Set<T> convertedSet = new HashSet<T>();
        for(I i : set) {
            convertedSet.add(converter.convert(i, null));
        }
        return convertedSet;
    }
}
