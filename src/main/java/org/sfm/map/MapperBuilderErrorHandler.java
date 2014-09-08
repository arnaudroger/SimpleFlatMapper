package org.sfm.map;

import java.lang.reflect.Type;

public interface MapperBuilderErrorHandler {

	void getterNotFound(String msg);
	void setterNotFound(Type target, String property);
}
