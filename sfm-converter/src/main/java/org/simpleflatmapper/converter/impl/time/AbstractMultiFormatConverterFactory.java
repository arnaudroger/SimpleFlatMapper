package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.AbstractConverterFactory;
import org.simpleflatmapper.converter.ContextFactoryBuilder;
import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.converter.ConvertingTypes;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public abstract class AbstractMultiFormatConverterFactory<I, O> extends AbstractConverterFactory<I, O> {
    public AbstractMultiFormatConverterFactory(Class<I> from, Class<O> to) {
        super(from, to);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Converter<? super I, ? extends O> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {

        DateTimeFormatter[] dateTimeFormatters = JavaTimeHelper.getDateTimeFormatters(params);
        
        if (dateTimeFormatters.length == 0) {
            return null;
        }

        ZoneId zoneId = JavaTimeHelper.getZoneId(params);

        Converter<I, O>[] converters = new Converter[dateTimeFormatters.length];

        for(int i = 0; i < dateTimeFormatters.length; i++) {
            DateTimeFormatter dateTimeFormatter = dateTimeFormatters[i];
            if (dateTimeFormatter.getZone() == null) {
                dateTimeFormatter.withZone(zoneId);
            }
            converters[i] = newConverter(dateTimeFormatter);
        }

        if (converters.length == 1) {
            return converters[0];
        } else {
            return new MultiDateTimeFormatterConverter<I, O>(converters);
        }

    }

    @SuppressWarnings("unchecked")
    protected abstract Converter<I, O> newConverter(DateTimeFormatter formatter);
}
