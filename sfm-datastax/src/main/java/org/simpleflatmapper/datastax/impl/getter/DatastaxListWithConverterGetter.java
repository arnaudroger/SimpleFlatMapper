package org.simpleflatmapper.datastax.impl.getter;

import com.datastax.driver.core.GettableByIndexData;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.converter.ContextualConverter;

import java.util.ArrayList;
import java.util.List;

public class DatastaxListWithConverterGetter<I, T> implements Getter<GettableByIndexData, List<T>> {

    private final int index;
    private final Class<I> type;
    private final ContextualConverter<I, T> converter;

    public DatastaxListWithConverterGetter(int index, Class<I> type, ContextualConverter<I, T> converter) {
        this.index = index;
        this.type = type;
        this.converter = converter;
    }

    @Override
    public List<T> get(GettableByIndexData target) throws Exception {
        List<I> list = target.getList(index, type);

        if (list == null) return null;

        List<T> convertedList = new ArrayList<T>(list.size());
        for(I i : list) {
            convertedList.add(converter.convert(i, null));
        }
        return convertedList;
    }
}
