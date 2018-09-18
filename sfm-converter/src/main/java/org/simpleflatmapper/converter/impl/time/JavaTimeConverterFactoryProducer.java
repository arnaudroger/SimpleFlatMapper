package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.AbstractContextualConverterFactory;
import org.simpleflatmapper.converter.AbstractConverterFactory;
import org.simpleflatmapper.converter.AbstractContextualConverterFactoryProducer;
import org.simpleflatmapper.converter.ContextFactoryBuilder;
import org.simpleflatmapper.converter.ContextualConverter;
import org.simpleflatmapper.converter.ContextualConverterFactory;
import org.simpleflatmapper.converter.ConvertingTypes;
import org.simpleflatmapper.converter.ToStringConverter;
import org.simpleflatmapper.util.Consumer;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Date;

public class JavaTimeConverterFactoryProducer extends AbstractContextualConverterFactoryProducer {


    @Override
    public void produce(Consumer<? super ContextualConverterFactory<?, ?>> consumer) {
        constantConverter(consumer, Instant.class, Date.class, new JavaInstantTojuDateConverter());
        factoryConverter(consumer, new AbstractContextualConverterFactory<LocalDateTime, Date>(LocalDateTime.class, Date.class) {
            @Override
            public ContextualConverter<? super LocalDateTime, ? extends Date> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new JavaLocalDateTimeTojuDateConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractContextualConverterFactory<LocalDate, Date>(LocalDate.class, Date.class) {
            @Override
            public ContextualConverter<? super LocalDate, ? extends Date> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new JavaLocalDateTojuDateConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractContextualConverterFactory<LocalTime, Date>(LocalTime.class, Date.class) {
            @Override
            public ContextualConverter<? super LocalTime, ? extends Date> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new JavaLocalTimeTojuDateConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        constantConverter(consumer, ZonedDateTime.class, Date.class, new JavaZonedDateTimeTojuDateConverter());
        constantConverter(consumer, OffsetDateTime.class, Date.class, new JavaOffsetDateTimeTojuDateConverter());
        constantConverter(consumer, OffsetTime.class, Date.class, new JavaOffsetTimeTojuDateConverter());
        factoryConverter(consumer, new AbstractContextualConverterFactory<YearMonth, Date>(YearMonth.class, Date.class) {
            @Override
            public ContextualConverter<? super YearMonth, ? extends Date> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new JavaYearMonthTojuDateConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractContextualConverterFactory<Year, Date>(Year.class, Date.class) {
            @Override
            public ContextualConverter<? super Year, ? extends Date> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new JavaYearTojuDateConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });

        constantConverter(consumer, Date.class, Instant.class, new DateToJavaInstantConverter());
        factoryConverter(consumer, new AbstractContextualConverterFactory<Date, LocalDateTime>(Date.class, LocalDateTime.class) {
            @Override
            public ContextualConverter<? super Date, ? extends LocalDateTime> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new DateToJavaLocalDateTimeConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractContextualConverterFactory<Date, LocalDate>(Date.class, LocalDate.class) {
            @Override
            public ContextualConverter<? super Date, ? extends LocalDate> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new DateToJavaLocalDateConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractContextualConverterFactory<Date, LocalTime>(Date.class, LocalTime.class) {
            @Override
            public ContextualConverter<? super Date, ? extends LocalTime> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new DateToJavaLocalTimeConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractContextualConverterFactory<Date, ZonedDateTime>(Date.class, ZonedDateTime.class) {
            @Override
            public ContextualConverter<? super Date, ? extends ZonedDateTime> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new DateToJavaZonedDateTimeConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractContextualConverterFactory<Date, OffsetDateTime>(Date.class, OffsetDateTime.class) {
            @Override
            public ContextualConverter<? super Date, ? extends OffsetDateTime> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new DateToJavaOffsetDateTimeConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractContextualConverterFactory<Date, OffsetTime>(Date.class, OffsetTime.class) {
            @Override
            public ContextualConverter<? super Date, ? extends OffsetTime> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new DateToJavaOffsetTimeConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractContextualConverterFactory<Date, YearMonth>(Date.class, YearMonth.class) {
            @Override
            public ContextualConverter<? super Date, ? extends YearMonth> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new DateToJavaYearMonthConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractContextualConverterFactory<Date, Year>(Date.class, Year.class) {
            @Override
            public ContextualConverter<? super Date, ? extends Year> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new DateToJavaYearConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });

        factoryConverter(consumer, new AbstractContextualConverterFactory<Object, Instant>(Object.class, Instant.class) {
            @Override
            public ContextualConverter<Object, Instant> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new ObjectToJavaInstantConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractContextualConverterFactory<Object, LocalDateTime>(Object.class, LocalDateTime.class) {
            @Override
            public ContextualConverter<Object, LocalDateTime> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new ObjectToJavaLocalDateTimeConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractContextualConverterFactory<Object, LocalDate>(Object.class, LocalDate.class) {
            @Override
            public ContextualConverter<Object, LocalDate> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new ObjectToJavaLocalDateConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractContextualConverterFactory<Object, LocalTime>(Object.class, LocalTime.class) {
            @Override
            public ContextualConverter<Object, LocalTime> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new ObjectToJavaLocalTimeConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractContextualConverterFactory<Object, ZonedDateTime>(Object.class, ZonedDateTime.class) {
            @Override
            public ContextualConverter<Object, ZonedDateTime> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new ObjectToJavaZonedDateTimeConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractContextualConverterFactory<Object, OffsetDateTime>(Object.class, OffsetDateTime.class) {
            @Override
            public ContextualConverter<Object, OffsetDateTime> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new ObjectToJavaOffsetDateTimeConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractContextualConverterFactory<Object, OffsetTime>(Object.class, OffsetTime.class) {
            @Override
            public ContextualConverter<Object, OffsetTime> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new ObjectToJavaOffsetTimeConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractContextualConverterFactory<Object, YearMonth>(Object.class, YearMonth.class) {
            @Override
            public ContextualConverter<Object, YearMonth> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new ObjectToJavaYearMonthConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractContextualConverterFactory<Object, Year>(Object.class, Year.class) {
            @Override
            public ContextualConverter<Object, Year> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                return new ObjectToJavaYearConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });



        factoryConverter(consumer, new AbstractMultiFormatContextualConverterFactory<CharSequence, Instant>(CharSequence.class, Instant.class) {
            @SuppressWarnings("unchecked")
            @Override
            protected ContextualConverter<CharSequence, Instant> newConverter(DateTimeFormatter formatter) {
                return new CharSequenceToInstantConverter(formatter);
            }
        });

        factoryConverter(consumer, new AbstractMultiFormatContextualConverterFactory<CharSequence, LocalDate>(CharSequence.class, LocalDate.class) {
            @SuppressWarnings("unchecked")
            @Override
            protected ContextualConverter<CharSequence, LocalDate> newConverter(DateTimeFormatter formatter) {
                return new CharSequenceToLocalDateConverter(formatter);
            }
        });
        factoryConverter(consumer, new AbstractMultiFormatContextualConverterFactory<CharSequence, LocalDateTime>(CharSequence.class, LocalDateTime.class) {
            @SuppressWarnings("unchecked")
            @Override
            protected ContextualConverter<CharSequence, LocalDateTime> newConverter(DateTimeFormatter formatter) {
                return new CharSequenceToLocalDateTimeConverter(formatter);
            }
        });
        factoryConverter(consumer, new AbstractMultiFormatContextualConverterFactory<CharSequence, LocalTime>(CharSequence.class, LocalTime.class) {
            @SuppressWarnings("unchecked")
            @Override
            protected ContextualConverter<CharSequence, LocalTime> newConverter(DateTimeFormatter formatter) {
                return new CharSequenceToLocalTimeConverter(formatter);
            }
        });
        factoryConverter(consumer, new AbstractMultiFormatContextualConverterFactory<CharSequence, OffsetDateTime>(CharSequence.class, OffsetDateTime.class) {
            @SuppressWarnings("unchecked")
            @Override
            protected ContextualConverter<CharSequence, OffsetDateTime> newConverter(DateTimeFormatter formatter) {
                return new CharSequenceToOffsetDateTimeConverter(formatter);
            }
        });
        factoryConverter(consumer, new AbstractMultiFormatContextualConverterFactory<CharSequence, OffsetTime>(CharSequence.class, OffsetTime.class) {
            @SuppressWarnings("unchecked")
            @Override
            protected ContextualConverter<CharSequence, OffsetTime> newConverter(DateTimeFormatter formatter) {
                return new CharSequenceToOffsetTimeConverter(formatter);
            }
        });
        factoryConverter(consumer, new AbstractMultiFormatContextualConverterFactory<CharSequence, Year>(CharSequence.class, Year.class) {
            @SuppressWarnings("unchecked")
            @Override
            protected ContextualConverter<CharSequence, Year> newConverter(DateTimeFormatter formatter) {
                return new CharSequenceToYearConverter(formatter);
            }
        });
        factoryConverter(consumer, new AbstractMultiFormatContextualConverterFactory<CharSequence, YearMonth>(CharSequence.class, YearMonth.class) {
            @SuppressWarnings("unchecked")
            @Override
            protected ContextualConverter<CharSequence, YearMonth> newConverter(DateTimeFormatter formatter) {
                return new CharSequenceToYearMonthConverter(formatter);
            }
        });
        factoryConverter(consumer, new AbstractMultiFormatContextualConverterFactory<CharSequence, ZonedDateTime>(CharSequence.class, ZonedDateTime.class) {
            @SuppressWarnings("unchecked")
            @Override
            protected ContextualConverter<CharSequence, ZonedDateTime> newConverter(DateTimeFormatter formatter) {
                return new CharSequenceToZonedDateTimeConverter(formatter);
            }
        });

        factoryConverter(consumer, new AbstractContextualConverterFactory<Temporal, String>(Temporal.class, String.class) {
            @Override
            public ContextualConverter<? super Temporal, ? extends String> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
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
