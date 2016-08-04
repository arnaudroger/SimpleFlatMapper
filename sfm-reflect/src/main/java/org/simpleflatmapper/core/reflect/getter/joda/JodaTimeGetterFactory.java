package org.simpleflatmapper.core.reflect.getter.joda;

import org.simpleflatmapper.core.reflect.Getter;
import org.simpleflatmapper.core.reflect.getter.GetterFactory;
import org.simpleflatmapper.util.TypeHelper;
import org.simpleflatmapper.util.date.joda.JodaTimeHelper;

import java.lang.reflect.Type;
import java.util.Date;

import static org.simpleflatmapper.util.Asserts.requireNonNull;


public class JodaTimeGetterFactory<T, K> implements GetterFactory<T, K> {

    private final GetterFactory<T, K> dateGetterFactory;

    public JodaTimeGetterFactory(GetterFactory<T, K> dateGetterFactory) {
        requireNonNull("dateGetterFactory", dateGetterFactory);
        this.dateGetterFactory = dateGetterFactory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <P> Getter<T, P> newGetter(Type target, K key, Object... properties) {

        Class<?> clazz = TypeHelper.toClass(target);

        Getter<T, ? extends Date> getter =
                dateGetterFactory.newGetter(java.util.Date.class, key, properties);

        if (JodaTimeHelper.isJodaDateTime(clazz)) {
            return (Getter<T, P>) new JodaDateTimeFromDateGetter<T>(getter, JodaTimeHelper.getDateTimeZoneOrDefault(properties));
        }
        if (JodaTimeHelper.isJodaLocalDate(clazz)) {
            return (Getter<T, P>)  new JodaLocalDateFromDateGetter<T>(getter);
        }
        if (JodaTimeHelper.isJodaLocalDateTime(clazz)) {
            return (Getter<T, P>) new JodaLocalDateTimeFromDateGetter<T>(getter);
        }
        if (JodaTimeHelper.isJodaLocalTime(clazz)) {
            return (Getter<T, P>)  new JodaLocalTimeFromObjectGetter<T>(getter);
        }

        return null;
    }
}
