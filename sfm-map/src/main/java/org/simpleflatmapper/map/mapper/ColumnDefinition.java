package org.simpleflatmapper.map.mapper;


import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.ContextualGetterFactory;
import org.simpleflatmapper.map.property.*;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.SetterFactory;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.simpleflatmapper.util.Asserts.requireNonNull;


public abstract class ColumnDefinition<K extends FieldKey<K>, CD extends ColumnDefinition<K, CD>> {

    public static final Predicate<PropertyMeta<?, ?>> DEFAULT_APPLIES_TO = new Predicate<PropertyMeta<?, ?>>() {
        @Override
        public boolean test(PropertyMeta<?, ?> propertyMeta) {
            return false;
        }
    };
    private final Object[] properties;

    protected ColumnDefinition(Object[] properties) {
        this.properties = requireNonNull("properties", properties);
    }


    public K rename(K key) {
        RenameProperty rp = lookFor(RenameProperty.class);
        if (rp != null) {
            return rp.apply(key);
        }
        return key;
    }

    public boolean ignore() {
        return has(IgnoreProperty.class);
    }

    public boolean has(Class<?> clazz) {
        return lookFor(clazz) != null;
    }

    public boolean isKey() {
        return has(KeyProperty.class);
    }

    public boolean isInferNull() {
        return has(KeyProperty.class) || has(InferNullProperty.class);
    }

    public Predicate<PropertyMeta<?, ?>> keyAppliesTo() {
        KeyProperty kp = lookFor(KeyProperty.class);

        if (kp != null) {
            return kp.getAppliesTo();
        }

        return DEFAULT_APPLIES_TO;
    }
    public Predicate<PropertyMeta<?, ?>> inferNullsAppliesTo() {
        InferNullProperty inp = lookFor(InferNullProperty.class);

        if (inp != null) {
            return inp.getAppliesTo();
        }

        KeyProperty kp = lookFor(KeyProperty.class);

        if (kp != null) {
            return kp.getAppliesTo();
        }

        return DEFAULT_APPLIES_TO;
    }

    public CD compose(ColumnDefinition<K, ?> columnDefinition) {
        ColumnDefinition cdi = requireNonNull("columnDefinition", columnDefinition);
        Object[] properties = Arrays.copyOf(cdi.properties, this.properties.length + cdi.properties.length);
        System.arraycopy(this.properties, 0, properties, cdi.properties.length, this.properties.length);
        return newColumnDefinition(properties);
    }

    public CD add(Object... props) {
        requireNonNull("properties", props);
        Object[] properties = Arrays.copyOf(this.properties, this.properties.length + props.length);
        System.arraycopy(props, 0, properties, this.properties.length, props.length);
        return newColumnDefinition(properties);
    }

    @SuppressWarnings("unchecked")
    public <T> T lookFor(Class<T> propClass) {
        for(Object cp : properties) {
            if (cp != null && propClass.isInstance(cp)) {
                return (T) cp;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> T[] lookForAll(Class<T> propClass) {
        List<T> list = new ArrayList<T>();
        for(Object cp : properties) {
            if (cp != null && propClass.isInstance(cp)) {
                list.add((T) cp);
            }
        }
        return list.toArray((T[]) Array.newInstance(propClass, 0));
    }

    public Getter<?, ?> getCustomGetterFrom(Type fromType) {
        GetterProperty getterPropertyFrom = getCustomGetterPropertyFrom(fromType);
        return getterPropertyFrom != null ? getterPropertyFrom.getGetter() : null;
    }

    public GetterProperty getCustomGetterPropertyFrom(Type fromType) {
        for(GetterProperty getterProperty : lookForAll(GetterProperty.class)) {
            if (getterProperty.getSourceType() == null || TypeHelper.isAssignable(getterProperty.getSourceType(), fromType)) {
                return getterProperty;
            }
        }
        return null;
    }

    public boolean hasCustomSourceFrom(Type ownerType) {
        return getCustomGetterPropertyFrom(ownerType) != null;
    }

    public Type getCustomSourceReturnTypeFrom(Type ownerType) {
        GetterProperty getterProperty = getCustomGetterPropertyFrom(ownerType);
        return getterProperty != null ? getterProperty.getReturnType() : null;

    }

    public ContextualGetterFactory<?, K> getCustomGetterFactoryFrom(Type sourceType) {
        final List<ContextualGetterFactory<?, K>> factories = new ArrayList<ContextualGetterFactory<?, K>>();
        for(GetterFactoryProperty getterFactoryProperty : lookForAll(GetterFactoryProperty.class)) {
            if (getterFactoryProperty.getSourceType() == null || TypeHelper.isAssignable(getterFactoryProperty.getSourceType(), sourceType)) {
                factories.add((ContextualGetterFactory<?, K>) getterFactoryProperty.getGetterFactory());
            }
        }

        if (factories.isEmpty())
            return null;

        if (factories.size() == 1) {
            return (ContextualGetterFactory<?, K>) factories.get(0);
        }

        return new ContextualGetterFactory<Object, K>() {
            @Override
            public <P> ContextualGetter<Object, P> newGetter(Type target, K key, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder, Object... properties) {
                for(ContextualGetterFactory<?, K> fact : factories) {
                    ContextualGetter<?, ?> getter = fact.newGetter(target, key, mappingContextFactoryBuilder, properties);
                    if (getter != null) {
                        return (ContextualGetter<Object, P>) getter;
                    }
                }
                return null;
            }
        };
    }

    public Setter<?, ?> getCustomSetterTo(Type targetType) {
        for(SetterProperty setterProperty : lookForAll(SetterProperty.class)) {
            if (setterProperty.getTargetType() == null || TypeHelper.isAssignable(setterProperty.getTargetType(), targetType)) {
                return setterProperty.getSetter();
            }
        }
        return null;
    }

    public SetterFactory<?, ?> getCustomSetterFactoryTo(Type targetType) {
        for(SetterFactoryProperty getterFactoryProperty : lookForAll(SetterFactoryProperty.class)) {
            if (getterFactoryProperty.getTargetType() == null || TypeHelper.isAssignable(getterFactoryProperty.getTargetType(), targetType)) {
                return getterFactoryProperty.getSetterFactory();
            }
        }
        return null;
    }

    protected abstract CD newColumnDefinition(Object[] properties);

    public CD addRename(String name) {
        return add(new RenameProperty(name));
    }

    public CD addIgnore() {
        return add(new IgnoreProperty());
    }

    public CD addKey() {
        return add(KeyProperty.DEFAULT);
    }

    public CD addKey(Predicate<PropertyMeta<?, ?>> appliesTo) {
        return add(new KeyProperty(appliesTo));
    }

    protected void appendToStringBuilder(StringBuilder sb) {
        for (int i = 0; i < properties.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(properties[i].toString());
        }
    }

    public String toString() {
        StringBuilder sb  = new StringBuilder();

        sb.append("ColumnDefinition{");
        appendToStringBuilder(sb);
        sb.append("}");

        return sb.toString();
    }

    public Object[] properties() {
        return properties;
    }

}
