package org.sfm.map;

import java.lang.reflect.Type;

public interface MapperBuilderErrorHandler {

	static MapperBuilderErrorHandler NULL = new MapperBuilderErrorHandler() {
		@Override
		public void propertyNotFound(Type target, String property) {
		}
		
		@Override
		public void getterNotFound(String msg) {
		}
	};
	void getterNotFound(String msg);
	void propertyNotFound(Type target, String property);
}
