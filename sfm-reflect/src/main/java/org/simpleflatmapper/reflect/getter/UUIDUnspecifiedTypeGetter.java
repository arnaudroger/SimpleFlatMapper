package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.util.UUIDHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public final class UUIDUnspecifiedTypeGetter<R> implements Getter<R, UUID> {

	private final Getter<R, ?> getter;

	public UUIDUnspecifiedTypeGetter(Getter<R, ?> getter)  {
		this.getter = getter;
	}

	@Override
	public UUID get(final R target) throws Exception {
		final Object o = getter.get(target);
		if (o == null) return null;
		if (o instanceof String) {
			return UUID.fromString((String)o);
		} else if (o instanceof byte[]) {
			return UUIDHelper.fromBytes((byte[])o);
		} else if (o instanceof InputStream) {
			return UUIDHelper.fromBytes(toBytes((InputStream)o));
		}
		throw new IllegalArgumentException("Cannot convert " + o + " to UUID");
	}

	private byte[] toBytes(InputStream o) throws IOException {
		try {
			int currentIndex = 0;
			byte[] bytes = new byte[16];
			do {
				int nbRead = o.read(bytes, currentIndex, bytes.length - currentIndex);
				if (nbRead == -1)
					return bytes;
				currentIndex += nbRead;
				if (currentIndex >= bytes.length)
					return bytes;
			} while(true);
		} finally {
			o.close();
		}
	}

	@Override
    public String toString() {
        return "UUIDUnspecifiedTypeGetter{" +
                "getter=" + getter +
                '}';
    }
}
