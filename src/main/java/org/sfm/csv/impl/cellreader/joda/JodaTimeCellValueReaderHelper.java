package org.sfm.csv.impl.cellreader.joda;

import org.joda.time.DateTime;
import org.sfm.csv.CellValueReader;
import org.sfm.csv.CsvColumnDefinition;
import org.sfm.reflect.TypeHelper;

import java.lang.reflect.Type;

public class JodaTimeCellValueReaderHelper {

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

    public static CellValueReader<?> getReader(Type type, CsvColumnDefinition columnDefinition) {
        Class<?> clazz = TypeHelper.toClass(type);
        if (dateTimeClass != null && clazz.isAssignableFrom(dateTimeClass)) {
            return new JodaDateTimeCellValueReader(columnDefinition.dateFormat());
        }
        if (localDateClass != null && clazz.isAssignableFrom(localDateClass)) {
            return new JodaLocalDateCellValueReader(columnDefinition.dateFormat());
        }
        if (localDateTimeClass != null && clazz.isAssignableFrom(localDateTimeClass)) {
            return new JodaLocalDateTimeCellValueReader(columnDefinition.dateFormat());
        }
        if (localTimeClass != null && clazz.isAssignableFrom(localTimeClass)) {
            return new JodaLocalTimeCellValueReader(columnDefinition.dateFormat());
        }
        return null;
    }
}
