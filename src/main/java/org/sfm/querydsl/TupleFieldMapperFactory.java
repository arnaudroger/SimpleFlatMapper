package org.sfm.querydsl;

import com.mysema.query.Tuple;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.impl.FieldErrorHandlerMapper;
import org.sfm.map.impl.FieldMapper;
import org.sfm.map.impl.FieldMapperFactory;
import org.sfm.map.impl.GetterFactory;
import org.sfm.map.impl.fieldmapper.FieldMapperImpl;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Setter;
import org.sfm.reflect.TypeHelper;

import java.lang.reflect.Type;

public final class TupleFieldMapperFactory implements FieldMapperFactory<Tuple, TupleElementKey> {

	private final GetterFactory<Tuple, TupleElementKey>  getterFactory;

	public TupleFieldMapperFactory(GetterFactory<Tuple, TupleElementKey> getterFactory) {
		this.getterFactory = getterFactory;
	}

	@Override
	public <T, P> FieldMapper<Tuple, T> newFieldMapper(Type propertyType, Setter<T, P> setter,
			TupleElementKey key, FieldMapperErrorHandler<TupleElementKey> errorHandler, MapperBuilderErrorHandler mappingErrorHandler) {
		final Class<?> type = TypeHelper.toClass(propertyType);
		
		Getter<Tuple, P> getter = getterFactory.newGetter(type, key);
		if (getter == null) {
			mappingErrorHandler.getterNotFound("Could not find getter for " + key + " type " + type);
		}
		
		FieldMapper<Tuple, T> fm =  new FieldMapperImpl<Tuple, T, P>(getter, setter);
		if (errorHandler != null) {
			fm = new FieldErrorHandlerMapper<Tuple, T, TupleElementKey>(key, fm, errorHandler);
		}
		return fm;
	}
}
