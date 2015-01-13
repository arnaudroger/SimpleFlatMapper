package org.sfm.jdbc.impl.getter;

import org.sfm.reflect.Getter;

import java.sql.Array;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public final class ArrayToListResultSetGetter<E> implements Getter<ResultSet, List<E>> {
	private final int column;
	private final Getter<ResultSet, E> elementGetter;
	
	public ArrayToListResultSetGetter(final int column, final Getter<ResultSet, E> elementGetter) {
		this.column = column;
		this.elementGetter = elementGetter;
	}

	public List<E> get(final ResultSet target) throws Exception {
		Array array = target.getArray(column);
		List<E> list = new ArrayList<E>(); 
		
		ResultSet rs = array.getResultSet();
		try {
			while(rs.next()) {
				list.add(elementGetter.get(rs));
			}
		} finally {
			rs.close();
		}
		
		return list;
	}
}
