package org.sfm.jdbc.impl.setter;

import org.sfm.utils.ErrorHelper;
import org.sfm.utils.conv.Converter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ConvertDelegateIndexedSetter<I, O> implements PrepareStatementIndexedSetter<I> {
    private final PrepareStatementIndexedSetter<O> setter;
    private final Converter<I, O> converter;

    public ConvertDelegateIndexedSetter(PrepareStatementIndexedSetter<O> setter, Converter<I, O> converter) {
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
