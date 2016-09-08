package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.util.Supplier;
import org.simpleflatmapper.util.SupplierHelper;
import org.simpleflatmapper.util.date.DateFormatSupplier;
import org.simpleflatmapper.util.date.DefaultDateFormatSupplier;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public final class JavaTimeHelper {

    private JavaTimeHelper () {}

    public static DateTimeFormatter getDateTimeFormatter(Object... properties) {

        ZoneId zoneId = getZoneId(properties);

        DefaultDateFormatSupplier defaultDateFormatSupplier = null;
        for(Object prop : properties) {
            DateTimeFormatter dateTimeFormatter = toDateTimeFormatter(prop, zoneId);
            if (dateTimeFormatter != null) {
                return dateTimeFormatter;
            } else if (prop instanceof DefaultDateFormatSupplier) {
                defaultDateFormatSupplier = (DefaultDateFormatSupplier) prop;
            }
        }

        if (defaultDateFormatSupplier != null) {
            return withZone(defaultDateFormatSupplier.get(), zoneId);
        }

        return null;
    }

    public static DateTimeFormatter[] getDateTimeFormatters(Object... properties) {
        List<DateTimeFormatter> dtf = new ArrayList<DateTimeFormatter>();

        ZoneId zoneId = getZoneId(properties);

        DefaultDateFormatSupplier defaultDateFormatSupplier = null;
        for(Object prop : properties) {
            DateTimeFormatter dateTimeFormatter = toDateTimeFormatter(prop, zoneId);
            if (dateTimeFormatter != null) {
                dtf.add(dateTimeFormatter);
            }else if (prop instanceof DefaultDateFormatSupplier) {
                defaultDateFormatSupplier = (DefaultDateFormatSupplier) prop;
            }
        }

        if (dtf.isEmpty()) {
            if (defaultDateFormatSupplier == null) {
                throw new IllegalStateException("No date format specified");
            }
            dtf.add(withZone(defaultDateFormatSupplier.get(), zoneId));
        }

        return dtf.toArray(new DateTimeFormatter[0]);
    }

    @SuppressWarnings("unchecked")
    private static DateTimeFormatter toDateTimeFormatter(Object prop, ZoneId zoneId) {
        if (SupplierHelper.isSupplierOf(prop, DateTimeFormatter.class)) {
            return withZone(((Supplier<DateTimeFormatter>) prop).get(), zoneId);
        } else if (prop instanceof DateFormatSupplier) {
            return  withZone(((DateFormatSupplier) prop).get(), zoneId);
        } else if (prop instanceof DateTimeFormatter) {
            return (DateTimeFormatter) prop;
        }
        return null;
    }

    private static DateTimeFormatter withZone(String format, ZoneId zoneId) {
        return withZone(DateTimeFormatter.ofPattern(format), zoneId);
    }

    private static DateTimeFormatter withZone(DateTimeFormatter dateTimeFormatter, ZoneId zoneId) {
        if (zoneId != null) {
            return dateTimeFormatter.withZone(zoneId);
        } else if (dateTimeFormatter.getZone() == null) {
            return dateTimeFormatter.withZone(ZoneId.systemDefault());
        }
        return dateTimeFormatter;
    }

    public static ZoneId getZoneIdOrDefault(Object... params) {
        ZoneId p = getZoneId(params);
        if (p != null) return p;
        return ZoneId.systemDefault();
    }

    @SuppressWarnings("unchecked")
    public static ZoneId getZoneId(Object[] params) {
        if (params != null) {
            for(Object p : params) {
                if (p instanceof ZoneId) {
                    return (ZoneId) p;
                } else if (p instanceof TimeZone) {
                    return ((TimeZone)p).toZoneId();
                } else if (SupplierHelper.isSupplierOf(p, ZoneId.class)) {
                    return ((Supplier<ZoneId>)p).get();
                } else if (SupplierHelper.isSupplierOf(p, TimeZone.class)) {
                    return (((Supplier<TimeZone>)p).get()).toZoneId();
                }
            }
        }
        return null;
    }

}
