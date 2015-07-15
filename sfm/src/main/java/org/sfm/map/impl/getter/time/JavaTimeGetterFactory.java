package org.sfm.map.impl.getter.time;

import org.sfm.map.ColumnDefinition;
import org.sfm.map.FieldKey;
import org.sfm.map.GetterFactory;
import org.sfm.map.column.time.JavaTimeHelper;
import org.sfm.reflect.Getter;
import org.sfm.reflect.TypeHelper;
import org.sfm.utils.Asserts;

import java.lang.reflect.Type;
import java.time.*;

public class JavaTimeGetterFactory<T, K extends FieldKey<K>> implements GetterFactory<T, K> {

    private final GetterFactory<T, K> getterFactory;

    public JavaTimeGetterFactory(GetterFactory<T, K> getterFactory) {
        Asserts.requireNonNull("getterFactory", getterFactory);
        this.getterFactory = getterFactory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <P> Getter<T, P> newGetter(Type target, K key, ColumnDefinition<?, ?> columnDefinition) {

        Class<?> clazz = TypeHelper.toClass(target);

        Getter<T, ?> getter =
                getterFactory.newGetter(Object.class, key, columnDefinition);

        if (Instant.class.equals(clazz)) {
            return (Getter<T, P>) new JavaInstantFromObjectGetter<T>(getter);
        } else if (LocalDate.class.equals(clazz)) {
            return (Getter<T, P>) new JavaLocalDateFromObjectGetter<T>(getter, JavaTimeHelper.getZoneIdOrDefault(columnDefinition));
        } else if (LocalDateTime.class.equals(clazz)) {
            return (Getter<T, P>) new JavaLocalDateTimeFromObjectGetter<T>(getter, JavaTimeHelper.getZoneIdOrDefault(columnDefinition));
        } else if (LocalTime.class.equals(clazz)) {
            return (Getter<T, P>) new JavaLocalTimeFromObjectGetter<T>(getter, JavaTimeHelper.getZoneIdOrDefault(columnDefinition));
        } else if (OffsetDateTime.class.equals(clazz)) {
            return (Getter<T, P>) new JavaOffsetDateTimeFromObjectGetter<T>(getter, JavaTimeHelper.getZoneIdOrDefault(columnDefinition));
        } else if (OffsetTime.class.equals(clazz)) {
            return (Getter<T, P>) new JavaOffsetTimeFromObjectGetter<T>(getter, JavaTimeHelper.getZoneIdOrDefault(columnDefinition));
        } else if (Year.class.equals(clazz)) {
            return (Getter<T, P>) new JavaYearFromObjectGetter<T>(getter, JavaTimeHelper.getZoneIdOrDefault(columnDefinition));
        } else if (YearMonth.class.equals(clazz)) {
            return (Getter<T, P>) new JavaYearMonthFromObjectGetter<T>(getter, JavaTimeHelper.getZoneIdOrDefault(columnDefinition));
        } else if (ZonedDateTime.class.equals(clazz)) {
            return (Getter<T, P>) new JavaZonedDateTimeFromObjectGetter<T>(getter, JavaTimeHelper.getZoneIdOrDefault(columnDefinition));
        }

        return null;
    }
}
