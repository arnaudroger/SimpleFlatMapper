package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.setter.ContextualSetter;
import org.simpleflatmapper.map.setter.LongContextualSetter;

import java.sql.PreparedStatement;

public class LongPreparedStatementSetter implements ContextualSetter<PreparedStatement, Long>, LongContextualSetter<PreparedStatement> {

    private final int columnIndex;
    private final LongPreparedStatementIndexSetter setter = new LongPreparedStatementIndexSetter();

    public LongPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void setLong(PreparedStatement target, long value, Context context) throws Exception {
        setter.setLong(target, value, columnIndex, context);
    }

    @Override
    public void set(PreparedStatement target, Long value, Context context) throws Exception {
        setter.set(target, value, columnIndex, context);
    }
}
