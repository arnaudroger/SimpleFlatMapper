package org.sfm.map.impl;

public class JodaTimeClasses {

    private static final Class<?> dateTimeClass;

    private static final Class<?> localDateClass;

    private static final Class<?> localDateTimeClass;

    private static final Class<?> localTimeClass;

    static {
        Class<?> clazz = null;
        try {
             clazz = Class.forName("org.joda.time.DateTime");
        } catch (ClassNotFoundException e) {
        }
        dateTimeClass = clazz;

        clazz = null;
        try {
            clazz = Class.forName("org.joda.time.LocalDate");
        } catch (ClassNotFoundException e) {
        }
        localDateClass = clazz;

        clazz = null;
        try {
            clazz = Class.forName("org.joda.time.LocalDateTime");
        } catch (ClassNotFoundException e) {
        }
        localDateTimeClass = clazz;

        clazz = null;
        try {
            clazz = Class.forName("org.joda.time.LocalTime");
        } catch (ClassNotFoundException e) {
        }
        localTimeClass = clazz;

    }

    public static boolean isJodaDateTime(Class<?> target) {
        return dateTimeClass != null && dateTimeClass.equals(target);
    }
    public static boolean isJodaLocalDateTime(Class<?> target) {
        return localDateTimeClass != null && localDateTimeClass.equals(target);
    }
    public static boolean isJodaLocalDate(Class<?> target) {
        return localDateClass != null && localDateClass.equals(target);
    }
    public static boolean isJodaLocalTime(Class<?> target) {
        return localTimeClass != null && localTimeClass.equals(target);
    }
 }
