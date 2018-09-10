package org.simpleflatmapper.test.map.mapper;

import org.junit.Test;
import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.mapper.MapperFieldMapperGetterAdapter;
import org.simpleflatmapper.util.Predicate;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MapperBiFunctionAdapterTest {


    @SuppressWarnings("unchecked")
    @Test
    public void test() throws Exception {
        Predicate<Object> nullChecker = mock(Predicate.class);
        SourceMapper<Object, String> mapper = mock(SourceMapper.class);

        MappingContext mappingContext = mock(MappingContext.class);

        MapperFieldMapperGetterAdapter<Object, String> biFunctionAdapter =
                new MapperFieldMapperGetterAdapter<Object, String>(mapper, nullChecker, 0);

        Object o = new Object();

        when(mapper.map(o, mappingContext)).thenReturn("HEllo", "Bye");
        when(nullChecker.test(any())).thenReturn(false, true);

        assertEquals("HEllo", biFunctionAdapter.get(o, mappingContext));
        assertNull(biFunctionAdapter.get(null, null));
        biFunctionAdapter.toString();
    }
}