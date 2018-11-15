package org.simpleflatmapper.datastax.impl.converter;

import com.datastax.driver.core.LocalDate;
import org.simpleflatmapper.converter.AbstractContextualConverterFactoryProducer;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;
import org.simpleflatmapper.converter.ContextualConverterFactory;
import org.simpleflatmapper.util.Consumer;

//IFJAVA8_START
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
//IFJAVA8_END

import java.util.Calendar;
import java.util.Date;

public class DatastaxConverterFactoryProducer extends AbstractContextualConverterFactoryProducer {
    @Override
    public void produce(Consumer<? super ContextualConverterFactory<?, ?>> consumer) {
//IFJAVA8_START
        this.constantConverter(consumer, Year.class, LocalDate.class, new ContextualConverter<Year, LocalDate>() {
            @Override
            public LocalDate convert(Year in, Context context) throws Exception {
                if (in == null) return null;
                return LocalDate.fromYearMonthDay(in.getValue(), 1, 1);
            }
        });
        this.constantConverter(consumer, YearMonth.class, LocalDate.class, new ContextualConverter<YearMonth, LocalDate>() {
            @Override
            public LocalDate convert(YearMonth in, Context context) throws Exception {
                if (in == null) return null;
                return LocalDate.fromYearMonthDay(in.getYear(), in.getMonthValue(), 1);
            }
        });
        this.constantConverter(consumer, java.time.LocalDate.class, LocalDate.class, new ContextualConverter<java.time.LocalDate, LocalDate>() {
            @Override
            public LocalDate convert(java.time.LocalDate in, Context context) throws Exception {
                if (in == null) return null;
                return LocalDate.fromYearMonthDay(in.getYear(), in.getMonthValue(), in.getDayOfMonth());
            }
        });
        this.constantConverter(consumer, LocalTime.class, Long.class, new ContextualConverter<LocalTime, Long>() {
            @Override
            public Long convert(LocalTime in, Context context) throws Exception {
                if (in == null) return null;
                return in.toNanoOfDay();
            }
        });
        this.constantConverter(consumer, OffsetTime.class, Long.class, new ContextualConverter<OffsetTime, Long>() {
            @Override
            public Long convert(OffsetTime in, Context context) throws Exception {
                if (in == null) return null;
                return in.toLocalTime().toNanoOfDay();
            }
        });
//IFJAVA8_END

        this.constantConverter(consumer, Date.class, LocalDate.class, new ContextualConverter<Date, LocalDate>() {
            @Override
            public LocalDate convert(Date in, Context context) throws Exception {
                if (in == null) return null;
                Calendar cal = Calendar.getInstance();
                cal.setTime(in);
                return LocalDate.fromYearMonthDay(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
            }
        });
    }
}
