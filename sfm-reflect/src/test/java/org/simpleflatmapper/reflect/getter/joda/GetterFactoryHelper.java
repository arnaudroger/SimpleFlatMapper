package org.simpleflatmapper.reflect.getter.joda;

import org.joda.time.DateTimeZone;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.util.date.joda.DateTimeZoneSupplier;


import java.lang.reflect.Type;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetterFactoryHelper {

    public static <T> Getter<Object, T> getGetter(Class<T> clazz, Object value, Object ... values) throws Exception {

        Getter<Object, Object> subGetter = mock(Getter.class);

        GetterFactory delegate = mock(GetterFactory.class);
        when(delegate.newGetter(any(Type.class), any(String.class), any(Object[].class))).thenReturn(subGetter);

        JodaTimeGetterFactory<Object, String> getterFactory = new JodaTimeGetterFactory<Object, String>(delegate);

        when(subGetter.get(any())).thenReturn(value, values);

        return getterFactory.newGetter(clazz, null, new DateTimeZoneSupplier() {
            @Override
            public DateTimeZone get() {
                return DateTimeZone.getDefault();
            }
        });
    }
}
