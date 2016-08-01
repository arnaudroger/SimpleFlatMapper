package org.simpleflatmapper.csv.impl.cellreader.joda;

import org.joda.time.format.DateTimeFormatter;
import org.sfm.utils.UnaryFactory;
import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.CsvColumnDefinition;
import org.sfm.map.column.joda.JodaHelper;
import org.sfm.map.impl.JodaTimeClasses;
import org.sfm.reflect.TypeHelper;

import java.lang.reflect.Type;

public class JodaTimeCellValueReaderHelper {


    public static CellValueReader<?> getReader(Type type, CsvColumnDefinition columnDefinition) {
        Class<?> clazz = TypeHelper.toClass(type);


        if (JodaTimeClasses.isJodaDateTime(clazz)) {
            return newJodaTime(columnDefinition,
                            new UnaryFactory<DateTimeFormatter, CellValueReader<?>>() {
                                @Override
                                public CellValueReader<?> newInstance(DateTimeFormatter dateTimeFormatter) {
                                    return new JodaDateTimeCellValueReader(dateTimeFormatter);
                                }
                            });
        }
        if (JodaTimeClasses.isJodaLocalDate(clazz)) {
            return newJodaTime(columnDefinition,
                    new UnaryFactory<DateTimeFormatter, CellValueReader<?>>() {
                        @Override
                        public CellValueReader<?> newInstance(DateTimeFormatter dateTimeFormatter) {
                            return new JodaLocalDateCellValueReader(dateTimeFormatter);
                        }
                    });
        }
        if (JodaTimeClasses.isJodaLocalDateTime(clazz)) {
            return newJodaTime(columnDefinition,
                    new UnaryFactory<DateTimeFormatter, CellValueReader<?>>() {
                        @Override
                        public CellValueReader<?> newInstance(DateTimeFormatter dateTimeFormatter) {
                            return new JodaLocalDateTimeCellValueReader(dateTimeFormatter);
                        }
                    });
        }
        if (JodaTimeClasses.isJodaLocalTime(clazz)) {
            return newJodaTime(columnDefinition,
                    new UnaryFactory<DateTimeFormatter, CellValueReader<?>>() {
                        @Override
                        public CellValueReader<?> newInstance(DateTimeFormatter dateTimeFormatter) {
                            return new JodaLocalTimeCellValueReader(dateTimeFormatter);
                        }
                    });
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private static CellValueReader<?> newJodaTime(CsvColumnDefinition csvColumnDefinition, UnaryFactory<DateTimeFormatter, CellValueReader<?>> unaryFactory) {
        DateTimeFormatter[] dateTimeFormatters = JodaHelper.getDateTimeFormatters(csvColumnDefinition);
        if (dateTimeFormatters.length == 1) {
            return unaryFactory.newInstance(dateTimeFormatters[0]);
        } else {
            CellValueReader<?>[] readers = new CellValueReader[dateTimeFormatters.length];
            for(int i = 0; i < readers.length; i++) {
                readers[i] = unaryFactory.newInstance(dateTimeFormatters[i]);
            }
            return new MultiFormaterCellValueReader(readers);
        }
    }
}
