package org.simpleflatmapper.jdbc.property;


import org.simpleflatmapper.map.property.SetterFactoryProperty;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.reflect.IndexedSetter;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.SetterFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class IndexedSetterProperty extends SetterFactoryProperty {

    private final IndexedSetter<?, ?> setter;

    public <P> IndexedSetterProperty(final IndexedSetter<PreparedStatement, P> setter) {
        super(new SetterFactory<PreparedStatement, Object>() {
            @SuppressWarnings("unchecked")
            @Override
            public <PP> Setter<PreparedStatement, PP> getSetter(Object arg) {
                final int index = ((PropertyMapping)arg).getColumnKey().getIndex();
                return new Setter<PreparedStatement, PP>() {
                    @Override
                    public void set(PreparedStatement target, PP value) throws Exception {
                        setter.set(target, (P) value, index);
                    }
                };
            }
        }, PreparedStatement.class);
        this.setter = setter;
    }

    public static <T> IndexedSetterProperty of(final PreparedStatementSetter<T> setter) {
        return new IndexedSetterProperty(new IndexedSetter<PreparedStatement, T>() {
            @Override
            public void set(PreparedStatement target, T value, int index) throws Exception {
                setter.set(target, index, value);
            }
        });
    }

    public IndexedSetter<?, ?> getIndexedSetter() {
        return setter;
    }

    @Override
    public String toString() {
        return "IndexedSetter{IndexedSetter}";
    }


    public interface PreparedStatementSetter<T> {
        void set(PreparedStatement ps, int i, T val) throws SQLException;
    }

}
