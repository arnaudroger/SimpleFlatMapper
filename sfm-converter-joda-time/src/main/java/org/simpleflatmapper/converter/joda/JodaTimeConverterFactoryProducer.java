package org.simpleflatmapper.converter.joda;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormatter;
import org.simpleflatmapper.converter.AbstractContextualConverterFactory;
import org.simpleflatmapper.converter.AbstractConverterFactory;
import org.simpleflatmapper.converter.AbstractContextualConverterFactoryProducer;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextFactoryBuilder;
import org.simpleflatmapper.converter.ContextualConverter;
import org.simpleflatmapper.converter.ContextualConverterFactory;
import org.simpleflatmapper.converter.ConvertingTypes;
import org.simpleflatmapper.converter.ToStringConverter;
import org.simpleflatmapper.converter.joda.impl.AbstractMultiFormatContextualConverterFactory;
import org.simpleflatmapper.converter.joda.impl.CharSequenceToJodaDateTimeConverter;
import org.simpleflatmapper.converter.joda.impl.CharSequenceToJodaInstantConverter;
import org.simpleflatmapper.converter.joda.impl.CharSequenceToJodaLocalDateConverter;
import org.simpleflatmapper.converter.joda.impl.CharSequenceToJodaLocalDateTimeConverter;
import org.simpleflatmapper.converter.joda.impl.CharSequenceToJodaLocalTimeConverter;
import org.simpleflatmapper.converter.joda.impl.DateToJodaDateTimeConverter;
import org.simpleflatmapper.converter.joda.impl.DateToJodaInstantConverter;
import org.simpleflatmapper.converter.joda.impl.DateToJodaLocalDateConverter;
import org.simpleflatmapper.converter.joda.impl.DateToJodaLocalDateTimeConverter;
import org.simpleflatmapper.converter.joda.impl.DateToJodaLocalTimeConverter;
import org.simpleflatmapper.converter.joda.impl.JodaDateTimeTojuDateConverter;
import org.simpleflatmapper.converter.joda.impl.JodaInstantTojuDateConverter;
import org.simpleflatmapper.converter.joda.impl.JodaLocalDateTimeTojuDateConverter;
import org.simpleflatmapper.converter.joda.impl.JodaLocalDateTojuDateConverter;
import org.simpleflatmapper.converter.joda.impl.JodaLocalTimeTojuDateConverter;
import org.simpleflatmapper.converter.joda.impl.JodaReadableInstantToStringConverter;
import org.simpleflatmapper.converter.joda.impl.JodaReadablePartialToStringConverter;
import org.simpleflatmapper.converter.joda.impl.JodaTimeHelper;
import org.simpleflatmapper.util.Consumer;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class JodaTimeConverterFactoryProducer extends AbstractContextualConverterFactoryProducer {

    @Override
    public void produce(Consumer<? super ContextualConverterFactory<?, ?>> consumer) {
        // Date to joda time
        factoryConverter(consumer, new AbstractContextualConverterFactory<Date, DateTime>(Date.class, DateTime.class) {
            @Override
            public ContextualConverter<Date, DateTime> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new DateToJodaDateTimeConverter(JodaTimeHelper.getDateTimeZoneOrDefault(params));
            }
        });
        constantConverter(consumer, Date.class, Instant.class, new DateToJodaInstantConverter());
        constantConverter(consumer, Date.class, LocalDate.class, new DateToJodaLocalDateConverter());
        constantConverter(consumer, Date.class, LocalDateTime.class, new DateToJodaLocalDateTimeConverter());
        constantConverter(consumer, Date.class, LocalTime.class, new DateToJodaLocalTimeConverter());

        // joda time to date
        constantConverter(consumer, DateTime.class, Date.class, new JodaDateTimeTojuDateConverter());
        constantConverter(consumer, Instant.class, Date.class, new JodaInstantTojuDateConverter());
        constantConverter(consumer, LocalDate.class, Date.class, new JodaLocalDateTojuDateConverter());
        factoryConverter(consumer, new AbstractContextualConverterFactory<LocalDateTime, Date>(LocalDateTime.class, Date.class) {
            @Override
            public ContextualConverter<LocalDateTime, Date> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new JodaLocalDateTimeTojuDateConverter(JodaTimeHelper.getDateTimeZoneOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractContextualConverterFactory<LocalTime, Date>(LocalTime.class, Date.class) {
            @Override
            public ContextualConverter<LocalTime, Date> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new JodaLocalTimeTojuDateConverter(JodaTimeHelper.getDateTimeZoneOrDefault(params));
            }
        });

        // char sequence to joda time
        factoryConverter(consumer, new AbstractMultiFormatContextualConverterFactory<CharSequence, DateTime>(CharSequence.class, DateTime.class) {
            @Override
            protected ContextualConverter<CharSequence, DateTime> newConverter(DateTimeFormatter formatter) {
                return new CharSequenceToJodaDateTimeConverter(formatter);
            }
        });
        factoryConverter(consumer, new AbstractMultiFormatContextualConverterFactory<CharSequence, Instant>(CharSequence.class, Instant.class) {
            @Override
            protected ContextualConverter<CharSequence, Instant> newConverter(DateTimeFormatter formatter) {
                return new CharSequenceToJodaInstantConverter(formatter);
            }
        });
        factoryConverter(consumer, new AbstractMultiFormatContextualConverterFactory<CharSequence, LocalDate>(CharSequence.class, LocalDate.class) {
            @Override
            protected ContextualConverter<CharSequence, LocalDate> newConverter(DateTimeFormatter formatter) {
                return new CharSequenceToJodaLocalDateConverter(formatter);
            }
        });
        factoryConverter(consumer, new AbstractMultiFormatContextualConverterFactory<CharSequence, LocalDateTime>(CharSequence.class, LocalDateTime.class) {
            @Override
            protected ContextualConverter<CharSequence, LocalDateTime> newConverter(DateTimeFormatter formatter) {
                return new CharSequenceToJodaLocalDateTimeConverter(formatter);
            }
        });
        factoryConverter(consumer, new AbstractMultiFormatContextualConverterFactory<CharSequence, LocalTime>(CharSequence.class, LocalTime.class) {
            @Override
            protected ContextualConverter<CharSequence, LocalTime> newConverter(DateTimeFormatter formatter) {
                return new CharSequenceToJodaLocalTimeConverter(formatter);
            }
        });


        factoryConverter(consumer, new AbstractContextualConverterFactory<ReadableInstant, String>(ReadableInstant.class, String.class) {
            @Override
            public ContextualConverter<? super ReadableInstant, String> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                DateTimeFormatter dateTimeFormatter  = JodaTimeHelper.getDateTimeFormatter(params);
                if (dateTimeFormatter != null) {
                    return new JodaReadableInstantToStringConverter(dateTimeFormatter);
                } else {
                    return ToStringConverter.INSTANCE;
                }
            }
        });
        factoryConverter(consumer, new AbstractContextualConverterFactory<ReadablePartial, String>(ReadablePartial.class, String.class) {
            @Override
            public ContextualConverter<? super ReadablePartial, String> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                DateTimeFormatter dateTimeFormatter  = JodaTimeHelper.getDateTimeFormatter(params);
                if (dateTimeFormatter != null) {
                    return new JodaReadablePartialToStringConverter(dateTimeFormatter);
                } else {
                    return ToStringConverter.INSTANCE;
                }
            }
        });
        
        //IFJAVA8_START
        constantConverter(consumer, LocalDateTime.class, java.time.LocalDateTime.class, new org.simpleflatmapper.converter.joda.impl.time.JodaLocalDateTimeTojuLocalDateTimeConverter());
        constantConverter(consumer, LocalDate.class, java.time.LocalDate.class, new org.simpleflatmapper.converter.joda.impl.time.JodaLocalDateTojuLocalDateConverter());
        constantConverter(consumer, LocalTime.class, java.time.LocalTime.class, new org.simpleflatmapper.converter.joda.impl.time.JodaLocalTimeTojuLocalTimeConverter());
        //IFJAVA8_END
        constantConverter(consumer, LocalTime.class, Long.class, new ContextualConverter<LocalTime, Long>() {
            @Override
            public Long convert(LocalTime in, Context context) throws Exception {
                if (in == null) return null;
                return TimeUnit.MILLISECONDS.toNanos(in.getMillisOfDay());
            }
        });

    }
}
