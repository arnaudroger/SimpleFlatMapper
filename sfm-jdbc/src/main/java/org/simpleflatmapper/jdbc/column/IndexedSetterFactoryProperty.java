package org.simpleflatmapper.jdbc.column;


import org.simpleflatmapper.jdbc.impl.setter.PreparedStatementSetterImpl;
import org.simpleflatmapper.core.map.column.SetterFactoryProperty;
import org.simpleflatmapper.core.map.mapper.PropertyMapping;
import org.simpleflatmapper.core.reflect.IndexedSetter;
import org.simpleflatmapper.core.reflect.IndexedSetterFactory;
import org.simpleflatmapper.core.reflect.Setter;
import org.simpleflatmapper.core.reflect.SetterFactory;

public class IndexedSetterFactoryProperty extends SetterFactoryProperty {
    private final IndexedSetterFactory<?, ?> setterFactory;

    public IndexedSetterFactoryProperty(final IndexedSetterFactory<?, ?> setterFactory) {
        super(new SetterFactory<Object, Object>() {
            @Override
            public <P> Setter<Object, P> getSetter(Object arg) {
                IndexedSetter setter = ((IndexedSetterFactory)setterFactory).getIndexedSetter(arg);
                if (setter != null) {
                    return new PreparedStatementSetterImpl(((PropertyMapping)arg).getColumnKey().getIndex(), setter);
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
