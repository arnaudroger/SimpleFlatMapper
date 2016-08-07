package org.simpleflatmapper.converter.joda;

import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.util.Date;

public class JodaTimeConverterFactory {
    public static <F, P> Converter<F, P> getConverterFrom(Class<F> inType, Type outType, Object... params) {
        if (TypeHelper.areEquals(Date.class, outType)) {
            if (JodaTimeHelper.isJodaLocalDateTime(inType)) {
                return (Converter<F, P>) new JodaLocalDateTimeTojuDateConverter(JodaTimeHelper.getDateTimeZoneOrDefault(params));
            }
            if (JodaTimeHelper.isJodaLocalTime(inType)) {
                return (Converter<F, P>) new JodaLocalTimeTojuDateConverter(JodaTimeHelper.getDateTimeZoneOrDefault(params));
            }
            if (JodaTimeHelper.isJodaLocalDate(inType)) {
                return (Converter<F, P>) new JodaLocalDateTojuDateConverter();
            }
            if (JodaTimeHelper.isJodaDateTime(inType)) {
                return (Converter<F, P>) new JodaDateTimeTojuDateConverter();
            }
            if (JodaTimeHelper.isJodaInstant(inType)) {
                return (Converter<F, P>) new JodaInstantTojuDateConverter();
            }
        }
        return null;
    }
}
