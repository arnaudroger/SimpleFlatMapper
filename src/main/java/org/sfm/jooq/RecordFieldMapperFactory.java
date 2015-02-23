package org.sfm.jooq;

import org.jooq.Record;
import org.sfm.map.FieldMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.impl.*;
import org.sfm.map.impl.fieldmapper.FieldMapperImpl;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Setter;

import java.lang.reflect.Type;

public class RecordFieldMapperFactory<R extends Record> implements
		FieldMapperFactory<R, JooqFieldKey, FieldMapperColumnDefinition<JooqFieldKey, R>> {

	final private RecordGetterFactory<R> getterFactory;

	public RecordFieldMapperFactory(RecordGetterFactory<R> recordGetterFactory) {
		this.getterFactory = recordGetterFactory;
	}

	@Override
	public <T, P> FieldMapper<R, T> newFieldMapper(PropertyMapping<T, P, JooqFieldKey , FieldMapperColumnDefinition<JooqFieldKey, R>> propertyMapping, FieldMapperErrorHandler<JooqFieldKey> errorHandler,
			MapperBuilderErrorHandler mapperErrorHandler) {

		final Type propertyType = propertyMapping.getPropertyMeta().getType();
		final Setter<T, P> setter = propertyMapping.getPropertyMeta().getSetter();
		final JooqFieldKey key = propertyMapping.getColumnKey();

		Getter<R, P> getter = getterFactory.newGetter(propertyType, key);
		if (getter == null) {
			mapperErrorHandler.getterNotFound("Could not find getter for " + key + " type " + propertyType);
		}
		FieldMapper<R, T> fm =  new FieldMapperImpl<R, T, P>(getter, setter);
		if (errorHandler != null) {
			fm = new FieldErrorHandlerMapper<R, T, JooqFieldKey>(key, fm, errorHandler);
		}
		return fm;
	}

}
