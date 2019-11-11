package org.simpleflatmapper.map.property;


import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.ContextualGetterFactory;
import org.simpleflatmapper.map.getter.ContextualGetterFactoryAdapter;
import org.simpleflatmapper.reflect.IndexedGetter;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;

public class GetterFactoryProperty {
    private final ContextualGetterFactory<?, ?> getterFactory;
    private final Type sourceType;

    public GetterFactoryProperty(ContextualGetterFactory<?, ?> getterFactory) {
        this(getterFactory, getSourceType(getterFactory));
    }

    public GetterFactoryProperty(ContextualGetterFactory<?, ?> getterFactory, Type sourceType) {
        this.getterFactory = getterFactory;
        this.sourceType = sourceType;
    }

    public GetterFactoryProperty(GetterFactory<?, ?> getterFactory) {
        this(getterFactory, getSourceType(getterFactory));
    }

    public GetterFactoryProperty(GetterFactory<?, ?> getterFactory, Type sourceType) {
        this.getterFactory = new ContextualGetterFactoryAdapter(getterFactory);
        this.sourceType = sourceType;
    }

    public ContextualGetterFactory<?, ?> getGetterFactory() {
        return getterFactory;
    }

    public Type getSourceType() {
        return sourceType;
    }

    @Override
    public String toString() {
        return "GetterFactory{" + getterFactory + "}";
    }

    private static Type getSourceType(ContextualGetterFactory<?, ?> getterFactory) {
        Type[] types = TypeHelper.getGenericParameterForClass(getterFactory.getClass(), ContextualGetterFactory.class);
        return types != null ? types[0] : null;
    }
    private static Type getSourceType(GetterFactory<?, ?> getterFactory) {
        Type[] types = TypeHelper.getGenericParameterForClass(getterFactory.getClass(), GetterFactory.class);
        return types != null ? types[0] : null;
    }


    public static <S, K extends FieldKey<K>, T> GetterFactoryProperty forType(final Type type, final IndexedGetter<S, T> getter) {
        ContextualGetterFactory<S, K> getterFactory = new ContextualGetterFactory<S, K>() {
            @Override
            public <P> ContextualGetter<S, P> newGetter(Type target, K key, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder, Object... properties) {
                if (TypeHelper.areEquals(type, target)) {
                    final int index = key.getIndex();
                    return (ContextualGetter<S, P>) new IndexedGetterAdapter<S, T>(getter, index);
                }
                return null;
            }
        };

        return new GetterFactoryProperty(getterFactory);
    }


    private static class IndexedGetterAdapter<S, T> implements ContextualGetter<S, T> {
        private final IndexedGetter<S, T> getter;
        private final int index;

        public IndexedGetterAdapter(IndexedGetter<S, T> getter, int index) {
            this.getter = getter;
            this.index = index;
        }

        @Override
        public T get(S target, Context context) throws Exception {
            return getter.get(target, index);
        }
    }
}
