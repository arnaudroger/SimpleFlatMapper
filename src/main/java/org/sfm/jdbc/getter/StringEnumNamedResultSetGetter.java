package org.sfm.jdbc.getter;

import java.sql.ResultSet;

import org.sfm.reflect.Getter;

public class StringEnumNamedResultSetGetter<E extends Enum<E>> implements  Getter<ResultSet, E> {

	private final String column;
	private final Class<E> enumType;
	
	public StringEnumNamedResultSetGetter(String column, Class<E> enumType) {
		this.column = column;
		this.enumType = enumType;
	}

	@Override
	public E get(ResultSet target) throws Exception {
		String o = target.getString(column);
		return (E) Enum.valueOf(enumType, String.valueOf(o));
	}
}
