package org.sfm.jdbc.getter;

import java.sql.ResultSet;

import org.sfm.reflect.Getter;

public class StringEnumIndexedResultSetGetter<E extends Enum<E>> implements  Getter<ResultSet, E> {

	private final int columnIndex;
	private final Class<E> enumType;
	
	public StringEnumIndexedResultSetGetter(int column, Class<E> enumType)  {
		this.columnIndex = column;
		this.enumType = enumType;
	}

	@Override
	public E get(ResultSet target) throws Exception {
		String o = target.getString(columnIndex);
		return (E) Enum.valueOf(enumType, String.valueOf(o));
	}
}
