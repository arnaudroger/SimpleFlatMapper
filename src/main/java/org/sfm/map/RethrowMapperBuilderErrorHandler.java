package org.sfm.map;

public class RethrowMapperBuilderErrorHandler implements
		MapperBuilderErrorHandler {

	@Override
	public void getterNotFound(String msg) {
		throw new IllegalArgumentException(msg);
	}

	@Override
	public void setterNotFound(Class<?> target, String property) {
		throw new IllegalArgumentException("Setter for " + property + " on  " + target + " not found");
	}

}
