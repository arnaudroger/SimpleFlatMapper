package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;

import java.util.UUID;

public final class StringUUIDGetter<R> implements Getter<R, UUID> {

	private final Getter<R, String> stringGetter;

	public StringUUIDGetter(final Getter<R, String> stringGetter)  {
		this.stringGetter = stringGetter;
	}

	@Override
	public UUID get(final R target) throws Exception {
		final String o = stringGetter.get(target);
		if (o == null) return null;
		return UUID.fromString(o);
	}

    @Override
    public String toString() {
        return "StringUUIDGetter{" +
                "stringGetter=" + stringGetter +
                '}';
    }
}
