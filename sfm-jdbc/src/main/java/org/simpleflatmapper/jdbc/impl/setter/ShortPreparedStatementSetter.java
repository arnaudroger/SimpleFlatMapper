package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.setter.ContextualSetter;
import org.simpleflatmapper.map.setter.ShortContextualSetter;

import java.sql.PreparedStatement;

public class ShortPreparedStatementSetter implements ContextualSetter<PreparedStatement, Short>, ShortContextualSetter<PreparedStatement> {

    private final int columnIndex;
    private final ShortPreparedStatementIndexSetter setter = new ShortPreparedStatementIndexSetter();

    public ShortPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void set(PreparedStatement target, Short value, Context context) throws Exception {
        setter.set(target, value, columnIndex, context);
    }

    @Override
    public void setShort(PreparedStatement target, short value, Context context) throws Exception {
        setter.setShort(target, value, columnIndex, context);
    }
}
