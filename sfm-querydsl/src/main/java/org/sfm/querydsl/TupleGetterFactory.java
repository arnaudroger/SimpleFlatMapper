package org.sfm.querydsl;

import com.mysema.query.Tuple;
import org.sfm.map.GetterFactory;
import org.sfm.map.mapper.ColumnDefinition;
import org.sfm.querydsl.getter.EnumTupleNamedIndexedGetter;
import org.sfm.querydsl.getter.EnumTupleOrdinalIndexedGetter;
import org.sfm.querydsl.getter.TupleIndexedGetter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.TypeHelper;

import java.lang.reflect.Type;

public final class TupleGetterFactory implements GetterFactory<Tuple, TupleElementKey>{

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <P> Getter<Tuple, P> newGetter(Type genericType, TupleElementKey key, ColumnDefinition<?, ?> columnDefinition) {
		
		Class<Object> propertyClass = TypeHelper.toClass(genericType);
		if (Enum.class.isAssignableFrom(propertyClass)) {
			Class<?> columnType = key.getExpression().getType();
			
			if (Number.class.isAssignableFrom(columnType)) {
				return new EnumTupleOrdinalIndexedGetter(key, propertyClass);
			} else if (String.class.equals(columnType)){
				return new EnumTupleNamedIndexedGetter(key, propertyClass);
			} else {
				return null;
			}
 			
		}
		
		return new TupleIndexedGetter<P>( key);
	}
}
