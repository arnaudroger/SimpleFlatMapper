package org.sfm.jdbc.jooq;

import java.lang.reflect.Type;

import org.jooq.Record;
import org.sfm.map.GetterFactory;
import org.sfm.reflect.Getter;
import org.sfm.reflect.TypeHelper;
import org.sfm.utils.conv.Converter;
import org.sfm.utils.conv.ConverterFactory;

public class RecordGetterFactory<R extends Record> implements
		GetterFactory<R, JooqFieldKey> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <P> Getter<R, P> newGetter(Type genericType, JooqFieldKey key) {
		Class<Object> propretyClass = TypeHelper.toClass(genericType);
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
		
		Class<P> clazz = TypeHelper.toClass(genericType);
		
		if (TypeHelper.areCompatible(clazz, key.getField().getType())) {
			return new RecordGetter<R, P>(key.getIndex());
		} else {
			return newRecordGetterWithConverter(key.getField().getType(), clazz, key.getIndex());
		}
	}
	
	private <P, F> Getter<R, P> newRecordGetterWithConverter(Class<F> inType, Class<P> outType, int index) {
		Converter<F, P> converter = ConverterFactory.getConverter(inType, outType);
		return new RecordGetterWithConverter<R, P, F>(index, converter);
	}

}
