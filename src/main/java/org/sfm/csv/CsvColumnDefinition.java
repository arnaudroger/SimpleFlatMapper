package org.sfm.csv;

import org.sfm.map.ColumnDefinition;
import org.sfm.reflect.TypeHelper;

import java.lang.reflect.Type;

public abstract class CsvColumnDefinition extends ColumnDefinition<CsvColumnKey> {

    public abstract String dateFormat();
    public abstract CellValueReader<?> getCustomReader();

    public static final CsvColumnDefinition IDENTITY = new IdentityCsvColumnDefinition();

    public static CsvColumnDefinition renameDefinition(final String name) {
        return new IdentityCsvColumnDefinition() {
            @Override
            public CsvColumnKey rename(CsvColumnKey key) {
                return key.alias(name);
            }
        };
    }

    public static CsvColumnDefinition dateFormatDefinition(final String dateFormatDef) {
        return new IdentityCsvColumnDefinition() {
            @Override
            public String dateFormat() {
                return dateFormatDef;
            }
        };
    }

    public static CsvColumnDefinition customReaderDefinition(final CellValueReader<?> cellValueReader) {
        return new IdentityCsvColumnDefinition() {
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
        };
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
    }

    static final class ComposeCsvColumnDefinition extends CsvColumnDefinition {
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
}
