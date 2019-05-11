package org.simpleflatmapper.jdbc.property;

import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.map.property.SetterFactoryProperty;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.SetterFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcSetterFactoryProperty {
    public static <T> SetterFactoryProperty of(final PreparedStatementSetter<T> setter) {
        SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey>> setterFactory = new SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey>>() {
            @Override
            public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey> arg) {
                final int columnIndex = arg.getColumnKey().getIndex();
                return new Setter<PreparedStatement, P>() {
                    @Override
                    public void set(PreparedStatement target, P value) throws Exception {
                        setter.set(target, columnIndex, (T) value);
                    }
                };
            }
        };

        return new SetterFactoryProperty(setterFactory);
    }


    public interface PreparedStatementSetter<T> {
        void set(PreparedStatement ps, int i, T val) throws SQLException;
    }
}
