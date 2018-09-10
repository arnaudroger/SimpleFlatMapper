package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.setter.BooleanContextualSetter;
import org.simpleflatmapper.map.setter.ContextualSetter;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.primitive.BooleanSetter;

import java.sql.PreparedStatement;

public class BooleanPreparedStatementSetter implements ContextualSetter<PreparedStatement, Boolean>, BooleanContextualSetter<PreparedStatement> {

    private final int columnIndex;
    private final BooleanPreparedStatementIndexSetter setter = new BooleanPreparedStatementIndexSetter();

    public BooleanPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void setBoolean(PreparedStatement target, boolean value, Context context) throws Exception {
        setter.setBoolean(target, value, columnIndex, context);
    }

    @Override
    public void set(PreparedStatement target, Boolean value, Context context) throws Exception {
        setter.set(target, value, columnIndex, context);
    }
}
