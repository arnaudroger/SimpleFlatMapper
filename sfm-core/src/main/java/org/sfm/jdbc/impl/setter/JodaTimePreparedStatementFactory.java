package org.sfm.jdbc.impl.setter;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.jdbc.impl.PreparedStatementSetterFactory;
import org.sfm.jdbc.impl.convert.joda.JodaDateTimeToTimestampConverter;
import org.sfm.jdbc.impl.convert.joda.JodaInstantToTimestampConverter;
import org.sfm.jdbc.impl.convert.joda.JodaLocalDateTimeToTimestampConverter;
import org.sfm.jdbc.impl.convert.joda.JodaLocalDateToDateConverter;
import org.sfm.jdbc.impl.convert.joda.JodaLocalTimeToTimeConverter;
import org.sfm.map.column.joda.JodaHelper;
import org.sfm.map.impl.JodaTimeClasses;
import org.sfm.map.mapper.ColumnDefinition;
import org.sfm.map.mapper.PropertyMapping;

import java.sql.Time;
import java.sql.Timestamp;

public class JodaTimePreparedStatementFactory implements PreparedStatementSetterFactory.Factory {
    @SuppressWarnings("unchecked")
    @Override
    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
        if (JodaTimeClasses.isJodaDateTime(pm.getPropertyMeta().getPropertyType())) {
            return (PreparedStatementIndexSetter<P>)
                    new ConvertDelegateIndexSetter<DateTime, Timestamp>(
                            new TimestampPreparedStatementIndexSetter(),
                            new JodaDateTimeToTimestampConverter());
        } else if (JodaTimeClasses.isJodaLocalDateTime(pm.getPropertyMeta().getPropertyType())) {
            return (PreparedStatementIndexSetter<P>)
                    new ConvertDelegateIndexSetter<LocalDateTime, Timestamp>(
                            new TimestampPreparedStatementIndexSetter(),
                            new JodaLocalDateTimeToTimestampConverter(JodaHelper.getDateTimeZoneOrDefault(pm.getColumnDefinition())));
        } else if (JodaTimeClasses.isJodaLocalDate(pm.getPropertyMeta().getPropertyType())) {
            return (PreparedStatementIndexSetter<P>)
                    new ConvertDelegateIndexSetter<LocalDate, java.sql.Date>(
                            new DatePreparedStatementIndexSetter(),
                            new JodaLocalDateToDateConverter());
        } else if (JodaTimeClasses.isJodaLocalTime(pm.getPropertyMeta().getPropertyType())) {
            return (PreparedStatementIndexSetter<P>)
                    new ConvertDelegateIndexSetter<LocalTime, Time>(
                            new TimePreparedStatementIndexSetter(),
                            new JodaLocalTimeToTimeConverter(JodaHelper.getDateTimeZoneOrDefault(pm.getColumnDefinition())));
        } else if (JodaTimeClasses.isJodaInstant(pm.getPropertyMeta().getPropertyType())) {
            return (PreparedStatementIndexSetter<P>)
                    new ConvertDelegateIndexSetter<Instant, Timestamp>(
                            new TimestampPreparedStatementIndexSetter(),
                            new JodaInstantToTimestampConverter());
        }
        return null;
    }
}
