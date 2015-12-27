package org.sfm.jdbc.impl.setter;

import org.sfm.reflect.Setter;
import org.sfm.utils.Asserts;

import java.sql.PreparedStatement;

public class PreparedStatementSetterImpl<T> implements Setter<PreparedStatement, T> {

    private final int columnIndex;
    private final PreparedStatementIndexSetter<T> indexedSetter;

    public PreparedStatementSetterImpl(int columnIndex, PreparedStatementIndexSetter<T> indexedSetter) {
        this.columnIndex = columnIndex;
        this.indexedSetter = Asserts.requireNonNull("indexedSetter", indexedSetter);
    }

    @Override
    public void set(PreparedStatement target, T value) throws Exception {
        indexedSetter.set(target, value, columnIndex);
    }
}
