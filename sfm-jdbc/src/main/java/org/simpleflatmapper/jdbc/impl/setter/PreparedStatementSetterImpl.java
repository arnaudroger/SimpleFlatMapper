package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.setter.ContextualIndexedSetter;
import org.simpleflatmapper.map.setter.ContextualSetter;
import org.simpleflatmapper.reflect.IndexedSetter;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.util.Asserts;

import java.sql.PreparedStatement;

public class PreparedStatementSetterImpl<T> implements ContextualSetter<PreparedStatement, T> {

    private final int columnIndex;
    private final ContextualIndexedSetter<PreparedStatement, T> indexedSetter;

    public PreparedStatementSetterImpl(int columnIndex, ContextualIndexedSetter<PreparedStatement, T> indexedSetter) {
        this.columnIndex = columnIndex;
        this.indexedSetter = Asserts.requireNonNull("indexedSetter", indexedSetter);
    }

    @Override
    public void set(PreparedStatement target, T value, Context context) throws Exception {
        indexedSetter.set(target, value, columnIndex, context);
    }
}
