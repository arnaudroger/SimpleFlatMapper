package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.primitive.DoubleSetter;

import java.sql.PreparedStatement;

public class DoublePreparedStatementSetter implements Setter<PreparedStatement, Double>, DoubleSetter<PreparedStatement> {

    private final int columnIndex;
    private final DoublePreparedStatementIndexSetter setter = new DoublePreparedStatementIndexSetter();

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
