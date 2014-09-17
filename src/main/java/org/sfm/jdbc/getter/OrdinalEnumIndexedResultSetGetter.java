package org.sfm.jdbc.getter;

import java.sql.ResultSet;

import org.sfm.reflect.EnumHelper;
import org.sfm.reflect.Getter;

public final class OrdinalEnumIndexedResultSetGetter<E extends Enum<E>> implements  Getter<ResultSet, E> {

	private final int columnIndex;
	private final E[] values;
	
	public OrdinalEnumIndexedResultSetGetter(final int column, final Class<E> enumType)  {
		this.columnIndex = column;
		this.values = EnumHelper.getValues(enumType);
	}

	@Override
	public E get(final ResultSet target) throws Exception {
		return values[target.getInt(columnIndex)];
	}
}
