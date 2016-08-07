package org.simpleflatmapper.converter.joda;

import org.joda.time.DateTime;
import org.simpleflatmapper.converter.AbstractConverterFactory;
import org.simpleflatmapper.converter.AbstractConverterFactoryProducer;
import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.converter.ConverterFactory;
import org.simpleflatmapper.converter.ConvertingTypes;
import org.simpleflatmapper.converter.joda.impl.DateToJodaDateTimeConverter;
import org.simpleflatmapper.converter.joda.impl.JodaDateTimeTojuDateConverter;
import org.simpleflatmapper.converter.joda.impl.JodaInstantTojuDateConverter;
import org.simpleflatmapper.converter.joda.impl.JodaLocalDateTimeTojuDateConverter;
import org.simpleflatmapper.converter.joda.impl.JodaLocalDateTojuDateConverter;
import org.simpleflatmapper.converter.joda.impl.JodaLocalTimeTojuDateConverter;
import org.simpleflatmapper.converter.joda.impl.JodaTimeHelper;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.function.Consumer;

public class JodaTimeConverterFactoryProducer extends AbstractConverterFactoryProducer {
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

    @Override
    public void produce(Consumer<ConverterFactory> consumer) {
        factoryConverter(consumer, new AbstractConverterFactory(Date.class, DateTime.class) {
            @Override
            public Converter<?, ?> newConverter(ConvertingTypes targetedTypes, Object... params) {
                return new DateToJodaDateTimeConverter(JodaTimeHelper.getDateTimeZoneOrDefault(params));
            }
        });

    }
}
