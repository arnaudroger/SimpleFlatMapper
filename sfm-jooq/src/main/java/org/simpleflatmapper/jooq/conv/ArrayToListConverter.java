package org.simpleflatmapper.jooq.conv;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.converter.Converter;

import java.sql.Array;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ArrayToListConverter<E> implements Converter<Array, List<E>> {

	private final Getter<ResultSet, E> elementGetter;
	
	public ArrayToListConverter(Getter<ResultSet, E> elementGetter) {
		this.elementGetter = elementGetter;
	}

	@Override
	public List<E> convert(Array array) throws Exception {
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
