package org.sfm.csv.impl.cellreader.joda;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.CsvColumnDefinition;
import org.sfm.map.impl.JodaTimeClasses;
import org.sfm.reflect.TypeHelper;

import java.lang.reflect.Type;

public class JodaTimeCellValueReaderHelper {


    public static CellValueReader<?> getReader(Type type, CsvColumnDefinition columnDefinition) {
        Class<?> clazz = TypeHelper.toClass(type);

        if (JodaTimeClasses.isJodaDateTime(clazz)) {
            return new JodaDateTimeCellValueReader(columnDefinition.dateFormat());
        }
        if (JodaTimeClasses.isJodaLocalDate(clazz)) {
            return new JodaLocalDateCellValueReader(columnDefinition.dateFormat());
        }
        if (JodaTimeClasses.isJodaLocalDateTime(clazz)) {
            return new JodaLocalDateTimeCellValueReader(columnDefinition.dateFormat());
        }
        if (JodaTimeClasses.isJodaLocalTime(clazz)) {
            return new JodaLocalTimeCellValueReader(columnDefinition.dateFormat());
        }

        return null;
    }
}
