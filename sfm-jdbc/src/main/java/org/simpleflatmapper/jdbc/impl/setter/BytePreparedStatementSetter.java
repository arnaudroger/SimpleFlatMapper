package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.setter.ByteContextualSetter;
import org.simpleflatmapper.map.setter.ContextualSetter;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.primitive.ByteSetter;

import java.sql.PreparedStatement;

public class BytePreparedStatementSetter implements ContextualSetter<PreparedStatement, Byte>, ByteContextualSetter<PreparedStatement> {

    private final int columnIndex;
    private final BytePreparedStatementIndexSetter setter = new BytePreparedStatementIndexSetter();

    public BytePreparedStatementSetter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void setByte(PreparedStatement target, byte value, Context context) throws Exception {
        setter.setByte(target, value, columnIndex, context);
    }

    @Override
    public void set(PreparedStatement target, Byte value, Context context) throws Exception {
        setter.set(target, value, columnIndex, context);
    }
}
