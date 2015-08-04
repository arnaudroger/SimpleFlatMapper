package org.sfm.datastax.impl;

import com.datastax.driver.core.GettableByIndexData;
import org.sfm.reflect.Getter;

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
