package org.simpleflatmapper.map.impl;

import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.ClassMetaWithDiscriminatorId;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.PropertyFinder;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.util.Consumer;

import java.lang.reflect.Type;
import java.util.List;

public class DiscriminatorClassMeta<T> implements ClassMeta<T> {
    private final ReflectionService reflectionService;
    private final List<ClassMetaWithDiscriminatorId<?>> discriminator;
    private final Type commonType;

    public DiscriminatorClassMeta(Type commonType, List<ClassMetaWithDiscriminatorId<?>> discriminator, ReflectionService reflectionService) {
        this.discriminator = discriminator;
        this.reflectionService = reflectionService;
        this.commonType = commonType;
    }

    @Override
    public ReflectionService getReflectionService() {
        return reflectionService;
    }

    @Override
    public PropertyFinder<T> newPropertyFinder() {
        return new DiscriminatorPropertyFinder<T>(commonType, discriminator, reflectionService);
    }

    @Override
    public Type getType() {
        return commonType;
    }

    @Override
    public List<InstantiatorDefinition> getInstantiatorDefinitions() {
        return null;
    }

    @Override
    public void forEachProperties(Consumer<? super PropertyMeta<T, ?>> consumer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getNumberOfProperties() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean needTransformer() {
        return true;
    }

    @Override
    public ClassMeta<T> withReflectionService(ReflectionService reflectionService) {
        return new DiscriminatorClassMeta<T>(commonType, discriminator, reflectionService);
    }
}
