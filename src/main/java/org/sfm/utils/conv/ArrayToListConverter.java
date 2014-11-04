package org.sfm.utils.conv;

import java.sql.Array;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.sfm.reflect.Getter;

public class ArrayToListConverter<F, E> implements Converter<F, List<E>> {

	private final Getter<ResultSet, E> elementGetter;
	
	public ArrayToListConverter(Getter<ResultSet, E> elementGetter) {
		this.elementGetter = elementGetter;
	}

	@Override
	public List<E> convert(F in) throws Exception {
		// expext an Array
		if (!(in instanceof Array)) {
			throw new IllegalArgumentException("Expect java.sql.Array but got " + in);
		}
		
		Array array = (Array) in;
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
