package org.simpleflatmapper.jdbc.property;

import org.simpleflatmapper.map.property.GetterFactoryProperty;
import org.simpleflatmapper.reflect.IndexedGetter;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcGetterFactoryProperty {
    public static <T> GetterFactoryProperty forType(final Class<T> type, final ResultSetGetter<T> getter) {
        return GetterFactoryProperty.forType(type, getter);
    }

    public interface ResultSetGetter<T> extends IndexedGetter<ResultSet, T> {
        T get(ResultSet ps, int i) throws SQLException;
    }

}
