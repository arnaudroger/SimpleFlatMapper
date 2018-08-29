package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.AbstractConverterFactory;
import org.simpleflatmapper.converter.AbstractConverterFactoryProducer;
import org.simpleflatmapper.converter.ContextFactoryBuilder;
import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.converter.ConverterFactory;
import org.simpleflatmapper.converter.ConvertingTypes;
import org.simpleflatmapper.converter.ToStringConverter;
import org.simpleflatmapper.util.Consumer;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Date;

public class JavaTimeConverterFactoryProducer extends AbstractConverterFactoryProducer {


    @Override
    public void produce(Consumer<? super ConverterFactory<?, ?>> consumer) {
        constantConverter(consumer, Instant.class, Date.class, new JavaInstantTojuDateConverter());
        factoryConverter(consumer, new AbstractConverterFactory<LocalDateTime, Date>(LocalDateTime.class, Date.class) {
            @Override
            public Converter<? super LocalDateTime, ? extends Date> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new JavaLocalDateTimeTojuDateConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractConverterFactory<LocalDate, Date>(LocalDate.class, Date.class) {
            @Override
            public Converter<? super LocalDate, ? extends Date> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new JavaLocalDateTojuDateConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractConverterFactory<LocalTime, Date>(LocalTime.class, Date.class) {
            @Override
            public Converter<? super LocalTime, ? extends Date> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new JavaLocalTimeTojuDateConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        constantConverter(consumer, ZonedDateTime.class, Date.class, new JavaZonedDateTimeTojuDateConverter());
        constantConverter(consumer, OffsetDateTime.class, Date.class, new JavaOffsetDateTimeTojuDateConverter());
        constantConverter(consumer, OffsetTime.class, Date.class, new JavaOffsetTimeTojuDateConverter());
        factoryConverter(consumer, new AbstractConverterFactory<YearMonth, Date>(YearMonth.class, Date.class) {
            @Override
            public Converter<? super YearMonth, ? extends Date> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new JavaYearMonthTojuDateConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractConverterFactory<Year, Date>(Year.class, Date.class) {
            @Override
            public Converter<? super Year, ? extends Date> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new JavaYearTojuDateConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });

        constantConverter(consumer, Date.class, Instant.class, new DateToJavaInstantConverter());
        factoryConverter(consumer, new AbstractConverterFactory<Date, LocalDateTime>(Date.class, LocalDateTime.class) {
            @Override
            public Converter<? super Date, ? extends LocalDateTime> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new DateToJavaLocalDateTimeConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractConverterFactory<Date, LocalDate>(Date.class, LocalDate.class) {
            @Override
            public Converter<? super Date, ? extends LocalDate> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new DateToJavaLocalDateConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractConverterFactory<Date, LocalTime>(Date.class, LocalTime.class) {
            @Override
            public Converter<? super Date, ? extends LocalTime> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new DateToJavaLocalTimeConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractConverterFactory<Date, ZonedDateTime>(Date.class, ZonedDateTime.class) {
            @Override
            public Converter<? super Date, ? extends ZonedDateTime> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new DateToJavaZonedDateTimeConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractConverterFactory<Date, OffsetDateTime>(Date.class, OffsetDateTime.class) {
            @Override
            public Converter<? super Date, ? extends OffsetDateTime> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new DateToJavaOffsetDateTimeConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractConverterFactory<Date, OffsetTime>(Date.class, OffsetTime.class) {
            @Override
            public Converter<? super Date, ? extends OffsetTime> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new DateToJavaOffsetTimeConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractConverterFactory<Date, YearMonth>(Date.class, YearMonth.class) {
            @Override
            public Converter<? super Date, ? extends YearMonth> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new DateToJavaYearMonthConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractConverterFactory<Date, Year>(Date.class, Year.class) {
            @Override
            public Converter<? super Date, ? extends Year> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new DateToJavaYearConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });

        factoryConverter(consumer, new AbstractConverterFactory<Object, Instant>(Object.class, Instant.class) {
            @Override
            public Converter<Object, Instant> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new ObjectToJavaInstantConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractConverterFactory<Object, LocalDateTime>(Object.class, LocalDateTime.class) {
            @Override
            public Converter<Object, LocalDateTime> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new ObjectToJavaLocalDateTimeConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractConverterFactory<Object, LocalDate>(Object.class, LocalDate.class) {
            @Override
            public Converter<Object, LocalDate> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new ObjectToJavaLocalDateConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractConverterFactory<Object, LocalTime>(Object.class, LocalTime.class) {
            @Override
            public Converter<Object, LocalTime> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new ObjectToJavaLocalTimeConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractConverterFactory<Object, ZonedDateTime>(Object.class, ZonedDateTime.class) {
            @Override
            public Converter<Object, ZonedDateTime> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new ObjectToJavaZonedDateTimeConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractConverterFactory<Object, OffsetDateTime>(Object.class, OffsetDateTime.class) {
            @Override
            public Converter<Object, OffsetDateTime> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new ObjectToJavaOffsetDateTimeConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractConverterFactory<Object, OffsetTime>(Object.class, OffsetTime.class) {
            @Override
            public Converter<Object, OffsetTime> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new ObjectToJavaOffsetTimeConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractConverterFactory<Object, YearMonth>(Object.class, YearMonth.class) {
            @Override
            public Converter<Object, YearMonth> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new ObjectToJavaYearMonthConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractConverterFactory<Object, Year>(Object.class, Year.class) {
            @Override
            public Converter<Object, Year> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new ObjectToJavaYearConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });



        factoryConverter(consumer, new AbstractMultiFormatConverterFactory<CharSequence, Instant>(CharSequence.class, Instant.class) {
            @SuppressWarnings("unchecked")
            @Override
            protected Converter<CharSequence, Instant> newConverter(DateTimeFormatter formatter) {
                return new CharSequenceToInstantConverter(formatter);
            }
        });

        factoryConverter(consumer, new AbstractMultiFormatConverterFactory<CharSequence, LocalDate>(CharSequence.class, LocalDate.class) {
            @SuppressWarnings("unchecked")
            @Override
            protected Converter<CharSequence, LocalDate> newConverter(DateTimeFormatter formatter) {
                return new CharSequenceToLocalDateConverter(formatter);
            }
        });
        factoryConverter(consumer, new AbstractMultiFormatConverterFactory<CharSequence, LocalDateTime>(CharSequence.class, LocalDateTime.class) {
            @SuppressWarnings("unchecked")
            @Override
            protected Converter<CharSequence, LocalDateTime> newConverter(DateTimeFormatter formatter) {
                return new CharSequenceToLocalDateTimeConverter(formatter);
            }
        });
        factoryConverter(consumer, new AbstractMultiFormatConverterFactory<CharSequence, LocalTime>(CharSequence.class, LocalTime.class) {
            @SuppressWarnings("unchecked")
            @Override
            protected Converter<CharSequence, LocalTime> newConverter(DateTimeFormatter formatter) {
                return new CharSequenceToLocalTimeConverter(formatter);
            }
        });
        factoryConverter(consumer, new AbstractMultiFormatConverterFactory<CharSequence, OffsetDateTime>(CharSequence.class, OffsetDateTime.class) {
            @SuppressWarnings("unchecked")
            @Override
            protected Converter<CharSequence, OffsetDateTime> newConverter(DateTimeFormatter formatter) {
                return new CharSequenceToOffsetDateTimeConverter(formatter);
            }
        });
        factoryConverter(consumer, new AbstractMultiFormatConverterFactory<CharSequence, OffsetTime>(CharSequence.class, OffsetTime.class) {
            @SuppressWarnings("unchecked")
            @Override
            protected Converter<CharSequence, OffsetTime> newConverter(DateTimeFormatter formatter) {
                return new CharSequenceToOffsetTimeConverter(formatter);
            }
        });
        factoryConverter(consumer, new AbstractMultiFormatConverterFactory<CharSequence, Year>(CharSequence.class, Year.class) {
            @SuppressWarnings("unchecked")
            @Override
            protected Converter<CharSequence, Year> newConverter(DateTimeFormatter formatter) {
                return new CharSequenceToYearConverter(formatter);
            }
        });
        factoryConverter(consumer, new AbstractMultiFormatConverterFactory<CharSequence, YearMonth>(CharSequence.class, YearMonth.class) {
            @SuppressWarnings("unchecked")
            @Override
            protected Converter<CharSequence, YearMonth> newConverter(DateTimeFormatter formatter) {
                return new CharSequenceToYearMonthConverter(formatter);
            }
        });
        factoryConverter(consumer, new AbstractMultiFormatConverterFactory<CharSequence, ZonedDateTime>(CharSequence.class, ZonedDateTime.class) {
            @SuppressWarnings("unchecked")
            @Override
            protected Converter<CharSequence, ZonedDateTime> newConverter(DateTimeFormatter formatter) {
                return new CharSequenceToZonedDateTimeConverter(formatter);
            }
        });

        factoryConverter(consumer, new AbstractConverterFactory<Temporal, String>(Temporal.class, String.class) {
            @Override
            public Converter<? super Temporal, ? extends String> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                DateTimeFormatter dateTimeFormatter = JavaTimeHelper.getDateTimeFormatter(params);
                if (dateTimeFormatter != null) {
                    return new JavaTemporalToStringConverter(dateTimeFormatter);
                } else {
                    return ToStringConverter.INSTANCE;
                }
            }
        });
    }
}
