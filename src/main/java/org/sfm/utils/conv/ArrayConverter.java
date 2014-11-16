package org.sfm.utils.conv;

import java.sql.Array;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.sfm.reflect.Getter;

public class ArrayConverter<E> implements Converter<Array, E[]> {

	private final Class<E> elementClass;
	private final Getter<ResultSet, E> elementGetter;
	
	public ArrayConverter(Class<E> elementClass, Getter<ResultSet, E> elementGetter) {
		this.elementClass = elementClass;
		this.elementGetter = elementGetter;
	}

	@Override
	public E[] convert(Array array) throws Exception {
		List<E> list = new ArrayList<E>();
		
		ResultSet rs = array.getResultSet();
		try {
			while(rs.next()) {
				list.add(elementGetter.get(rs));
			}
			
		} finally {
			rs.close();
		}
		
		@SuppressWarnings("unchecked")
		E[] elements = (E[]) java.lang.reflect.Array.newInstance(elementClass, list.size());
		return list.toArray(elements);
	}

}
