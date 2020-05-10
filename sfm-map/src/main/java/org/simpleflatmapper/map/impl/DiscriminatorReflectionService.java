package org.simpleflatmapper.map.impl;

import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.InstantiatorFactory;
import org.simpleflatmapper.reflect.ObjectGetterFactory;
import org.simpleflatmapper.reflect.ObjectSetterFactory;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.asm.AsmFactory;
import org.simpleflatmapper.reflect.ClassMetaWithDiscriminatorId;
import org.simpleflatmapper.reflect.meta.AliasProvider;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.util.TypeHelper;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public class DiscriminatorReflectionService extends ReflectionService {
    
    private final ReflectionService delegate;
    private final Map<Class<?>, List<ClassMetaWithDiscriminatorId<?>>> discriminators;
    private final ConcurrentMap<Type, ClassMeta<?>> metaCache = new ConcurrentHashMap<Type, ClassMeta<?>>();

    public DiscriminatorReflectionService(ReflectionService delegate, Map<Class<?>, List<ClassMetaWithDiscriminatorId<?>>> discriminators) {
        this.delegate = delegate;
        this.discriminators = discriminators;
    }

    @Override
    public void registerClassMeta(Type type, ClassMeta<?> classMeta) {
        delegate.registerClassMeta(type, classMeta);
    }

    @Override
    public ObjectSetterFactory getObjectSetterFactory() {
        return delegate.getObjectSetterFactory();
    }

    @Override
    public InstantiatorFactory getInstantiatorFactory() {
        return delegate.getInstantiatorFactory();
    }

    @Override
    public boolean isAsmActivated() {
        return delegate.isAsmActivated();
    }

    @Override
    public AsmFactory getAsmFactory(ClassLoader classLoader) {
        return delegate.getAsmFactory(classLoader);
    }

    @Override
    public <T> ClassMeta<T> getClassMeta(Type target) {

        requireNonNull("target", target);
        ClassMeta<T> meta = (ClassMeta<T>) metaCache.get(target);
        if (meta == null) {
            meta = newClassMeta(target);
            requireNonNull("meta", meta);
            metaCache.putIfAbsent(target, meta);
        }
        return meta;
    }
        
    private <T> ClassMeta<T> newClassMeta(Type target) {
        List<ClassMetaWithDiscriminatorId<?>> implementations = discriminators.get(TypeHelper.toClass(target));
        if (implementations == null || implementations.isEmpty()) {
            ClassMeta<T> classMeta = delegate.getClassMeta(target);
            return classMeta.withReflectionService(this);
        }
        
        if (implementations.size() == 1) {
            // only one implementation
            ClassMetaWithDiscriminatorId<?> metaWithDiscriminatorId = implementations.get(0);
            return (ClassMeta<T>) metaWithDiscriminatorId.withReflectionService(this).classMeta;
        }
        
        List<ClassMetaWithDiscriminatorId<?>> reassignedImplementations = new ArrayList<ClassMetaWithDiscriminatorId<?>>();
        
        for(ClassMetaWithDiscriminatorId<?> cm : implementations) {
            reassignedImplementations.add(cm.withReflectionService(this));
        }
        
        return new DiscriminatorClassMeta<T>(target, reassignedImplementations, this);
    }

    @Override
    public <T> ClassMeta<T> getClassMetaExtraInstantiator(Type target, Member builderInstantiator) {
        return delegate.getClassMetaExtraInstantiator(target, builderInstantiator);
    }

    @Override
    public String getColumnName(Method method) {
        return delegate.getColumnName(method);
    }

    @Override
    public String getColumnName(Field field) {
        return delegate.getColumnName(field);
    }

    @Override
    public List<InstantiatorDefinition> extractInstantiator(Type target, Member extraInstantiator) throws IOException {
        return delegate.extractInstantiator(target, extraInstantiator);
    }

    @Override
    public ObjectGetterFactory getObjectGetterFactory() {
        return delegate.getObjectGetterFactory();
    }

    @Override
    public DiscriminatorReflectionService withAliasProvider(AliasProvider aliasProvider) {
        return new DiscriminatorReflectionService(delegate.withAliasProvider(aliasProvider), discriminators);
    }

    @Override
    public DiscriminatorReflectionService withBuilderIgnoresNullValues(boolean builderIgnoresNullValues) {
        return new DiscriminatorReflectionService(delegate.withBuilderIgnoresNullValues(builderIgnoresNullValues), discriminators);
    }

    @Override
    @Deprecated
    public DiscriminatorReflectionService withSelfScoreFullName(boolean selfScoreFullName) {
        return this;
    }

    @Override
    public boolean builderIgnoresNullValues() {
        return delegate.builderIgnoresNullValues();
    }

    @Override
    public boolean selfScoreFullName() {
        return delegate.selfScoreFullName();
    }

    @Override
    public void registerBuilder(String name, DefaultBuilderSupplier defaultBuilderSupplier) {
        delegate.registerBuilder(name, defaultBuilderSupplier);
    }
}
