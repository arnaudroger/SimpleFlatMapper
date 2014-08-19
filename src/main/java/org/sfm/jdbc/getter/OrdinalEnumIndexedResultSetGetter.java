package org.sfm.jdbc.getter;

import java.sql.ResultSet;

import org.sfm.reflect.Getter;

public final class OrdinalEnumIndexedResultSetGetter<E extends Enum<E>> implements  Getter<ResultSet, E> {

	private final int columnIndex;
	private final E[] values;
	@SuppressWarnings("unchecked")
	public OrdinalEnumIndexedResultSetGetter(final int column, final Class<E> enumType)  {
		this.columnIndex = column;
		try {
			this.values = (E[]) enumType.getDeclaredMethod("values").invoke(enumType);
		} catch (Exception e) {
			throw new Error("Unexpected error getting enum values " + e, e);
		}
	}

	@Override
	public E get(final ResultSet target) throws Exception {
		return values[target.getInt(columnIndex)];
	}
}
