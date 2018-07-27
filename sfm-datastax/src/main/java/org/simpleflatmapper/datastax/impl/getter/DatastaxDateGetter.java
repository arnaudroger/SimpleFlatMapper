package org.simpleflatmapper.datastax.impl.getter;

import com.datastax.driver.core.GettableByIndexData;
import com.datastax.driver.core.LocalDate;
import org.simpleflatmapper.reflect.Getter;

public class DatastaxDateGetter implements Getter<GettableByIndexData, LocalDate> {

    private final int index;

    public DatastaxDateGetter(int index) {
        this.index = index;
    }

    @Override
    public LocalDate get(GettableByIndexData target) throws Exception {
        return target.getDate(index);
    }
}
