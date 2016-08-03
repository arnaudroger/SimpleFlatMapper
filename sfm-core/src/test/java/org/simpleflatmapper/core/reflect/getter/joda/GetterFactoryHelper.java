package org.simpleflatmapper.core.reflect.getter.joda;

import org.joda.time.DateTimeZone;
import org.simpleflatmapper.core.map.GetterFactory;
import org.simpleflatmapper.core.map.column.FieldMapperColumnDefinition;
import org.simpleflatmapper.core.map.column.joda.JodaDateTimeZoneProperty;
import org.simpleflatmapper.core.reflect.Getter;
import org.simpleflatmapper.core.samples.SampleFieldKey;


import java.lang.reflect.Type;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetterFactoryHelper {

    public static <T> Getter<Object, T> getGetter(Class<T> clazz, Object value, Object ... values) throws Exception {

        Getter<Object, Object> subGetter = mock(Getter.class);

        GetterFactory delegate = mock(GetterFactory.class);
        when(delegate.newGetter(any(Type.class), any(SampleFieldKey.class), any(FieldMapperColumnDefinition.class))).thenReturn(subGetter);

        JodaTimeGetterFactory<Object, SampleFieldKey> getterFactory = new JodaTimeGetterFactory<Object, SampleFieldKey>(delegate);

        when(subGetter.get(any())).thenReturn(value, values);

        return getterFactory.newGetter(clazz, null,
                FieldMapperColumnDefinition.<SampleFieldKey>identity().add(new JodaDateTimeZoneProperty(DateTimeZone.getDefault())));
    }
}
