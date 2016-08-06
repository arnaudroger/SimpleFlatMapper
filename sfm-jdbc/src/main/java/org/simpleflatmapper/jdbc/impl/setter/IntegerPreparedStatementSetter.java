package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.primitive.IntSetter;

import java.sql.PreparedStatement;

public class IntegerPreparedStatementSetter implements Setter<PreparedStatement, Integer>, IntSetter<PreparedStatement> {

    private final int columnIndex;
    private final IntegerPreparedStatementIndexSetter setter = new IntegerPreparedStatementIndexSetter();

    public IntegerPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void setInt(PreparedStatement target, int value) throws Exception {
        setter.setInt(target, value, columnIndex);
    }

    @Override
    public void set(PreparedStatement target, Integer value) throws Exception {
        setter.set(target, value, columnIndex);
    }
}
