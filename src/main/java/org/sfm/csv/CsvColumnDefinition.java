package org.sfm.csv;

import org.sfm.map.ColumnDefinition;
import org.sfm.reflect.TypeHelper;

import java.lang.reflect.Type;

public abstract class CsvColumnDefinition extends ColumnDefinition<CsvColumnKey> {

    public abstract String dateFormat();
    public abstract CellValueReader<?> getCustomReader();

    public abstract CsvColumnDefinition addRename(String name);
    public abstract CsvColumnDefinition addDateFormat(String dateFormatDef);
    public abstract CsvColumnDefinition addCustomReader(CellValueReader<?> cellValueReader);
    public abstract CsvColumnDefinition compose(CsvColumnDefinition columnDefinition);

    public static final CsvColumnDefinition IDENTITY = new IdentityCsvColumnDefinition();

    public static CsvColumnDefinition renameDefinition(final String name) {
        return new RenameCsvColumnDefinition(name);
    }

    public static CsvColumnDefinition dateFormatDefinition(final String dateFormatDef) {
        return new DateFormatCsvColumnDefinition(dateFormatDef);
    }

    public static CsvColumnDefinition customReaderDefinition(final CellValueReader<?> cellValueReader) {
        return new CustomReaderCsvColumnDefinition(cellValueReader);
    }


    public static CsvColumnDefinition compose(final CsvColumnDefinition def1, final CsvColumnDefinition def2) {
        if (def1 == IDENTITY) return def2;
        if (def2 == IDENTITY) return def1;
        return new ComposeCsvColumnDefinition(def1, def2);
    }




    static class IdentityCsvColumnDefinition extends CsvColumnDefinition {
        @Override
        public String dateFormat() {
            return null;
        }

        @Override
        public CellValueReader<?> getCustomReader() {
            return null;
        }

        @Override
        public CsvColumnKey rename(CsvColumnKey key) {
            return key;
        }

        @Override
        public boolean hasCustomSource() {
            return false;
        }

        @Override
        public Type getCustomSourceReturnType() {
            throw new IllegalStateException();
        }


        public CsvColumnDefinition addRename(final String name) {
            return compose(CsvColumnDefinition.renameDefinition(name));
        }

        public CsvColumnDefinition addDateFormat(final String dateFormatDef) {
            return compose(CsvColumnDefinition.dateFormatDefinition(dateFormatDef));
        }

        public CsvColumnDefinition addCustomReader(final CellValueReader<?> cellValueReader) {
            return compose(CsvColumnDefinition.customReaderDefinition(cellValueReader));
        }

        public CsvColumnDefinition compose(CsvColumnDefinition columnDefinition) {
            return compose(this, columnDefinition);
        }


    }

    static final class ComposeCsvColumnDefinition extends IdentityCsvColumnDefinition {
        private final CsvColumnDefinition def1;
        private final CsvColumnDefinition def2;

        public ComposeCsvColumnDefinition(CsvColumnDefinition def1, CsvColumnDefinition def2) {
            this.def1 = def1;
            this.def2 = def2;
        }

        @Override
        public String dateFormat() {
            String df = def2.dateFormat();
            if (df == null) {
                df = def1.dateFormat();
            }
            return df;
        }

        @Override
        public CellValueReader<?> getCustomReader() {
            CellValueReader<?> reader = def2.getCustomReader();

            if (reader == null) {
                reader = def1.getCustomReader();
            }

            return reader;
        }

        @Override
        public CsvColumnKey rename(CsvColumnKey key) {
            return def2.rename(def1.rename(key));
        }

        @Override
        public boolean hasCustomSource() {
            return def1.hasCustomSource() || def2.hasCustomSource();
        }

        @Override
        public Type getCustomSourceReturnType() {
            if (def1.hasCustomSource()) {
                return def1.getCustomSourceReturnType();
            } else if (def2.hasCustomSource()){
                return def2.getCustomSourceReturnType();
            } else {
                throw new IllegalStateException();
            }
        }
    }

    private static class CustomReaderCsvColumnDefinition extends IdentityCsvColumnDefinition {
        private final CellValueReader<?> cellValueReader;

        public CustomReaderCsvColumnDefinition(CellValueReader<?> cellValueReader) {
            this.cellValueReader = cellValueReader;
        }

        @Override
        public CellValueReader<?> getCustomReader() {
            return cellValueReader;
        }

        @Override
        public boolean hasCustomSource() {
            return true;
        }

        @Override
        public Type getCustomSourceReturnType() {
            return TypeHelper.getParamTypesForInterface(cellValueReader.getClass(), CellValueReader.class)[0];
        }
    }

    private static class DateFormatCsvColumnDefinition extends IdentityCsvColumnDefinition {
        private final String dateFormatDef;

        public DateFormatCsvColumnDefinition(String dateFormatDef) {
            this.dateFormatDef = dateFormatDef;
        }

        @Override
        public String dateFormat() {
            return dateFormatDef;
        }
    }

    private static class RenameCsvColumnDefinition extends IdentityCsvColumnDefinition {
        private final String name;

        public RenameCsvColumnDefinition(String name) {
            this.name = name;
        }

        @Override
        public CsvColumnKey rename(CsvColumnKey key) {
            return key.alias(name);
        }
    }
}
