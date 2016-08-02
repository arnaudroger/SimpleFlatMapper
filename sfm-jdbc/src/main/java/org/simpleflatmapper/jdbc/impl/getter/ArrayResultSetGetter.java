package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.core.reflect.Getter;

import java.sql.Array;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public final class ArrayResultSetGetter<E> implements Getter<ResultSet, E[]> {
	private final int column;
	private final Class<E> elementType;
	private final Getter<ResultSet, E> elementGetter;
	
	public ArrayResultSetGetter(final int column, final Class<E> elementType, final Getter<ResultSet, E> elementGetter) {
		this.column = column;
		this.elementType = elementType;
		this.elementGetter = elementGetter;
	}

	@SuppressWarnings("unchecked")
	public E[] get(final ResultSet target) throws Exception {
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
		
		E[] eltArray = (E[]) java.lang.reflect.Array.newInstance(elementType, 0);
		return list.toArray(eltArray);
	}

    @Override
    public String toString() {
        return "ArrayResultSetGetter{" +
                "column=" + column +
                ", elementGetter=" + elementGetter +
                '}';
    }
}
