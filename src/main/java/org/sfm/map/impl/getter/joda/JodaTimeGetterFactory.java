package org.sfm.map.impl.getter.joda;

import org.sfm.map.ColumnDefinition;
import org.sfm.map.FieldKey;
import org.sfm.map.GetterFactory;
import org.sfm.map.impl.JodaTimeClasses;
import org.sfm.reflect.Getter;
import org.sfm.reflect.TypeHelper;

import java.lang.reflect.Type;
import java.util.Date;

import static org.sfm.utils.Asserts.requireNonNull;

public class JodaTimeGetterFactory<T, K extends FieldKey<K>> implements GetterFactory<T, K> {

    private final GetterFactory<T, K> dateGetterFactory;

    public JodaTimeGetterFactory(GetterFactory<T, K> dateGetterFactory) {
        requireNonNull("dateGetterFactory", dateGetterFactory);
        this.dateGetterFactory = dateGetterFactory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <P> Getter<T, P> newGetter(Type target, K key, ColumnDefinition<?, ?> columnDefinition) {

        Class<?> clazz = TypeHelper.toClass(target);

        Getter<T, ? extends Date> getter =
                dateGetterFactory.newGetter(java.util.Date.class, key, columnDefinition);

        if (JodaTimeClasses.isJodaDateTime(clazz)) {
            return (Getter<T, P>) new JodaDateTimeFromDateGetter<T>(getter);
        }
        if (JodaTimeClasses.isJodaLocalDate(clazz)) {
            return (Getter<T, P>)  new JodaLocalDateFromDateGetter<T>(getter);
        }
        if (JodaTimeClasses.isJodaLocalDateTime(clazz)) {
            return (Getter<T, P>) new JodaLocalDateTimeFromDateGetter<T>(getter);
        }
        if (JodaTimeClasses.isJodaLocalTime(clazz)) {
            return (Getter<T, P>)  new JodaLocalTimeFromObjectGetter<T>(getter);
        }

        return null;
    }
}
