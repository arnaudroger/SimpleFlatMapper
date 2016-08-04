package org.simpleflatmapper.core.map.mapper;

import org.junit.Test;
import org.simpleflatmapper.core.map.Mapper;
import org.simpleflatmapper.util.Predicate;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MapperGetterAdapterTest {


    @Test
    public void test() throws Exception {
        Predicate<Object> nullChecker = mock(Predicate.class);
        Mapper<Object, String> mapper = mock(Mapper.class);

        MapperGetterAdapter<Object, String> getter =
                new MapperGetterAdapter<Object, String>(mapper, nullChecker);

        when(mapper.map(any())).thenReturn("HEllo", "Bye");
        when(nullChecker.test(any())).thenReturn(false, true);

        assertEquals("HEllo", getter.get(null));
        assertNull(getter.get(null));
        getter.toString();
    }

}