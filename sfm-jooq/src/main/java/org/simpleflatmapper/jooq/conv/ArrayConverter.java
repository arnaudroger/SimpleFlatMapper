package org.simpleflatmapper.jooq.conv;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.converter.Converter;

import java.sql.Array;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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
		E[] elements = (E[]) java.lang.reflect.Array.newInstance(elementClass, 0);
		return list.toArray(elements);
	}

}
