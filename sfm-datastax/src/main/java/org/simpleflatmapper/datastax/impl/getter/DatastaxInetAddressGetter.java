package org.simpleflatmapper.datastax.impl.getter;

import com.datastax.driver.core.GettableByIndexData;
import org.simpleflatmapper.reflect.Getter;

import java.net.InetAddress;

public class DatastaxInetAddressGetter implements Getter<GettableByIndexData, InetAddress> {

    private final int index;

    public DatastaxInetAddressGetter(int index) {
        this.index = index;
    }

    @Override
    public InetAddress get(GettableByIndexData target) throws Exception {
        return target.getInet(index);
    }
}
