package org.sfm.jdbc.getter;

import java.sql.ResultSet;

import org.sfm.reflect.Getter;

public final class StringEnumIndexedResultSetGetter<E extends Enum<E>> implements Getter<ResultSet, E> {

	private final int columnIndex;
	private final Class<E> enumType;
	
	public StringEnumIndexedResultSetGetter(final int column, final Class<E> enumType)  {
		this.columnIndex = column;
		this.enumType = enumType;
	}

	@Override
	public E get(final ResultSet target) throws Exception {
		final String o = target.getString(columnIndex);
		return (E) Enum.valueOf(enumType, String.valueOf(o));
	}
}
