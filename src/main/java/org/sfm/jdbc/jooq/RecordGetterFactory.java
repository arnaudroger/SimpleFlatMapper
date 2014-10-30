package org.sfm.jdbc.jooq;

import java.lang.reflect.Type;

import org.jooq.Field;
import org.jooq.Record;
import org.sfm.map.GetterFactory;
import org.sfm.reflect.Getter;
import org.sfm.reflect.TypeHelper;

public class RecordGetterFactory<R extends Record> implements
		GetterFactory<R, JooqFieldKey> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <P> Getter<R, P> newGetter(Type genericType, JooqFieldKey key) {
		Class<Object> propretyClass = TypeHelper.toClass(genericType);
		if (Enum.class.isAssignableFrom(propretyClass)) {
			Class<?> columnType = key.getField().getType();
			
			if (Number.class.isAssignableFrom(columnType)) {
				return new EnumRecordOrdinalGetter(key, propretyClass);
			} else if (String.class.equals(columnType)){
				return new EnumRecordNamedGetter(key, propretyClass);
			} else {
				return null;
			}
 			
		}
		
		return new RecordGetter<R, P>((Field<P>) key.getField());
	}

}
