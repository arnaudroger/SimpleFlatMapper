package org.simpleflatmapper.jdbc.property;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.ContextualGetterFactory;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.map.property.GetterFactoryProperty;
import org.simpleflatmapper.map.property.SetterFactoryProperty;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.SetterFactory;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcGetterFactoryProperty {
    public static <T> GetterFactoryProperty forType(final Class<T> type, final ResultSetGetter<T> getter) {
        ContextualGetterFactory<ResultSet, JdbcColumnKey> getterFactory = new ContextualGetterFactory<ResultSet, JdbcColumnKey>() {
            @Override
            public <P> ContextualGetter<ResultSet, P> newGetter(Type target, JdbcColumnKey key, MappingContextFactoryBuilder<?, JdbcColumnKey> mappingContextFactoryBuilder, Object... properties) {
                if (TypeHelper.areEquals(type, target)) {
                    final int index = key.getIndex();
                    return (ContextualGetter<ResultSet, P>) new ResultSetGetterAdapter<T>(getter, index);
                }
                return null;
            }
        };

        return new GetterFactoryProperty(getterFactory);
    }


    public interface ResultSetGetter<T> {
        T get(ResultSet ps, int i) throws SQLException;
    }

    private static class ResultSetGetterAdapter<T> implements ContextualGetter<ResultSet, T> {
        private final ResultSetGetter<T> getter;
        private final int index;

        public ResultSetGetterAdapter(ResultSetGetter<T> getter, int index) {
            this.getter = getter;
            this.index = index;
        }

        @Override
        public T get(ResultSet target, Context context) throws Exception {
            return getter.get(target, index);
        }
    }
}
