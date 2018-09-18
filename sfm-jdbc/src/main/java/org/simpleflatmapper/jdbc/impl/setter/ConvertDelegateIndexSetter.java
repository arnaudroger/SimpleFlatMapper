package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.setter.ContextualIndexedSetter;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.converter.ContextualConverter;

import java.sql.PreparedStatement;
import java.sql.SQLException;


public class ConvertDelegateIndexSetter<I, O> implements PreparedStatementIndexSetter<I> {
    private final ContextualIndexedSetter<PreparedStatement, O> setter;
    private final ContextualConverter<? super I, ? extends O> converter;

    public ConvertDelegateIndexSetter(ContextualIndexedSetter<PreparedStatement, O> setter, ContextualConverter<? super I, ? extends O> converter) {
        this.setter = setter;
        this.converter = converter;
    }

    @Override
    public void set(PreparedStatement target, I value, int columnIndex, Context context) throws SQLException {
        try {
            setter.set(target, converter.convert(value, context), columnIndex, context);
        } catch (Exception e) {
            ErrorHelper.rethrow(e);
        }
    }
}
