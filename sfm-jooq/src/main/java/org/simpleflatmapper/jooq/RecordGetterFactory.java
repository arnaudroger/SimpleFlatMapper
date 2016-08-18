package org.simpleflatmapper.jooq;

import org.jooq.Record;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.jooq.getter.RecordGetter;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;

public class RecordGetterFactory<R extends Record> implements
		GetterFactory<R, JooqFieldKey> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <P> Getter<R, P> newGetter(Type genericType, JooqFieldKey key, Object... properties) {
		Class<P> propertyClass = TypeHelper.toClass(genericType);

		if (TypeHelper.areCompatible(propertyClass, TypeHelper.toClass(key.getType(genericType)))) {
			return new RecordGetter<R, P>(key.getIndex());
		}

		return null;
	}
	

}
