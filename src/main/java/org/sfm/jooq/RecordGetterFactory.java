package org.sfm.jooq;

import org.jooq.Record;
import org.sfm.jooq.getter.EnumRecordNamedGetter;
import org.sfm.jooq.getter.EnumRecordOrdinalGetter;
import org.sfm.jooq.getter.RecordGetter;
import org.sfm.jooq.getter.RecordGetterWithConverter;
import org.sfm.map.impl.GetterFactory;
import org.sfm.reflect.Getter;
import org.sfm.reflect.TypeHelper;
import org.sfm.utils.conv.Converter;
import org.sfm.utils.conv.ConverterFactory;

import java.lang.reflect.Type;

public class RecordGetterFactory<R extends Record> implements
		GetterFactory<R, JooqFieldKey> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <P> Getter<R, P> newGetter(Type genericType, JooqFieldKey key) {
		Class<P> propretyClass = TypeHelper.toClass(genericType);
		if (Enum.class.isAssignableFrom(propretyClass)) {
			Class<?> columnType = key.getField().getType();
			
			if (TypeHelper.isNumber(columnType)) {
				return new EnumRecordOrdinalGetter(key, propretyClass);
			} else if (String.class.equals(columnType)){
				return new EnumRecordNamedGetter(key, propretyClass);
			} else {
				return null;
			}
		}
		
		if (TypeHelper.areCompatible(propretyClass, key.getField().getType())) {
			return new RecordGetter<R, P>(key.getIndex());
		} else {
			return newRecordGetterWithConverter(key.getField().getType(), genericType, key.getIndex());
		}
	}
	
	private <P, F> Getter<R, P> newRecordGetterWithConverter(Class<F> inType, Type outType, int index) {
		Converter<F, P> converter = ConverterFactory.getConverter(inType, outType);
		if (converter != null) {
			return new RecordGetterWithConverter<R, P, F>(index, converter);
		} else {
			return null;
		}
	}

}
