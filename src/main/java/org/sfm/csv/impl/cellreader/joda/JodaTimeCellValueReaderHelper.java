package org.sfm.csv.impl.cellreader.joda;

import org.joda.time.DateTime;
import org.sfm.csv.CellValueReader;
import org.sfm.csv.CsvColumnDefinition;

import java.util.Map;

public class JodaTimeCellValueReaderHelper {

    private static boolean isJodaTimePresent() {
        try {
            Class.forName("org.joda.time.DateTime");
            return true;
        } catch(Throwable e) {}
        return false;
    }

    private static final Class<?> dateTimeClass;

    static {
        Class<?> clazz = null;
        try {
             clazz = Class.forName("org.joda.time.DateTime");
        } catch (ClassNotFoundException e) {
        }
        dateTimeClass = clazz;
    }

    public static boolean isJodaTime(Class<?> propertyClass) {
        return (dateTimeClass != null && propertyClass.isAssignableFrom(dateTimeClass));
    }

    public static CellValueReader<DateTime> getReader(CsvColumnDefinition columnDefinition) {
        return new JodaDateTimeCellValueReader(columnDefinition.dateFormat());
    }
}
