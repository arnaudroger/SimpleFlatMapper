package org.simpleflatmapper.converter.joda.impl;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.simpleflatmapper.converter.AbstractContextualConverterFactory;
import org.simpleflatmapper.converter.ContextFactoryBuilder;
import org.simpleflatmapper.converter.ContextualConverter;
import org.simpleflatmapper.converter.ConvertingTypes;


public abstract class AbstractMultiFormatContextualConverterFactory<I, O> extends AbstractContextualConverterFactory<I, O> {
    public AbstractMultiFormatContextualConverterFactory(Class<I> from, Class<O> to) {
        super(from, to);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ContextualConverter<? super I, ? extends O> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {

        DateTimeFormatter[] dateTimeFormatters = JodaTimeHelper.getDateTimeFormatters(params);

        DateTimeZone zoneId = JodaTimeHelper.getDateTimeZoneOrDefault(params);

        ContextualConverter<I, O>[] converters = new ContextualConverter[dateTimeFormatters.length];

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
    protected abstract ContextualConverter<I, O> newConverter(DateTimeFormatter formatter);
}
