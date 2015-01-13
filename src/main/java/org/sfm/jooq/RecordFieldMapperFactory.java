package org.sfm.jooq;

import org.jooq.Record;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.impl.FieldErrorHandlerMapper;
import org.sfm.map.impl.FieldMapper;
import org.sfm.map.impl.FieldMapperFactory;
import org.sfm.map.impl.fieldmapper.FieldMapperImpl;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Setter;

import java.lang.reflect.Type;

public class RecordFieldMapperFactory<R extends Record> implements
		FieldMapperFactory<R, JooqFieldKey> {

	private RecordGetterFactory<R> getterFactory;

	public RecordFieldMapperFactory(RecordGetterFactory<R> recordGetterFactory) {
		this.getterFactory = recordGetterFactory;
	}

	@Override
	public <T, P> FieldMapper<R, T> newFieldMapper(Type type, Setter<T, P> setter,
			JooqFieldKey key, FieldMapperErrorHandler<JooqFieldKey> errorHandler,
			MapperBuilderErrorHandler mapperErrorHandler) {
		Getter<R, P> getter = getterFactory.newGetter(type, key);
		if (getter == null) {
			mapperErrorHandler.getterNotFound("Could not find getter for " + key + " type " + type);
		}
		FieldMapper<R, T> fm =  new FieldMapperImpl<R, T, P>(getter, setter);
		if (errorHandler != null) {
			fm = new FieldErrorHandlerMapper<R, T, JooqFieldKey>(key, fm, errorHandler);
		}
		return fm;
	}

}
