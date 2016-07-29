package org.sfm.map.column;


import org.sfm.jdbc.impl.setter.PreparedStatementSetterImpl;
import org.sfm.map.mapper.PropertyMapping;
import org.sfm.reflect.IndexedSetter;
import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterFactory;

public class IndexedSetterProperty extends SetterFactoryProperty {

    private final IndexedSetter<?, ?> setter;

    public IndexedSetterProperty(final IndexedSetter<?, ?> setter) {
        super(new SetterFactory<Object, Object>() {
            @Override
            public <P> Setter<Object, P> getSetter(Object arg) {
                return new PreparedStatementSetterImpl(((PropertyMapping)arg).getColumnKey().getIndex(), setter);
            }
        });
        this.setter = setter;
    }

    public IndexedSetter<?, ?> getIndexedSetter() {
        return setter;
    }

    @Override
    public String toString() {
        return "IndexedSetter{IndexedSetter}";
    }
}
