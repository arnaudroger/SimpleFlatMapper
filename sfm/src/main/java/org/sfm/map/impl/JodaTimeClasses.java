package org.sfm.map.impl;

import org.sfm.reflect.TypeHelper;

import java.lang.reflect.Type;

public class JodaTimeClasses {


    public static final String ORG_JODA_TIME_DATE_TIME = "org.joda.time.DateTime";

    public static final String ORG_JODA_TIME_LOCAL_DATE = "org.joda.time.LocalDate";

    public static final String ORG_JODA_TIME_LOCAL_DATE_TIME = "org.joda.time.LocalDateTime";

    public static final String ORG_JODA_TIME_LOCAL_TIME = "org.joda.time.LocalTime";

    public static final String ORG_JODA_INSTANT = "org.joda.time.Instant";

    public static boolean isJoda(Type target) {
        return getTypeName(target).startsWith("org.joda.time");
    }

    private static String getTypeName(Type target) {
        return TypeHelper.toClass(target).getName();
    }

    public static boolean isJodaDateTime(Type target) {
        return ORG_JODA_TIME_DATE_TIME.equals(getTypeName(target));
    }
    public static boolean isJodaInstant(Type target) {
        return ORG_JODA_INSTANT.equals(getTypeName(target));
    }
    public static boolean isJodaLocalDateTime(Type target) {
        return ORG_JODA_TIME_LOCAL_DATE_TIME.equals(getTypeName(target));
    }
    public static boolean isJodaLocalDate(Type target) {
        return ORG_JODA_TIME_LOCAL_DATE.equals(getTypeName(target));
    }
    public static boolean isJodaLocalTime(Type target) {
        return ORG_JODA_TIME_LOCAL_TIME.equals(getTypeName(target));
    }
 }
