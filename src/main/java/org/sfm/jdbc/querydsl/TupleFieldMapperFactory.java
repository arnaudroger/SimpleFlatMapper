package org.sfm.jdbc.querydsl;

import org.sfm.map.FieldErrorHandlerMapper;
import org.sfm.map.FieldMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.FieldMapperFactory;
import org.sfm.map.FieldMapperImpl;
import org.sfm.map.GetterFactory;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Setter;
import org.sfm.reflect.TypeHelper;

import com.mysema.query.Tuple;

public final class TupleFieldMapperFactory implements FieldMapperFactory<Tuple, TupleElementKey> {

	private final GetterFactory<Tuple, TupleElementKey>  getterFactory;

	public TupleFieldMapperFactory(GetterFactory<Tuple, TupleElementKey> getterFactory) {
		this.getterFactory = getterFactory;
	}

	@Override
	public <T, P> FieldMapper<Tuple, T> newFieldMapper(Setter<T, P> setter,
			TupleElementKey key, FieldMapperErrorHandler<TupleElementKey> errorHandler, MapperBuilderErrorHandler mappingErrorHandler) {
		final Class<?> type = TypeHelper.toClass(setter.getPropertyType());
		
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
