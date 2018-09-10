package org.simpleflatmapper.jdbc.property;


import org.simpleflatmapper.map.property.SetterFactoryProperty;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.reflect.IndexedSetter;
import org.simpleflatmapper.reflect.IndexedSetterFactory;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.SetterFactory;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;

public class IndexedSetterFactoryProperty extends SetterFactoryProperty {
    private final IndexedSetterFactory<?, ?> setterFactory;

    public IndexedSetterFactoryProperty(final IndexedSetterFactory<PreparedStatement, ?> setterFactory) {
        this(setterFactory, getTargetType(setterFactory));
    }

    public IndexedSetterFactoryProperty(final IndexedSetterFactory<PreparedStatement, ?> setterFactory, Type targetType) {
        super(getSetterFactory(setterFactory), targetType);
        this.setterFactory = setterFactory;
    }

    private static <A> SetterFactory<PreparedStatement, A> getSetterFactory(final IndexedSetterFactory<PreparedStatement, A> setterFactory) {
        return new SetterFactory<PreparedStatement, A>() {
            @Override
            public <P> Setter<PreparedStatement, P> getSetter(A arg) {
                final int index  = ((PropertyMapping)arg).getColumnKey().getIndex();
                final IndexedSetter<PreparedStatement, P> setter =  setterFactory.getIndexedSetter(arg);
                if (setter != null) {
                    return new Setter<PreparedStatement, P>() {
                        @Override
                        public void set(PreparedStatement target, P value) throws Exception {
                            setter.set(target, value, index);
                        }
                    };
                }
                return null;
            }
        };
    }

    public IndexedSetterFactory<?, ?> getIndexedSetterFactory() {
        return setterFactory;
    }

    @Override
    public String toString() {
        return "IndexedSetterFactory{IndexedSetterFactory}";
    }

    private static Type getTargetType(IndexedSetterFactory<?, ?> setterFactory) {
        Type[] types = TypeHelper.getGenericParameterForClass(setterFactory.getClass(), IndexedSetterFactory.class);
        return types != null ? types[0] : null;
    }

}
