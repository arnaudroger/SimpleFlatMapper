package org.sfm.jdbc.getter;

import java.sql.ResultSet;

import org.sfm.reflect.Getter;

public final class EnumIndexedResultSetGetter<E extends Enum<E>> implements  Getter<ResultSet, E> {

	private final int columnIndex;
	private final Class<E> enumType;
	private final E[] values;
	@SuppressWarnings("unchecked")
	public EnumIndexedResultSetGetter(final int column, final Class<E> enumType)  {
		this.columnIndex = column;
		this.enumType = enumType;
		try {
			this.values = (E[]) enumType.getDeclaredMethod("values").invoke(enumType);
		} catch (Exception e) {
			throw new Error("Unexpected error getting enum values " + e, e);
		}
	}

	@Override
	public E get(final ResultSet target) throws Exception {
		final Object o = target.getObject(columnIndex);
		if (o instanceof Number) {
			return values[((Number) o).intValue()];
		} else {
			return (E) Enum.valueOf(enumType, String.valueOf(o));
		}
	}
}
