package org.simpleflatmapper.jooq;

import org.jooq.Record;
import org.simpleflatmapper.jooq.conv.JooqConverterFactory;
import org.simpleflatmapper.jooq.getter.EnumRecordNamedGetter;
import org.simpleflatmapper.jooq.getter.EnumRecordOrdinalGetter;
import org.simpleflatmapper.jooq.getter.RecordGetter;
import org.simpleflatmapper.jooq.getter.RecordGetterWithConverter;
import org.simpleflatmapper.core.map.GetterFactory;
import org.simpleflatmapper.core.map.mapper.ColumnDefinition;
import org.simpleflatmapper.core.reflect.Getter;
import org.simpleflatmapper.core.reflect.TypeHelper;
import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.converter.ConverterFactory;

import java.lang.reflect.Type;

public class RecordGetterFactory<R extends Record> implements
		GetterFactory<R, JooqFieldKey> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <P> Getter<R, P> newGetter(Type genericType, JooqFieldKey key, ColumnDefinition<?, ?> columnDefinition) {
		Class<P> propertyClass = TypeHelper.toClass(genericType);
		if (Enum.class.isAssignableFrom(propertyClass)) {
			Class<?> columnType = key.getField().getType();
			
			if (TypeHelper.isNumber(columnType)) {
				return new EnumRecordOrdinalGetter(key, propertyClass);
			} else if (String.class.equals(columnType)){
				return new EnumRecordNamedGetter(key, propertyClass);
			} else {
				return null;
			}
		}
		
		if (TypeHelper.areCompatible(propertyClass, key.getField().getType())) {
			return new RecordGetter<R, P>(key.getIndex());
		} else {
			return newRecordGetterWithConverter(key.getField().getType(), genericType, key.getIndex());
		}
	}
	
	private <P, F> Getter<R, P> newRecordGetterWithConverter(Class<F> inType, Type outType, int index) {
		Converter<F, P> converter = ConverterFactory.getConverter(inType, outType);
		if (converter == null) {
			converter = JooqConverterFactory.getConverter(inType, outType);
		}
		if (converter != null) {
			return new RecordGetterWithConverter<R, P, F>(index, converter);
		}
		return null;
	}

}
