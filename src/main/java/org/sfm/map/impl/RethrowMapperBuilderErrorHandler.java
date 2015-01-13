package org.sfm.map.impl;

import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.MapperBuildingException;

import java.lang.reflect.Type;

public final class RethrowMapperBuilderErrorHandler implements
		MapperBuilderErrorHandler {

	@Override
	public void getterNotFound(final String msg) {
		throw new MapperBuildingException(msg);
	}

	@Override
	public void propertyNotFound(final Type target, final String property) {
		throw new MapperBuildingException("Setter for " + property + " on  " + target + " not found");
	}

	@Override
	public void customFieldError(FieldKey<?> key, String message) {
		throw new MapperBuildingException(message);
	}

}
