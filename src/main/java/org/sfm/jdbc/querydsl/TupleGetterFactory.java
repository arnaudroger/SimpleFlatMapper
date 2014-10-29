package org.sfm.jdbc.querydsl;

import java.lang.reflect.Type;

import org.sfm.map.GetterFactory;
import org.sfm.reflect.Getter;
import org.sfm.reflect.TypeHelper;

import com.mysema.query.Tuple;

public final class TupleGetterFactory implements GetterFactory<Tuple, TupleElementKey>{
	public static final int UNDEFINED = -99999;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <P> Getter<Tuple, P> newGetter(Type genericType, TupleElementKey key) {
		
		Class<Object> propretyClass = TypeHelper.toClass(genericType);
		if (Enum.class.isAssignableFrom(propretyClass)) {
			Class<?> columnType = key.getExpression().getType();
			
			if (Number.class.isAssignableFrom(columnType)) {
				return new EnumTupleOrdinalIndexedGetter(key, propretyClass);
			} else if (String.class.equals(columnType)){
				return new EnumTupleNamedIndexedGetter(key, propretyClass);
			} else {
				return null;
			}
 			
		}
		
		return new TupleIndexedGetter<P>( key);
	}
}
