package org.simpleflatmapper.querydsl;

import com.mysema.query.Tuple;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.querydsl.getter.EnumTupleNamedIndexedGetter;
import org.simpleflatmapper.querydsl.getter.EnumTupleOrdinalIndexedGetter;
import org.simpleflatmapper.querydsl.getter.TupleIndexedGetter;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;

public final class TupleGetterFactory implements GetterFactory<Tuple, TupleElementKey> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <P> Getter<Tuple, P> newGetter(Type genericType, TupleElementKey key, Object... properties) {
		
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
