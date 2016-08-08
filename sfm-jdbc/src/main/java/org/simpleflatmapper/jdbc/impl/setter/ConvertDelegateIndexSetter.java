package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.reflect.IndexedSetter;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.converter.Converter;

import java.sql.PreparedStatement;
import java.sql.SQLException;


public class ConvertDelegateIndexSetter<I, O> implements PreparedStatementIndexSetter<I> {
    private final IndexedSetter<PreparedStatement, O>  setter;
    private final Converter<? super I, ? extends O> converter;

    public ConvertDelegateIndexSetter(IndexedSetter<PreparedStatement, O> setter, Converter<? super I, ? extends O> converter) {
        this.setter = setter;
        this.converter = converter;
    }

    @Override
    public void set(PreparedStatement target, I value, int columnIndex) throws SQLException {
        try {
            setter.set(target, converter.convert(value), columnIndex);
        } catch (Exception e) {
            ErrorHelper.rethrow(e);
        }
    }
}
