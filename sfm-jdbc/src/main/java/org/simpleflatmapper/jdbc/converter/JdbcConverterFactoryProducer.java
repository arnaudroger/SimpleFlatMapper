package org.simpleflatmapper.jdbc.converter;

import org.simpleflatmapper.converter.AbstractConverterFactory;
import org.simpleflatmapper.converter.AbstractConverterFactoryProducer;
import org.simpleflatmapper.converter.ContextFactoryBuilder;
import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.converter.ConverterFactory;

import java.lang.reflect.Type;
import java.sql.Array;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Time;

import org.simpleflatmapper.converter.ConvertingScore;
import org.simpleflatmapper.converter.ConvertingTypes;
import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.jdbc.ResultSetGetterFactory;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.Supplier;
import org.simpleflatmapper.util.SupplierHelper;

import java.sql.Timestamp;
import org.simpleflatmapper.util.TypeHelper;

//IFJAVA8_START
import org.simpleflatmapper.jdbc.converter.time.DateToLocalDateConverter;
import org.simpleflatmapper.jdbc.converter.time.TimeToLocalTimeConverter;
import org.simpleflatmapper.jdbc.converter.time.TimeToOffsetTimeConverter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
//IFJAVA8_END
import java.util.Calendar;
import java.util.List;

public class JdbcConverterFactoryProducer extends AbstractConverterFactoryProducer {
    @Override
    public void produce(Consumer<? super ConverterFactory<?, ?>> consumer) {
        //IFJAVA8_START
        constantConverter(consumer, Time.class, LocalTime.class, new TimeToLocalTimeConverter());
        constantConverter(consumer, Date.class, LocalDate.class, new DateToLocalDateConverter());
        factoryConverter(consumer, new AbstractConverterFactory<Time, OffsetTime>(Time.class, OffsetTime.class) {
            @Override
            public Converter<Time, OffsetTime> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                ZoneOffset zoneOffset = getZoneOffset(params);
                return new TimeToOffsetTimeConverter(zoneOffset);
            }

            @SuppressWarnings("unchecked")
            private ZoneOffset getZoneOffset(Object[] params) {
                for(Object prop : params) {
                    if (prop instanceof ZoneOffset) {
                        return (ZoneOffset) prop;
                    } else if (SupplierHelper.isSupplierOf(prop, ZoneOffset.class)) {
                        return ((Supplier<ZoneOffset>)prop).get();
                    }
                }

                return ZoneOffset.UTC;
            }
        });
        //IFJAVA8_END

        factoryConverter(consumer, new AbstractConverterFactory<Array, Object>(Array.class, java.lang.reflect.Array.class) {
            @Override
            public Converter<? super Array, ? extends Object> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder,  Object... params) {
                Type elementType = TypeHelper.getComponentTypeOfListOrArray(targetedTypes.getTo());
                Getter<? super ResultSet, ?> getter = ResultSetGetterFactory.INSTANCE.newGetter(elementType, new JdbcColumnKey("elt", 2), params);
                return new SqlArrayToJavaArrayConverter<Object>(TypeHelper.<Object>toClass(elementType), getter);
            }

            @Override
            public ConvertingScore score(ConvertingTypes targetedTypes) {
                return new ConvertingScore(super.score(targetedTypes).getFromScore(), TypeHelper.isArray(targetedTypes.getTo()) ? 1 : -1 );
            }
        });
        factoryConverter(consumer, new AbstractConverterFactory<Array, List>(Array.class, List.class) {
            @Override
            public Converter<? super Array, ? extends List> newConverter(ConvertingTypes targetedTypes, ContextFactoryBuilder contextFactoryBuilder, Object... params) {
                Type elementType = TypeHelper.getComponentTypeOfListOrArray(targetedTypes.getTo());
                Getter<? super ResultSet, ?> getter = ResultSetGetterFactory.INSTANCE.newGetter(elementType, new JdbcColumnKey("elt", 2), params);
                return new SqlArrayToListConverter<Object>( getter);
            }
        });

        constantConverter(consumer, Calendar.class, Timestamp.class, new CalendarToTimestampConverter());
        constantConverter(consumer, java.util.Date.class, Timestamp.class, new UtilDateToTimestampConverter());
        constantConverter(consumer, java.util.Date.class, Time.class, new UtilDateToTimeConverter());
        constantConverter(consumer, java.util.Date.class, Date.class, new UtilDateToDateConverter());
    }
}
