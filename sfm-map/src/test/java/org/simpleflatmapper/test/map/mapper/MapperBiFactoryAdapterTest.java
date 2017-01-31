package org.simpleflatmapper.test.map.mapper;

import org.junit.Test;
import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.map.mapper.MapperBiFactoryAdapter;
import org.simpleflatmapper.util.Predicate;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MapperBiFactoryAdapterTest {


    @SuppressWarnings("unchecked")
    @Test
    public void test() throws Exception {
        Predicate<Object> nullChecker = mock(Predicate.class);
        Mapper<Object, String> mapper = mock(Mapper.class);

        MapperBiFactoryAdapter<Object, String> getter =
                new MapperBiFactoryAdapter<Object, String>(mapper, nullChecker, 0);

        when(mapper.map(any(), any())).thenReturn("HEllo", "Bye");
        when(nullChecker.test(any())).thenReturn(false, true);

        assertEquals("HEllo", getter.newInstance(null, null));
        assertNull(getter.newInstance(null, null));
        getter.toString();
    }

}