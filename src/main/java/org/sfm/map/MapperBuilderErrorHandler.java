package org.sfm.map;

import java.lang.reflect.Type;

public interface MapperBuilderErrorHandler {

	void getterNotFound(String msg);
	void propertyNotFound(Type target, String property);
}
