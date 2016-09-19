package org.simpleflatmapper.map;

import org.junit.Test;

import static org.junit.Assert.*;

public class MapperBuilderErrorHandlerTest {

    @Test
    public void testNull() {
        MapperBuilderErrorHandler.NULL.accessorNotFound(null);
        MapperBuilderErrorHandler.NULL.propertyNotFound(null, null);
        MapperBuilderErrorHandler.NULL.customFieldError(null, null);
    }

}