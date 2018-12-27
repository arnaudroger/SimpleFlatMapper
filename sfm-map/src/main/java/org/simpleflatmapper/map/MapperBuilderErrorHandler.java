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

	/**
	 * called when sfm could not find a accessor
	 * @param msg the message describing the issue
	 */
	void accessorNotFound(String msg);

	/**
	 * called when the property could not be found on the target type.
	 * @param target the target type
	 * @param property the property name
	 */
	void propertyNotFound(Type target, String property);
	
	void customFieldError(FieldKey<?> key, String message);
}
