package org.sfm.jdbc.impl.getter;

import org.sfm.reflect.Getter;

import java.sql.ResultSet;

public final class StringEnumResultSetGetter<E extends Enum<E>> implements Getter<ResultSet, E> {

	private final Class<E> enumType;
	private Getter<ResultSet, String> stringGetter;
	
	public StringEnumResultSetGetter(final Getter<ResultSet, String> stringGetter, final Class<E> enumType)  {
		this.stringGetter = stringGetter;
		this.enumType = enumType;
	}

	@Override
	public E get(final ResultSet target) throws Exception {
		final String o = stringGetter.get(target);
		return (E) Enum.valueOf(enumType, String.valueOf(o));
	}

    @Override
    public String toString() {
        return "StringEnumResultSetGetter{" +
                "enumType=" + enumType +
                ", stringGetter=" + stringGetter +
                '}';
    }
}
