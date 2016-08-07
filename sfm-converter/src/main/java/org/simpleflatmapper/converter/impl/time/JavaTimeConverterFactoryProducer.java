package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.AbstractConverterFactory;
import org.simpleflatmapper.converter.AbstractConverterFactoryProducer;
import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.converter.ConverterFactory;
import org.simpleflatmapper.converter.ConverterFactoryProducer;
import org.simpleflatmapper.converter.ConvertingTypes;
import org.simpleflatmapper.util.TypeHelper;
import org.simpleflatmapper.util.date.time.JavaTimeHelper;

import java.lang.reflect.Type;
import java.time.*;
import java.util.Date;
import java.util.function.Consumer;

public class JavaTimeConverterFactoryProducer extends AbstractConverterFactoryProducer {


    @Override
    public void produce(Consumer<ConverterFactory> consumer) {
        constantConverter(consumer, Instant.class, Date.class, new JavaInstantTojuDateConverter());
        factoryConverter(consumer, new AbstractConverterFactory(LocalDateTime.class, Date.class) {
            @Override
            public Converter<?, ?> newConverter(ConvertingTypes targetedTypes, Object... params) {
                return new JavaLocalDateTimeTojuDateConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractConverterFactory(LocalDate.class, Date.class) {
            @Override
            public Converter<?, ?> newConverter(ConvertingTypes targetedTypes, Object... params) {
                return new JavaLocalDateTojuDateConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractConverterFactory(LocalTime.class, Date.class) {
            @Override
            public Converter<?, ?> newConverter(ConvertingTypes targetedTypes, Object... params) {
                return new JavaLocalTimeTojuDateConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        constantConverter(consumer, ZonedDateTime.class, Date.class, new JavaZonedDateTimeTojuDateConverter());
        constantConverter(consumer, OffsetDateTime.class, Date.class, new JavaOffsetDateTimeTojuDateConverter());
        constantConverter(consumer, OffsetTime.class, Date.class, new JavaOffsetTimeTojuDateConverter());
        factoryConverter(consumer, new AbstractConverterFactory(YearMonth.class, Date.class) {
            @Override
            public Converter<?, ?> newConverter(ConvertingTypes targetedTypes, Object... params) {
                return new JavaYearMonthTojuDateConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractConverterFactory(Year.class, Date.class) {
            @Override
            public Converter<?, ?> newConverter(ConvertingTypes targetedTypes, Object... params) {
                return new JavaYearTojuDateConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });

        constantConverter(consumer, Date.class, Instant.class, new DateToJavaInstantConverter());
        factoryConverter(consumer, new AbstractConverterFactory(Date.class, LocalDateTime.class) {
            @Override
            public Converter<?, ?> newConverter(ConvertingTypes targetedTypes, Object... params) {
                return new DateToJavaLocalDateTimeConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractConverterFactory(Date.class, LocalDate.class) {
            @Override
            public Converter<?, ?> newConverter(ConvertingTypes targetedTypes, Object... params) {
                return new DateToJavaLocalDateConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractConverterFactory(Date.class, LocalTime.class) {
            @Override
            public Converter<?, ?> newConverter(ConvertingTypes targetedTypes, Object... params) {
                return new DateToJavaLocalTimeConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractConverterFactory(Date.class, ZonedDateTime.class) {
            @Override
            public Converter<?, ?> newConverter(ConvertingTypes targetedTypes, Object... params) {
                return new DateToJavaZonedDateTimeConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractConverterFactory(Date.class, OffsetDateTime.class) {
            @Override
            public Converter<?, ?> newConverter(ConvertingTypes targetedTypes, Object... params) {
                return new DateToJavaOffsetDateTimeConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractConverterFactory(Date.class, OffsetTime.class) {
            @Override
            public Converter<?, ?> newConverter(ConvertingTypes targetedTypes, Object... params) {
                return new DateToJavaOffsetTimeConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractConverterFactory(Date.class, YearMonth.class) {
            @Override
            public Converter<?, ?> newConverter(ConvertingTypes targetedTypes, Object... params) {
                return new DateToJavaYearMonthConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
        factoryConverter(consumer, new AbstractConverterFactory(Date.class, Year.class) {
            @Override
            public Converter<?, ?> newConverter(ConvertingTypes targetedTypes, Object... params) {
                return new DateToJavaYearConverter(JavaTimeHelper.getZoneIdOrDefault(params));
            }
        });
    }
}
