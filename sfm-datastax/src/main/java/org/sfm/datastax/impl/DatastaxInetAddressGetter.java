package org.sfm.datastax.impl;

import com.datastax.driver.core.GettableData;
import org.sfm.reflect.Getter;

import java.net.InetAddress;
import java.util.UUID;

public class DatastaxInetAddressGetter implements Getter<GettableData, InetAddress> {

    private final int index;

    public DatastaxInetAddressGetter(int index) {
        this.index = index;
    }

    @Override
    public InetAddress get(GettableData target) throws Exception {
        return target.getInet(index);
    }
}
