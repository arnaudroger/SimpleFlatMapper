package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.getter.IdentityGetter;
import org.simpleflatmapper.reflect.setter.NullSetter;
import org.simpleflatmapper.util.BooleanProvider;

import java.lang.reflect.Type;

public class SelfPropertyMeta<T, E> extends PropertyMeta<T, E> {
    private static final Getter IDENTITY_GETTER = new IdentityGetter();
    public static final String PROPERTY_PATH = "{this}";

    private final BooleanProvider isValid;
    private final ClassMeta<E> classMeta;
    private final Object[] defineProperties;
    private final String originalName;


    public SelfPropertyMeta(ReflectionService reflectService, Type type, BooleanProvider isValid, Object[] defineProperties, String originalName, ClassMeta<E> classMeta) {
        super("self", type, reflectService);
        this.originalName = originalName;
        this.isValid = isValid;
        this.defineProperties = defineProperties;
        this.classMeta = classMeta;
    }

    @Override
    protected ClassMeta<E> newPropertyClassMeta() {
        return classMeta;
    }


    @Override
    public PropertyMeta<T, E> withReflectionService(ReflectionService reflectionService) {
        return new SelfPropertyMeta<T, E>(reflectionService, getOwnerType(), isValid, defineProperties, originalName, classMeta);
    }

    @Override
    public PropertyMeta<T, E> toNonMapped() {
        return new NonMappedPropertyMeta<T, E>(getName(), getOwnerType(), reflectService, defineProperties);
    }

    @Override
    public Object[] getDefinedProperties() {
        return defineProperties;
    }

    @Override
    public Setter<? super T, ? super E> getSetter() {
        return NullSetter.NULL_SETTER;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Getter<T, E> getGetter() {
        return (Getter<T, E>) IDENTITY_GETTER;

    }

    @Override
    public Type getPropertyType() {
        return getOwnerType();
    }

    @Override
    public String getPath() {
        return PROPERTY_PATH;
    }

    @Override
    public String toString() {
        return "SelfPropertyMeta{" +
                "type=" + getOwnerType() +
                ",name=" + getName() +
                '}';
    }

    @Override
    public boolean isValid() {
        return isValid.getBoolean();
    }

    @Override
    public boolean isSelf() {
        return true;
    }



}
