package org.simpleflatmapper.converter.joda.impl;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.simpleflatmapper.util.Supplier;
import org.simpleflatmapper.util.SupplierHelper;
import org.simpleflatmapper.util.date.DateFormatSupplier;
import org.simpleflatmapper.util.date.DefaultDateFormatSupplier;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public final class JodaTimeHelper {

    private JodaTimeHelper() {}

    public static DateTimeFormatter getDateTimeFormatter(Object... properties) {

        final DateTimeZone dateTimeZone = getDateTimeZone(properties);

        DefaultDateFormatSupplier defaultDateFormatSupplier = null;
        for(Object prop : properties) {
            DateTimeFormatter dateTimeFormatter = toDateTimeFormater(prop, dateTimeZone);
            if (dateTimeFormatter != null) {
                return dateTimeFormatter;
            } else if (prop instanceof DefaultDateFormatSupplier) {
                defaultDateFormatSupplier = (DefaultDateFormatSupplier) prop;
            }
        }

        if (defaultDateFormatSupplier != null) {
            return withZone(defaultDateFormatSupplier.get(), dateTimeZone);
        }

        return null;
    }
    @SuppressWarnings("unchecked")
    private static DateTimeFormatter toDateTimeFormater(Object prop, DateTimeZone dateTimeZone) {
        if (SupplierHelper.isSupplierOf(prop, DateTimeFormatter.class)) {
            return (withZone(((Supplier<DateTimeFormatter>) prop).get(), dateTimeZone));
        } else if (prop instanceof DateFormatSupplier) {
            return (withZone(((DateFormatSupplier)prop).get(), dateTimeZone));
        } else if (prop instanceof DateTimeFormatter) {
            return (DateTimeFormatter) prop;
        }
        return null;
    }

    public static DateTimeFormatter[] getDateTimeFormatters(Object... properties) {
        List<DateTimeFormatter> dtf = new ArrayList<DateTimeFormatter>();

        final DateTimeZone dateTimeZone = getDateTimeZone(properties);

        DefaultDateFormatSupplier defaultDateFormatSupplier = null;
        for(Object prop : properties) {
            DateTimeFormatter df = toDateTimeFormater(prop, dateTimeZone);
            if (df != null) {
                dtf.add(df);
            } else if (prop instanceof DefaultDateFormatSupplier) {
                defaultDateFormatSupplier = (DefaultDateFormatSupplier) prop;
            }
        }

        if (dtf.isEmpty()) {
            if (defaultDateFormatSupplier == null) {
                throw new IllegalStateException("No date format specified");
            }
            dtf.add(withZone(defaultDateFormatSupplier.get(), dateTimeZone));
        }

        return dtf.toArray(new DateTimeFormatter[0]);
    }
    private static DateTimeFormatter withZone(String format, DateTimeZone zoneId) {
        return withZone(DateTimeFormat.forPattern(format), zoneId);
    }
    private static DateTimeFormatter withZone(DateTimeFormatter dateTimeFormatter, DateTimeZone zoneId) {
        if (zoneId != null) {
            return dateTimeFormatter.withZone(zoneId);
        } else if (dateTimeFormatter.getZone() == null) {
            return dateTimeFormatter.withZone(DateTimeZone.getDefault());
        }
        return dateTimeFormatter;
    }


    public static DateTimeZone getDateTimeZoneOrDefault(Object... params) {
        DateTimeZone p = getDateTimeZone(params);
        if (p != null) return p;
        return DateTimeZone.getDefault();
    }

    @SuppressWarnings("unchecked")
    private static DateTimeZone getDateTimeZone(Object[] params) {
        if (params != null) {
            for(Object p : params) {
                if (p instanceof DateTimeZone) {
                    return (DateTimeZone) p;
                } else if (p instanceof TimeZone) {
                    return DateTimeZone.forTimeZone((TimeZone)p);
                } else if (SupplierHelper.isSupplierOf(p, DateTimeZone.class)) {
                    return ((Supplier<DateTimeZone>)p).get();
                } else if (SupplierHelper.isSupplierOf(p, TimeZone.class)) {
                    return DateTimeZone.forTimeZone(((Supplier<TimeZone>)p).get());
                }
            }
        }
        return null;
    }
}
