package org.simpleflatmapper.datastax.impl.setter;

import com.datastax.driver.core.SettableByIndexData;
import org.simpleflatmapper.reflect.Setter;

import java.net.InetAddress;

public class InetAddressSettableDataSetter implements Setter<SettableByIndexData, InetAddress> {
    private final int index;

    public InetAddressSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void set(SettableByIndexData target, InetAddress value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setInet(index, value);
        }
    }
}
