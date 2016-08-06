package org.simpleflatmapper.datastax.impl.setter;

import com.datastax.driver.core.SettableByIndexData;
import org.simpleflatmapper.reflect.Setter;

import java.util.Set;

public class SetSettableDataSetter<E> implements Setter<SettableByIndexData<?>, Set<E>> {
    private final int index;

    public SetSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void set(SettableByIndexData<?> target, Set<E> value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setSet(index, value);
        }
    }
}
