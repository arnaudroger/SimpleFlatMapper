package org.simpleflatmapper.map;


import java.lang.reflect.Type;

public class IgnoreMapperBuilderErrorHandler implements MapperBuilderErrorHandler {

    public static final IgnoreMapperBuilderErrorHandler INSTANCE = new IgnoreMapperBuilderErrorHandler();

    private IgnoreMapperBuilderErrorHandler() {
    }

    @Override
    public void accessorNotFound(final String msg) {
        throw new MapperBuildingException(msg);
    }

    @Override
    public void propertyNotFound(final Type target, final String property) {
    }

    @Override
    public void customFieldError(FieldKey<?> key, String message) {
    }

}
