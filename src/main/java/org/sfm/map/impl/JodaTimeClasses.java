package org.sfm.map.impl;

import java.lang.reflect.Type;

public class JodaTimeClasses {


    public static final String ORG_JODA_TIME_DATE_TIME = "org.joda.time.DateTime";

    public static final String ORG_JODA_TIME_LOCAL_DATE = "org.joda.time.LocalDate";

    public static final String ORG_JODA_TIME_LOCAL_DATE_TIME = "org.joda.time.LocalDateTime";

    public static final String ORG_JODA_TIME_LOCAL_TIME = "org.joda.time.LocalTime";

    public static boolean isJoda(Type target) {
        return target.getTypeName().startsWith("org.joda.time");
    }

    public static boolean isJodaDateTime(Type target) {
        return ORG_JODA_TIME_DATE_TIME.equals(target.getTypeName());
    }
    public static boolean isJodaLocalDateTime(Type target) {
        return ORG_JODA_TIME_LOCAL_DATE_TIME.equals(target.getTypeName());
    }
    public static boolean isJodaLocalDate(Type target) {
        return ORG_JODA_TIME_LOCAL_DATE.equals(target.getTypeName());
    }
    public static boolean isJodaLocalTime(Type target) {
        return ORG_JODA_TIME_LOCAL_TIME.equals(target.getTypeName());
    }
 }
