package org.sfm.jdbc.getter;

import java.sql.ResultSet;

import org.sfm.reflect.EnumHelper;
import org.sfm.reflect.Getter;

public final class EnumResultSetGetter<E extends Enum<E>> implements  Getter<ResultSet, E> {

	private final int columnIndex;
	private final Class<E> enumType;
	private final E[] values;
	
	public EnumResultSetGetter(final int column, final Class<E> enumType)  {
		this.columnIndex = column;
		this.enumType = enumType;
		this.values = EnumHelper.getValues(enumType);
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
