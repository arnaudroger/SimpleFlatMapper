package org.sfm.map;

public interface MapperBuilderErrorHandler {

	void getterNotFound(String msg);
	void setterNotFound(Class<?> target, String property);
}
