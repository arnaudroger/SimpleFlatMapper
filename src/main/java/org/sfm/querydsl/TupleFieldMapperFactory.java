package org.sfm.querydsl;

import com.mysema.query.Tuple;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.GetterFactory;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.impl.*;
import org.sfm.map.impl.fieldmapper.FieldMapperImpl;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Setter;

import java.lang.reflect.Type;

public final class TupleFieldMapperFactory implements FieldMapperFactory<Tuple, TupleElementKey, FieldMapperColumnDefinition<TupleElementKey, Tuple>> {

	private final GetterFactory<Tuple, TupleElementKey> getterFactory;

	public TupleFieldMapperFactory(GetterFactory<Tuple, TupleElementKey> getterFactory) {
		this.getterFactory = getterFactory;
	}

	@Override
	public <T, P> FieldMapper<Tuple, T> newFieldMapper(PropertyMapping<T, P, TupleElementKey , FieldMapperColumnDefinition<TupleElementKey, Tuple>> propertyMapping, FieldMapperErrorHandler<TupleElementKey> errorHandler,
													   MapperBuilderErrorHandler mappingErrorHandler) {

		final Type propertyType = propertyMapping.getPropertyMeta().getType();
		final Setter<T, P> setter = propertyMapping.getPropertyMeta().getSetter();
		final TupleElementKey key = propertyMapping.getColumnKey();

		Getter<Tuple, P> getter = getterFactory.newGetter(propertyType, key);
		if (getter == null) {
			mappingErrorHandler.getterNotFound("Could not find getter for " + key + " type " + propertyType);
		}
		
		FieldMapper<Tuple, T> fm =  new FieldMapperImpl<Tuple, T, P>(getter, setter);
		if (errorHandler != null) {
			fm = new FieldErrorHandlerMapper<Tuple, T, TupleElementKey>(key, fm, errorHandler);
		}
		return fm;
	}
}
