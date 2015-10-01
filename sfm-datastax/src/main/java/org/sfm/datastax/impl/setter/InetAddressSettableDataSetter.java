package org.sfm.datastax.impl.setter;

import com.datastax.driver.core.SettableData;
import org.sfm.reflect.Setter;

import java.net.InetAddress;

public class InetAddressSettableDataSetter implements Setter<SettableData, InetAddress> {
    private final int index;

    public InetAddressSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void set(SettableData target, InetAddress value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setInet(index, value);
        }
    }
}
