package org.sfm.utils.conv.time;

import org.sfm.map.column.time.JavaTimeHelper;
import org.sfm.reflect.TypeHelper;
import org.sfm.utils.conv.Converter;

import java.lang.reflect.Type;
import java.time.*;
import java.util.Date;

public class JavaTimeConverterFactory {
    public static <F, P> Converter<F, P> getConverterFrom(Class<F> inType, Type outType, Object[] params) {

        if (TypeHelper.areEquals(Date.class, outType)) {
            if (Instant.class.equals(inType)) {
                return (Converter<F, P>) new JavaInstantTojuDateConverter();
            } else if (LocalDateTime.class.equals(inType)) {
                return (Converter<F, P>) new JavaLocalDateTimeTojuDateConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            } else if (LocalDate.class.equals(inType)) {
                return (Converter<F, P>) new JavaLocalDateTojuDateConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            } else if (LocalTime.class.equals(inType)) {
                return (Converter<F, P>) new JavaLocalTimeTojuDateConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            } else if (ZonedDateTime.class.equals(inType)) {
                return (Converter<F, P>) new JavaZonedDateTimeTojuDateConverter();
            } else if (OffsetDateTime.class.equals(inType)) {
                return (Converter<F, P>) new JavaOffsetDateTimeTojuDateConverter();
            } else if (OffsetTime.class.equals(inType)) {
                return (Converter<F, P>) new JavaOffsetTimeTojuDateConverter();
            } else if (YearMonth.class.equals(inType)) {
                return (Converter<F, P>) new JavaYearMonthTojuDateConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            } else if (Year.class.equals(inType)) {
                return (Converter<F, P>) new JavaYearTojuDateConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        }
        return null;
    }

    public static <F, P> Converter<F, P> getConverterTo(Class<F> inType, Type outType, Object[] params) {

        if (TypeHelper.isAssignable(Date.class, inType)) {
            if (Instant.class.equals(outType)) {
                return (Converter<F, P>) new DateToJavaInstantConverter();
            } else if (LocalDateTime.class.equals(outType)) {
                return (Converter<F, P>) new DateToJavaLocalDateTimeConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            } else if (LocalDate.class.equals(outType)) {
                return (Converter<F, P>) new DateToJavaLocalDateConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            } else if (LocalTime.class.equals(outType)) {
                return (Converter<F, P>) new DateToJavaLocalTimeConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            } else if (ZonedDateTime.class.equals(outType)) {
                return (Converter<F, P>) new DateToJavaZonedDateTimeConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            } else if (OffsetDateTime.class.equals(outType)) {
                return (Converter<F, P>) new DateToJavaOffsetDateTimeConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            } else if (OffsetTime.class.equals(outType)) {
                return (Converter<F, P>) new DateToJavaOffsetTimeConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            } else if (YearMonth.class.equals(outType)) {
                return (Converter<F, P>) new DateToJavaYearMonthConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            } else if (Year.class.equals(outType)) {
                return (Converter<F, P>) new DateToJavaYearConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        }
        return null;
    }
}
