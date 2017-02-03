package org.simpleflatmapper.reflect.setter;

import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.primitive.ByteSetter;

public final class IndexedByteArraySetter implements Setter<byte[], Byte>, ByteSetter<byte[]> {
    private final int index;

    public IndexedByteArraySetter(int index) {
        this.index = index;
    }

    @Override
    public void setByte(byte[] target, byte value) throws Exception {
        target[index] = value;
    }

    @Override
    public void set(byte[] target, Byte value) throws Exception {
        setByte(target, value);
    }
}
