package org.simpleflatmapper.map;

import java.lang.reflect.Type;

public interface MapperBuilderErrorHandler {

	MapperBuilderErrorHandler NULL = new MapperBuilderErrorHandler() {
		@Override
		public void propertyNotFound(Type target, String property) {
		}

		@Override
		public void customFieldError(FieldKey<?> key, String message) {

		}

		@Override
		public void accessorNotFound(String msg) {
		}
	};

	void accessorNotFound(String msg);
	void propertyNotFound(Type target, String property);
	void customFieldError(FieldKey<?> key, String message);
}
