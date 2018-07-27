package org.simpleflatmapper.datastax.impl.getter;

import com.datastax.driver.core.GettableByIndexData;
import org.simpleflatmapper.reflect.Getter;

import java.util.Date;

public class DatastaxTimestampGetter implements Getter<GettableByIndexData, Date> {

    private final int index;

    public DatastaxTimestampGetter(int index) {
        this.index = index;
    }

    @Override
    public Date get(GettableByIndexData target) throws Exception {
        return target.getTimestamp(index);
    }
}
