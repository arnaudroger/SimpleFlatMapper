package org.simpleflatmapper.core.conv.joda;

import org.simpleflatmapper.core.map.column.joda.JodaHelper;
import org.simpleflatmapper.core.map.column.JodaTimeClasses;
import org.simpleflatmapper.core.reflect.TypeHelper;
import org.simpleflatmapper.core.conv.Converter;

import java.lang.reflect.Type;
import java.util.Date;

public class JodaTimeConverterFactory {
    public static <F, P> Converter<F, P> getConverterFrom(Class<F> inType, Type outType, Object... params) {
        if (TypeHelper.areEquals(Date.class, outType)) {
            if (JodaTimeClasses.isJodaLocalDateTime(inType)) {
                return (Converter<F, P>) new JodaLocalDateTimeTojuDateConverter(JodaHelper.getDateTimeZoneOrDefault(params));
            }
            if (JodaTimeClasses.isJodaLocalTime(inType)) {
                return (Converter<F, P>) new JodaLocalTimeTojuDateConverter(JodaHelper.getDateTimeZoneOrDefault(params));
            }
            if (JodaTimeClasses.isJodaLocalDate(inType)) {
                return (Converter<F, P>) new JodaLocalDateTojuDateConverter();
            }
            if (JodaTimeClasses.isJodaDateTime(inType)) {
                return (Converter<F, P>) new JodaDateTimeTojuDateConverter();
            }
            if (JodaTimeClasses.isJodaInstant(inType)) {
                return (Converter<F, P>) new JodaInstantTojuDateConverter();
            }
        }
        return null;
    }
}
