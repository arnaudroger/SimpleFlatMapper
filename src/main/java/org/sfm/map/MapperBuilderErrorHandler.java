package org.sfm.map;

import org.sfm.csv.CsvColumnKey;
import org.sfm.map.impl.FieldKey;

import java.lang.reflect.Type;

public interface MapperBuilderErrorHandler {

	static MapperBuilderErrorHandler NULL = new MapperBuilderErrorHandler() {
		@Override
		public void propertyNotFound(Type target, String property) {
		}

		@Override
		public void customFieldError(FieldKey<?> key, String message) {

		}

		@Override
		public void getterNotFound(String msg) {
		}
	};
	void getterNotFound(String msg);
	void propertyNotFound(Type target, String property);
	void customFieldError(FieldKey<?> key, String message);
}
