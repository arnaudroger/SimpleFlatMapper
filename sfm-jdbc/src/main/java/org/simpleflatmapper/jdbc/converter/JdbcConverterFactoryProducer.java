package org.simpleflatmapper.jdbc.converter;

import org.simpleflatmapper.converter.AbstractConverterFactory;
import org.simpleflatmapper.converter.AbstractConverterFactoryProducer;
import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.converter.ConverterFactory;

import java.sql.Date;
import java.sql.Time;
import org.simpleflatmapper.converter.ConvertingTypes;
import org.simpleflatmapper.util.Supplier;
import org.simpleflatmapper.util.SupplierHelper;

//IFJAVA8_START
import org.simpleflatmapper.jdbc.converter.time.DateToLocalDateConverter;
import org.simpleflatmapper.jdbc.converter.time.TimeToLocalTimeConverter;
import org.simpleflatmapper.jdbc.converter.time.TimeToOffsetTimeConverter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
//IFJAVA8_END
import java.util.Calendar;
import java.util.function.Consumer;

public class JdbcConverterFactoryProducer extends AbstractConverterFactoryProducer {
    @Override
    public void produce(Consumer<ConverterFactory> consumer) {
        //IFJAVA8_START
        constantConverter(consumer, Time.class, LocalTime.class, new TimeToLocalTimeConverter());
        constantConverter(consumer, Date.class, LocalDate.class, new DateToLocalDateConverter());
        factoryConverter(consumer, new AbstractConverterFactory<Time, OffsetTime>(Time.class, OffsetTime.class) {
            @Override
            public Converter<Time, OffsetTime> newConverter(ConvertingTypes targetedTypes, Object... params) {
                ZoneOffset zoneOffset = getZoneOffset(params);
                return new TimeToOffsetTimeConverter(zoneOffset);
            }

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

        constantConverter(consumer, Calendar.class, Timestamp.class, new CalendarToTimestampConverter());
        constantConverter(consumer, java.util.Date.class, Timestamp.class, new UtilDateToTimestampConverter());
        constantConverter(consumer, java.util.Date.class, Time.class, new UtilDateToTimeConverter());
        constantConverter(consumer, java.util.Date.class, Date.class, new UtilDateToDateConverter());
    }
}
