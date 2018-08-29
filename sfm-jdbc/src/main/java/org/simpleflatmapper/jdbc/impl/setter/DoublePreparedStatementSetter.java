package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.setter.ContextualSetter;
import org.simpleflatmapper.map.setter.DoubleContextualSetter;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.primitive.DoubleSetter;

import java.sql.PreparedStatement;

public class DoublePreparedStatementSetter implements ContextualSetter<PreparedStatement, Double>, DoubleContextualSetter<PreparedStatement> {

    private final int columnIndex;
    private final DoublePreparedStatementIndexSetter setter = new DoublePreparedStatementIndexSetter();

    public DoublePreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void setDouble(PreparedStatement target, double value, Context context) throws Exception {
        setter.setDouble(target, value, columnIndex, context);
    }

    @Override
    public void set(PreparedStatement target, Double value, Context context) throws Exception {
        setter.set(target, value, columnIndex, context);
    }
}
