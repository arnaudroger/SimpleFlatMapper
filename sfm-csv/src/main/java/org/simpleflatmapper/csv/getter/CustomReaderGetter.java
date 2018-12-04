package org.simpleflatmapper.csv.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.CsvRow;
import org.simpleflatmapper.map.getter.ContextualGetter;

public  class CustomReaderGetter<P> implements ContextualGetter<CsvRow, P> {
        private final CellValueReader<?> reader;
        private final int index;

        public CustomReaderGetter(int index, CellValueReader<?> reader) {
            this.index = index;
            this.reader = reader;
        }

        @Override
        public P get(CsvRow target, Context context) throws Exception {
            return (P) target.read(reader, index);
        }
    }