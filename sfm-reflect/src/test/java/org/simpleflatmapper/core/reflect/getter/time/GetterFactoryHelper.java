package org.simpleflatmapper.core.reflect.getter.time;

import org.simpleflatmapper.core.reflect.Getter;
import org.simpleflatmapper.core.reflect.getter.GetterFactory;
import org.simpleflatmapper.util.date.time.ZoneIdSupplier;

import java.time.ZoneId;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetterFactoryHelper {

    public static <T> Getter<Object, T> getGetter(Class<T> clazz, Object value, Object ... values) throws Exception {

        Getter<Object, Object> subGetter = mock(Getter.class);

        GetterFactory<Object, String> delegate = mock(GetterFactory.class);
        when(delegate.<Object>newGetter(any(), any(), any())).thenReturn(subGetter);

        JavaTimeGetterFactory<Object, String> getterFactory = new JavaTimeGetterFactory<Object, String>(delegate);

        when(subGetter.get(any())).thenReturn(value, values);

        return getterFactory.newGetter(clazz, null,
                new ZoneIdSupplier() {
                    @Override
                    public ZoneId get() {
                        return ZoneId.systemDefault();
                    }
                });
    }
}
