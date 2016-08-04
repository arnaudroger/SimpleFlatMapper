package org.simpleflatmapper.csv.impl.cellreader.joda;

import org.joda.time.format.DateTimeFormatter;
import org.simpleflatmapper.util.TypeHelper;
import org.simpleflatmapper.util.UnaryFactory;
import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.CsvColumnDefinition;
import org.simpleflatmapper.util.date.joda.JodaTimeHelper;

import java.lang.reflect.Type;

public class JodaTimeCellValueReaderHelper {


    public static CellValueReader<?> getReader(Type type, CsvColumnDefinition columnDefinition) {
        Class<?> clazz = TypeHelper.toClass(type);


        if (JodaTimeHelper.isJodaDateTime(clazz)) {
            return newJodaTime(columnDefinition,
                            new UnaryFactory<DateTimeFormatter, CellValueReader<?>>() {
                                @Override
                                public CellValueReader<?> newInstance(DateTimeFormatter dateTimeFormatter) {
                                    return new JodaDateTimeCellValueReader(dateTimeFormatter);
                                }
                            });
        }
        if (JodaTimeHelper.isJodaLocalDate(clazz)) {
            return newJodaTime(columnDefinition,
                    new UnaryFactory<DateTimeFormatter, CellValueReader<?>>() {
                        @Override
                        public CellValueReader<?> newInstance(DateTimeFormatter dateTimeFormatter) {
                            return new JodaLocalDateCellValueReader(dateTimeFormatter);
                        }
                    });
        }
        if (JodaTimeHelper.isJodaLocalDateTime(clazz)) {
            return newJodaTime(columnDefinition,
                    new UnaryFactory<DateTimeFormatter, CellValueReader<?>>() {
                        @Override
                        public CellValueReader<?> newInstance(DateTimeFormatter dateTimeFormatter) {
                            return new JodaLocalDateTimeCellValueReader(dateTimeFormatter);
                        }
                    });
        }
        if (JodaTimeHelper.isJodaLocalTime(clazz)) {
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
        DateTimeFormatter[] dateTimeFormatters = JodaTimeHelper.getDateTimeFormatters(csvColumnDefinition.properties());
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
