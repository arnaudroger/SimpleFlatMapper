package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;

public final class StringEnumGetter<R, E extends Enum<E>> implements Getter<R, E> {

	private final Class<E> enumType;
	private final Getter<R, String> stringGetter;
	
	public StringEnumGetter(final Getter<R, String> stringGetter, final Class<E> enumType)  {
		this.stringGetter = stringGetter;
		this.enumType = enumType;
	}

	@Override
	public E get(final R target) throws Exception {
		final String o = stringGetter.get(target);
		return (E) Enum.valueOf(enumType, String.valueOf(o));
	}

    @Override
    public String toString() {
        return "StringEnumGetter{" +
                "enumType=" + enumType +
                ", stringGetter=" + stringGetter +
                '}';
    }
}
