package org.simpleflatmapper.datastax.impl.getter;

import com.datastax.driver.core.GettableByIndexData;
import org.simpleflatmapper.datastax.DataHelper;
import org.simpleflatmapper.reflect.Getter;

public class DatastaxDateGetter implements Getter<GettableByIndexData, Object> {

    private final int index;

    public DatastaxDateGetter(int index) {
        this.index = index;
    }

    @Override
    public Object get(GettableByIndexData target) throws Exception {
        return DataHelper.getDate(index, target);
    }
}
