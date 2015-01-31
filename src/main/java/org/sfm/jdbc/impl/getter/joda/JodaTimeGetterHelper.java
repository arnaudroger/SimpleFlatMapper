package org.sfm.jdbc.impl.getter.joda;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.CsvColumnDefinition;
import org.sfm.csv.impl.cellreader.joda.JodaDateTimeCellValueReader;
import org.sfm.csv.impl.cellreader.joda.JodaLocalDateCellValueReader;
import org.sfm.csv.impl.cellreader.joda.JodaLocalDateTimeCellValueReader;
import org.sfm.csv.impl.cellreader.joda.JodaLocalTimeCellValueReader;
import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.jdbc.impl.getter.ResultSetGetterFactory;
import org.sfm.map.impl.JodaTimeClasses;
import org.sfm.reflect.Getter;
import org.sfm.reflect.TypeHelper;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.Date;

public class JodaTimeGetterHelper {


    public static Getter<ResultSet, ?> getGetter(Type type, JdbcColumnKey key) {
        Class<?> clazz = TypeHelper.toClass(type);

        Getter<ResultSet, ? extends Date> getter = ResultSetGetterFactory.DATE_GETTER_FACTORY.newGetter(java.util.Date.class, key);

        if (JodaTimeClasses.isJodaDateTime(clazz)) {
            return new JodaDateTimeResultSetGetter(getter);
        }
        if (JodaTimeClasses.isJodaLocalDate(clazz)) {
            return new JodaLocalDateResultSetGetter(getter);
        }
        if (JodaTimeClasses.isJodaLocalDateTime(clazz)) {
            return new JodaLocalDateTimeResultSetGetter(getter);
        }
        if (JodaTimeClasses.isJodaLocalTime(clazz)) {
            return new JodaLocalTimeResultSetGetter(getter);
        }

        return null;
    }
}
