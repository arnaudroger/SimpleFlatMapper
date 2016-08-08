package org.simpleflatmapper.csv.impl.cellreader;

import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.util.ErrorHelper;

public class CharSequenceConverterCellValueReader<P> implements CellValueReader<P> {
    private final Converter<? super CharSequence, ? extends P> converter;

    public CharSequenceConverterCellValueReader(Converter<? super CharSequence, ? extends P> converter) {
        this.converter = converter;
    }

    @Override
    public P read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        try {
            return converter.convert(String.valueOf(chars, offset, length));
        } catch (Exception e) {
            return ErrorHelper.rethrow(e);
        }
    }
}
