package org.sfm.map.error;

import org.sfm.map.FieldKey;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.MapperBuildingException;
import org.sfm.utils.ErrorDoc;

import java.lang.reflect.Type;

public final class RethrowMapperBuilderErrorHandler implements
		MapperBuilderErrorHandler {

	@Override
	public void accessorNotFound(final String msg) {
		throw new MapperBuildingException(msg);
	}

	@Override
	public void propertyNotFound(final Type target, final String property) {
		throw new MapperBuildingException("Could not find eligible property for " + property + " on  " + target + " not found "
				+ " See " + ErrorDoc.toUrl("PROPERTY_NOT_FOUND"));
	}

	@Override
	public void customFieldError(FieldKey<?> key, String message) {
		throw new MapperBuildingException(message);
	}

}
