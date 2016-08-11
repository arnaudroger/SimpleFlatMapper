package org.simpleflatmapper.jdbc.property;


import org.simpleflatmapper.jdbc.impl.setter.PreparedStatementSetterImpl;
import org.simpleflatmapper.map.property.SetterFactoryProperty;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.reflect.IndexedSetter;
import org.simpleflatmapper.reflect.IndexedSetterFactory;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.SetterFactory;

import java.sql.PreparedStatement;

public class IndexedSetterFactoryProperty extends SetterFactoryProperty {
    private final IndexedSetterFactory<?, ?> setterFactory;

    public <A> IndexedSetterFactoryProperty(final IndexedSetterFactory<PreparedStatement, A> setterFactory) {
        super(new SetterFactory<PreparedStatement, A>() {
            @Override
            public <P> Setter<PreparedStatement, P> getSetter(A arg) {
                IndexedSetter<PreparedStatement, P> setter =  setterFactory.getIndexedSetter(arg);
                if (setter != null) {
                    return new PreparedStatementSetterImpl<P>(((PropertyMapping)arg).getColumnKey().getIndex(), setter);
                }
                return null;
            }
        });
        this.setterFactory = setterFactory;

    }
    public IndexedSetterFactory<?, ?> getIndexedSetterFactory() {
        return setterFactory;
    }

    @Override
    public String toString() {
        return "IndexedSetterFactory{IndexedSetterFactory}";
    }
}
