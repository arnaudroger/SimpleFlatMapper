package org.simpleflatmapper.reflect.getter.time;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.util.TypeHelper;
import org.simpleflatmapper.util.Asserts;
import org.simpleflatmapper.util.date.time.JavaTimeHelper;

import java.lang.reflect.Type;
import java.time.*;

public class JavaTimeGetterFactory<T, K> implements GetterFactory<T, K> {

    private final GetterFactory<T, K> getterFactory;

    public JavaTimeGetterFactory(GetterFactory<T, K> getterFactory) {
        Asserts.requireNonNull("getterFactory", getterFactory);
        this.getterFactory = getterFactory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <P> Getter<T, P> newGetter(Type target, K key, Object... properties) {

        Class<?> clazz = TypeHelper.toClass(target);

        Getter<T, ?> getter =
                getterFactory.newGetter(Object.class, key, properties);

        if (Instant.class.equals(clazz)) {
            return (Getter<T, P>) new JavaInstantFromObjectGetter<T>(getter, JavaTimeHelper.getZoneIdOrDefault(properties));
        } else if (LocalDate.class.equals(clazz)) {
            return (Getter<T, P>) new JavaLocalDateFromObjectGetter<T>(getter, JavaTimeHelper.getZoneIdOrDefault(properties));
        } else if (LocalDateTime.class.equals(clazz)) {
            return (Getter<T, P>) new JavaLocalDateTimeFromObjectGetter<T>(getter, JavaTimeHelper.getZoneIdOrDefault(properties));
        } else if (LocalTime.class.equals(clazz)) {
            return (Getter<T, P>) new JavaLocalTimeFromObjectGetter<T>(getter, JavaTimeHelper.getZoneIdOrDefault(properties));
        } else if (OffsetDateTime.class.equals(clazz)) {
            return (Getter<T, P>) new JavaOffsetDateTimeFromObjectGetter<T>(getter, JavaTimeHelper.getZoneIdOrDefault(properties));
        } else if (OffsetTime.class.equals(clazz)) {
            return (Getter<T, P>) new JavaOffsetTimeFromObjectGetter<T>(getter, JavaTimeHelper.getZoneIdOrDefault(properties));
        } else if (Year.class.equals(clazz)) {
            return (Getter<T, P>) new JavaYearFromObjectGetter<T>(getter, JavaTimeHelper.getZoneIdOrDefault(properties));
        } else if (YearMonth.class.equals(clazz)) {
            return (Getter<T, P>) new JavaYearMonthFromObjectGetter<T>(getter, JavaTimeHelper.getZoneIdOrDefault(properties));
        } else if (ZonedDateTime.class.equals(clazz)) {
            return (Getter<T, P>) new JavaZonedDateTimeFromObjectGetter<T>(getter, JavaTimeHelper.getZoneIdOrDefault(properties));
        }

        return null;
    }
}
