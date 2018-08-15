package org.simpleflatmapper.map.error;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.MapperBuilderErrorHandler;
import org.simpleflatmapper.map.MapperBuildingException;
import org.simpleflatmapper.util.ErrorDoc;

import java.lang.reflect.Type;

public final class RethrowMapperBuilderErrorHandler implements
		MapperBuilderErrorHandler {

	public static RethrowMapperBuilderErrorHandler INSTANCE = new RethrowMapperBuilderErrorHandler();

	private RethrowMapperBuilderErrorHandler(){
	}

	@Override
	public void accessorNotFound(final String msg) {
		throw new MapperBuildingException(msg);
	}

	@Override
	public void propertyNotFound(final Type target, final String property) {
		throw new MapperBuildingException("Could not find eligible property for '" + property + "' on  " + target + " not found "
				+ " See " + ErrorDoc.PROPERTY_NOT_FOUND.toUrl());
	}

	@Override
	public void customFieldError(FieldKey<?> key, String message) {
		throw new MapperBuildingException(message);
	}

}
