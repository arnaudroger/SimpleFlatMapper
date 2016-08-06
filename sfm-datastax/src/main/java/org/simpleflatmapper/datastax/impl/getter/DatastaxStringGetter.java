package org.simpleflatmapper.datastax.impl.getter;

import com.datastax.driver.core.GettableByIndexData;
import org.simpleflatmapper.reflect.Getter;

public class DatastaxStringGetter implements Getter<GettableByIndexData, String> {

    private final int index;

    public DatastaxStringGetter(int index) {
        this.index = index;
    }

    @Override
    public String get(GettableByIndexData target) throws Exception {
        return target.getString(index);
    }
}
