package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.core.reflect.Setter;
import org.simpleflatmapper.core.reflect.primitive.ByteSetter;

import java.sql.PreparedStatement;

public class BytePreparedStatementSetter implements Setter<PreparedStatement, Byte>, ByteSetter<PreparedStatement> {

    private final int columnIndex;
    private final BytePreparedStatementIndexSetter setter = new BytePreparedStatementIndexSetter();

    public BytePreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void setByte(PreparedStatement target, byte value) throws Exception {
        setter.setByte(target, value, columnIndex);
    }

    @Override
    public void set(PreparedStatement target, Byte value) throws Exception {
        setter.set(target, value, columnIndex);
    }
}
