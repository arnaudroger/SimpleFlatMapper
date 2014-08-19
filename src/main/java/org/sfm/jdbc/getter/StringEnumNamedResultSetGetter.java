package org.sfm.jdbc.getter;

import java.sql.ResultSet;

import org.sfm.reflect.Getter;

public final class StringEnumNamedResultSetGetter<E extends Enum<E>> implements Getter<ResultSet, E> {

	private final String column;
	private final Class<E> enumType;
	
	public StringEnumNamedResultSetGetter(final String column, final Class<E> enumType) {
		this.column = column;
		this.enumType = enumType;
	}

	@Override
	public E get(final ResultSet target) throws Exception {
		final String o = target.getString(column);
		return (E) Enum.valueOf(enumType, String.valueOf(o));
	}
}
