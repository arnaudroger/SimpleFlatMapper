package org.sfm.jdbc.column;


import org.sfm.jdbc.impl.setter.PreparedStatementSetterImpl;
import org.sfm.map.column.SetterFactoryProperty;
import org.sfm.map.mapper.PropertyMapping;
import org.sfm.reflect.IndexedSetter;
import org.sfm.reflect.IndexedSetterFactory;
import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterFactory;

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
