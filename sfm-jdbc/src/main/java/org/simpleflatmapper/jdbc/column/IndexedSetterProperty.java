package org.simpleflatmapper.jdbc.column;


import org.simpleflatmapper.jdbc.impl.setter.PreparedStatementSetterImpl;
import org.simpleflatmapper.map.property.SetterFactoryProperty;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.reflect.IndexedSetter;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.SetterFactory;

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
