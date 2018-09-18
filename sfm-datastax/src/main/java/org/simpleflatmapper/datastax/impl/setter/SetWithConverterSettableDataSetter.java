package org.simpleflatmapper.datastax.impl.setter;

import com.datastax.driver.core.SettableByIndexData;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.converter.ContextualConverter;

import java.util.HashSet;
import java.util.Set;

public class SetWithConverterSettableDataSetter<I, O> implements Setter<SettableByIndexData<?>, Set<I>> {
    private final int index;
    private final ContextualConverter<I, O> converter;

    public SetWithConverterSettableDataSetter(int index, ContextualConverter<I, O> converter) {
        this.index = index;
        this.converter = converter;
    }

    @Override
    public void set(SettableByIndexData<?> target, Set<I> value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            Set<O> list = new HashSet<O>(value.size());
            for(I i : value) {
                list.add(converter.convert(i, null));
            }
            target.setSet(index, list);
        }
    }
}
