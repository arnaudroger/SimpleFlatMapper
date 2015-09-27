package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;
import org.sfm.utils.conv.Converter;

import java.sql.PreparedStatement;

public class ConvertValuePreparedStatementSetter<I, O> implements Setter<PreparedStatement, I> {
    private final Setter<PreparedStatement, O> setter;
    private final Converter<I, O> converter;

    public ConvertValuePreparedStatementSetter(Setter<PreparedStatement, O> setter, Converter<I, O> converter) {
        this.setter = setter;
        this.converter = converter;
    }

    @Override
    public void set(PreparedStatement target, I value) throws Exception {
        setter.set(target, converter.convert(value));
    }
}
