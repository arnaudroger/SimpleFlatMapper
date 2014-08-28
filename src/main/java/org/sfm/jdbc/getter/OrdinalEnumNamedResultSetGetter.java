package org.sfm.jdbc.getter;

import java.sql.ResultSet;

import org.sfm.reflect.Getter;

public final class OrdinalEnumNamedResultSetGetter<E extends Enum<E>> implements  Getter<ResultSet, E> {

	private final String column;
	private final E[] values;
	
	public OrdinalEnumNamedResultSetGetter(final String column, final Class<E> enumType) {
		this.column = column;
		this.values = EnumHelper.getValues(enumType);
	}

	@Override
	public E get(final ResultSet target) throws Exception {
		return values[target.getInt(column)];
	}
}
