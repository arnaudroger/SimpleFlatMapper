package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.setter.ContextualSetter;
import org.simpleflatmapper.map.setter.IntContextualSetter;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.primitive.IntSetter;

import java.sql.PreparedStatement;

public class IntegerPreparedStatementSetter implements ContextualSetter<PreparedStatement, Integer>, IntContextualSetter<PreparedStatement> {

    private final int columnIndex;
    private final IntegerPreparedStatementIndexSetter setter = new IntegerPreparedStatementIndexSetter();

    public IntegerPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void setInt(PreparedStatement target, int value, Context context) throws Exception {
        setter.setInt(target, value, columnIndex, context);
    }

    @Override
    public void set(PreparedStatement target, Integer value, Context context) throws Exception {
        setter.set(target, value, columnIndex, context);
    }
}
