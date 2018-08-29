package org.simpleflatmapper.jdbc.impl.setter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.util.ErrorHelper;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PreparedStatementIndexSetterOnGetter<I, P> implements PreparedStatementIndexSetter<P> {
    private final Getter<P, I> getter;
    private final PreparedStatementIndexSetter<I> setter;

    public PreparedStatementIndexSetterOnGetter(PreparedStatementIndexSetter<I> setter , Getter<P, I> getter) {
        this.setter = setter;
        this.getter = getter;
    }

    @Override
    public void set(PreparedStatement ps, P value, int columnIndex, Context context) throws SQLException {
        try {
            setter.set(ps, getter.get(value), columnIndex, context);
        } catch (Exception e) {
            ErrorHelper.rethrow(e);
        }
    }
}
