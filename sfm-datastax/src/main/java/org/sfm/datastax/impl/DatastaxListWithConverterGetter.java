package org.sfm.datastax.impl;

import com.datastax.driver.core.GettableByIndexData;
import org.sfm.reflect.Getter;
import org.sfm.utils.conv.Converter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DatastaxListWithConverterGetter<I, T> implements Getter<GettableByIndexData, List<T>> {

    private final int index;
    private final Class<I> type;
    private final Converter<I, T> converter;

    public DatastaxListWithConverterGetter(int index, Class<I> type, Converter<I, T> converter) {
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
            convertedList.add(converter.convert(i));
        }
        return convertedList;
    }
}
