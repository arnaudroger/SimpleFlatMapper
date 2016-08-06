package org.simpleflatmapper.datastax.impl.setter;

import com.datastax.driver.core.SettableByIndexData;
import org.simpleflatmapper.reflect.Setter;

import java.util.List;

public class ListSettableDataSetter<E> implements Setter<SettableByIndexData<?>, List<E>> {
    private final int index;

    public ListSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void set(SettableByIndexData<?> target, List<E> value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setList(index, value);
        }
    }
}
