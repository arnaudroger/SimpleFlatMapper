package org.sfm.csv;

import org.sfm.map.ColumnDefinition;

public abstract class CsvColumnDefinition extends ColumnDefinition<CsvColumnKey> {

    public abstract String dateFormat(String dateFormat);
    public abstract boolean hasCustomReader();
    public abstract CellValueReader<?> getCustomReader();

    public static CsvColumnDefinition IDENTITY = new IndentityCsvColumnDefinition();

    public static CsvColumnDefinition newRename(final String name) {
        return new IndentityCsvColumnDefinition() {
            @Override
            public CsvColumnKey rename(CsvColumnKey key) {
                return key.alias(name);
            }
        };
    }

    public static CsvColumnDefinition dateFormatDef(final String dateFormatDef) {
        return new IndentityCsvColumnDefinition() {
            @Override
            public String dateFormat(String dateFormat) {
                return dateFormatDef;
            }
        };
    }

    public static CsvColumnDefinition compose(final CsvColumnDefinition def1, final CsvColumnDefinition def2) {
        if (def1 == IDENTITY) return def2;
        if (def2 == IDENTITY) return def1;

        return new ComposeCsvColumnDefinition(def1, def2);
    }

    static CsvColumnDefinition newCustomReader(final CellValueReader<?> cellValueReader) {
        return new IndentityCsvColumnDefinition() {
            @Override
            public CellValueReader<?> getCustomReader() {
                return cellValueReader;
            }

            @Override
            public boolean hasCustomReader() {
                return true;
            }
        };
    }



    static class IndentityCsvColumnDefinition extends CsvColumnDefinition {
        @Override
        public String dateFormat(String dateFormat) {
            return dateFormat;
        }

        @Override
        public boolean hasCustomReader() {
            return false;
        }

        @Override
        public CellValueReader<?> getCustomReader() {
            return null;
        }

        @Override
        public CsvColumnKey rename(CsvColumnKey key) {
            return key;
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
        public String dateFormat(String dateFormat) {
            return def2.dateFormat(def1.dateFormat(dateFormat));
        }

        @Override
        public boolean hasCustomReader() {
            return def2.hasCustomReader() || def1.hasCustomReader();
        }

        @Override
        public CellValueReader<?> getCustomReader() {
            CellValueReader<?> reader = def1.getCustomReader();

            if (reader == null) {
                reader = def2.getCustomReader();
            }

            return reader;
        }

        @Override
        public CsvColumnKey rename(CsvColumnKey key) {
            return def2.rename(def1.rename(key));
        }
    }
}
