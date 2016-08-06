package org.simpleflatmapper.datastax.impl.getter;

import com.datastax.driver.core.GettableByIndexData;
import org.simpleflatmapper.reflect.Getter;

public class DatastaxToStringGetter implements Getter<GettableByIndexData, String> {
    private final Getter<GettableByIndexData, ?> getter;

    public DatastaxToStringGetter(Getter<GettableByIndexData, ?> getter) {
        this.getter = getter;
    }

    @Override
    public String get(GettableByIndexData target) throws Exception {
        return String.valueOf(getter.get(target));
    }
}
