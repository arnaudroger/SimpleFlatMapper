package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.primitive.FloatSetter;

import java.sql.PreparedStatement;

public class FloatPreparedStatementSetter implements Setter<PreparedStatement, Float>, FloatSetter<PreparedStatement> {
    private final int columnIndex;
    private final FloatPreparedStatementIndexSetter setter = new FloatPreparedStatementIndexSetter();

    public FloatPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void setFloat(PreparedStatement target, float value) throws Exception {
        setter.setFloat(target, value, columnIndex);
    }

    @Override
    public void set(PreparedStatement target, Float value) throws Exception {
        setter.set(target, value, columnIndex);
    }
}
