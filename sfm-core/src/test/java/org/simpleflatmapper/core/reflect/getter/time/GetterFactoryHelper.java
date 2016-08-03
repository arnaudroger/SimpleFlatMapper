package org.simpleflatmapper.core.reflect.getter.time;

import org.simpleflatmapper.core.map.GetterFactory;
import org.simpleflatmapper.core.map.column.FieldMapperColumnDefinition;
import org.simpleflatmapper.core.map.column.time.JavaZoneIdProperty;
import org.simpleflatmapper.core.reflect.Getter;
import org.simpleflatmapper.core.samples.SampleFieldKey;

import java.time.ZoneId;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetterFactoryHelper {

    public static <T> Getter<Object, T> getGetter(Class<T> clazz, Object value, Object ... values) throws Exception {

        Getter<Object, Object> subGetter = mock(Getter.class);

        GetterFactory<Object, SampleFieldKey> delegate = mock(GetterFactory.class);
        when(delegate.<Object>newGetter(any(), any(), any())).thenReturn(subGetter);

        JavaTimeGetterFactory<Object, SampleFieldKey> getterFactory = new JavaTimeGetterFactory<Object, SampleFieldKey>(delegate);

        when(subGetter.get(any())).thenReturn(value, values);

        return getterFactory.newGetter(clazz, null,
                FieldMapperColumnDefinition.<SampleFieldKey>identity().add(new JavaZoneIdProperty(ZoneId.systemDefault())));
    }
}
