package org.simpleflatmapper.test.map;

import org.junit.Test;
import org.simpleflatmapper.map.MapperBuilderErrorHandler;

import static org.junit.Assert.*;

public class MapperBuilderErrorHandlerTest {

    @Test
    public void testNull() {
        MapperBuilderErrorHandler.NULL.accessorNotFound(null);
        MapperBuilderErrorHandler.NULL.propertyNotFound(null, null);
        MapperBuilderErrorHandler.NULL.customFieldError(null, null);
    }

}