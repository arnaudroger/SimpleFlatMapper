package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.ByteGetter;

public final class IndexedByteArrayGetter implements Getter<byte[], Byte>, ByteGetter<byte[]> {
    private final int index;

    public IndexedByteArrayGetter(int index) {
        this.index = index;
    }

    @Override
    public byte getByte(byte[] target) throws Exception {
        return target[index];
    }

    @Override
    public Byte get(byte[] target) throws Exception {
        return getByte(target);
    }
}
