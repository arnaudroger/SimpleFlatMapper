package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.LongSetter;

import java.sql.PreparedStatement;

public class LongPreparedStatementSetter implements Setter<PreparedStatement, Long>, LongSetter<PreparedStatement> {

    private final int columnIndex;
    private final LongPreparedStatementIndexSetter setter = new LongPreparedStatementIndexSetter();

    public LongPreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void setLong(PreparedStatement target, long value) throws Exception {
        setter.setLong(target, value, columnIndex);
    }

    @Override
    public void set(PreparedStatement target, Long value) throws Exception {
        setter.set(target, value, columnIndex);
    }
}
