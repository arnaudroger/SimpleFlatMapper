package org.simpleflatmapper.datastax.impl.setter;

import com.datastax.driver.core.SettableByIndexData;
import org.simpleflatmapper.reflect.Setter;

import java.util.Map;

public class MapSettableDataSetter<K, V> implements Setter<SettableByIndexData<?>, Map<K, V>> {
    private final int index;

    public MapSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void set(SettableByIndexData<?> target, Map<K, V> value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setMap(index, value);
        }
    }
}
