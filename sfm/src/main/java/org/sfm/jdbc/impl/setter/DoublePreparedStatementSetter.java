package org.sfm.jdbc.impl.setter;

import org.sfm.jdbc.impl.setter.DoublePreparedStatementIndexedSetter;
import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.DoubleSetter;

import java.sql.PreparedStatement;

public class DoublePreparedStatementSetter implements Setter<PreparedStatement, Double>, DoubleSetter<PreparedStatement> {

    private final int columnIndex;
    private final DoublePreparedStatementIndexedSetter setter = new DoublePreparedStatementIndexedSetter();

    public DoublePreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void setDouble(PreparedStatement target, double value) throws Exception {
        setter.setDouble(target, value, columnIndex);
    }

    @Override
    public void set(PreparedStatement target, Double value) throws Exception {
        setter.set(target, value, columnIndex);
    }
}
