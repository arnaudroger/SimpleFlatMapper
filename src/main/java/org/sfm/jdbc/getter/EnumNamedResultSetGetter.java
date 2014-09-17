package org.sfm.jdbc.getter;

import java.sql.ResultSet;

import org.sfm.reflect.EnumHelper;
import org.sfm.reflect.Getter;

public final class EnumNamedResultSetGetter<E extends Enum<E>> implements  Getter<ResultSet, E> {

	private final String column;
	private final Class<E> enumType;
	private final E[] values;
	
	public EnumNamedResultSetGetter(final String column, final Class<E> enumType) {
		this.column = column;
		this.enumType = enumType;
		this.values = EnumHelper.getValues(enumType);
	}

	@Override
	public E get(final ResultSet target) throws Exception {
		final Object o = target.getObject(column);
		if (o instanceof Number) {
			return values[((Number) o).intValue()];
		} else {
			return (E) Enum.valueOf(enumType, String.valueOf(o));
		}
	}
}
