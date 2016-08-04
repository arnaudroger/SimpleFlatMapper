package org.simpleflatmapper.jdbc.impl.setter;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.jdbc.impl.PreparedStatementSetterFactory;
import org.simpleflatmapper.jdbc.impl.convert.joda.JodaDateTimeToTimestampConverter;
import org.simpleflatmapper.jdbc.impl.convert.joda.JodaInstantToTimestampConverter;
import org.simpleflatmapper.jdbc.impl.convert.joda.JodaLocalDateTimeToTimestampConverter;
import org.simpleflatmapper.jdbc.impl.convert.joda.JodaLocalDateToDateConverter;
import org.simpleflatmapper.jdbc.impl.convert.joda.JodaLocalTimeToTimeConverter;
import org.simpleflatmapper.core.map.mapper.ColumnDefinition;
import org.simpleflatmapper.core.map.mapper.PropertyMapping;
import org.simpleflatmapper.util.date.joda.JodaTimeHelper;

import java.sql.Time;
import java.sql.Timestamp;

public class JodaTimePreparedStatementFactory implements PreparedStatementSetterFactory.Factory {
    @SuppressWarnings("unchecked")
    @Override
    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
        if (JodaTimeHelper.isJodaDateTime(pm.getPropertyMeta().getPropertyType())) {
            return (PreparedStatementIndexSetter<P>)
                    new ConvertDelegateIndexSetter<DateTime, Timestamp>(
                            new TimestampPreparedStatementIndexSetter(),
                            new JodaDateTimeToTimestampConverter());
        } else if (JodaTimeHelper.isJodaLocalDateTime(pm.getPropertyMeta().getPropertyType())) {
            return (PreparedStatementIndexSetter<P>)
                    new ConvertDelegateIndexSetter<LocalDateTime, Timestamp>(
                            new TimestampPreparedStatementIndexSetter(),
                            new JodaLocalDateTimeToTimestampConverter(JodaTimeHelper.getDateTimeZoneOrDefault(pm.getColumnDefinition())));
        } else if (JodaTimeHelper.isJodaLocalDate(pm.getPropertyMeta().getPropertyType())) {
            return (PreparedStatementIndexSetter<P>)
                    new ConvertDelegateIndexSetter<LocalDate, java.sql.Date>(
                            new DatePreparedStatementIndexSetter(),
                            new JodaLocalDateToDateConverter());
        } else if (JodaTimeHelper.isJodaLocalTime(pm.getPropertyMeta().getPropertyType())) {
            return (PreparedStatementIndexSetter<P>)
                    new ConvertDelegateIndexSetter<LocalTime, Time>(
                            new TimePreparedStatementIndexSetter(),
                            new JodaLocalTimeToTimeConverter(JodaTimeHelper.getDateTimeZoneOrDefault(pm.getColumnDefinition())));
        } else if (JodaTimeHelper.isJodaInstant(pm.getPropertyMeta().getPropertyType())) {
            return (PreparedStatementIndexSetter<P>)
                    new ConvertDelegateIndexSetter<Instant, Timestamp>(
                            new TimestampPreparedStatementIndexSetter(),
                            new JodaInstantToTimestampConverter());
        }
        return null;
    }
}
