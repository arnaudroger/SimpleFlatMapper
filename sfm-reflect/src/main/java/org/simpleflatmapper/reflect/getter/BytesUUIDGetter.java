package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.util.UUIDHelper;

import java.util.UUID;

public final class BytesUUIDGetter<R> implements Getter<R, UUID> {

	private final Getter<R, byte[]> bytesGetter;

	public BytesUUIDGetter(final Getter<R, byte[]> bytesGetter)  {
		this.bytesGetter = bytesGetter;
	}

	@Override
	public UUID get(final R target) throws Exception {
		final byte[] o = bytesGetter.get(target);
		if (o == null) return null;
		return UUIDHelper.fromBytes(o);
	}

    @Override
    public String toString() {
        return "StringUUIDGetter{" +
                "bytesGetter=" + bytesGetter +
                '}';
    }
}
