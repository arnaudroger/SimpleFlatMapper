package org.sfm.jdbc.impl.setter;

import org.sfm.jdbc.impl.setter.IntegerPreparedStatementIndexedSetter;
import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.IntSetter;

import java.sql.PreparedStatement;

public class IntegerPreparedStatementSetter implements Setter<PreparedStatement, Integer>, IntSetter<PreparedStatement> {

    private final int columnIndex;
    private final IntegerPreparedStatementIndexedSetter setter = new IntegerPreparedStatementIndexedSetter();

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
