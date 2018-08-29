package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.setter.ContextualSetter;
import org.simpleflatmapper.map.setter.FloatContextualSetter;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.primitive.FloatSetter;

import java.sql.PreparedStatement;

public class FloatPreparedStatementSetter implements ContextualSetter<PreparedStatement, Float>, FloatContextualSetter<PreparedStatement> {
    private final int columnIndex;
    private final FloatPreparedStatementIndexSetter setter = new FloatPreparedStatementIndexSetter();

    public FloatPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void setFloat(PreparedStatement target, float value, Context context) throws Exception {
        setter.setFloat(target, value, columnIndex, context);
    }

    @Override
    public void set(PreparedStatement target, Float value, Context context) throws Exception {
        setter.set(target, value, columnIndex, context);
    }
}
