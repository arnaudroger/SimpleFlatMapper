package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.instantiator.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.ScoredGetter;
import org.simpleflatmapper.reflect.ScoredSetter;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.getter.GetterHelper;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FastTupleClassMeta<T> implements ClassMeta<T> {

    private final ClassMeta<T> delegate;
    private final List<InstantiatorDefinition> instantiatorDefinitions;

    public FastTupleClassMeta(Type target, ReflectionService reflectionService) {

        try {
            Class<T> clazz = TypeHelper.toClass(target);
            instantiatorDefinitions = new ArrayList<InstantiatorDefinition>();
            instantiatorDefinitions.add(new ExecutableInstantiatorDefinition(clazz.getConstructor()));
            final List<PropertyMeta<T, ?>> properties = getPropertyMetas(target, reflectionService);
            this.delegate =
                    new ObjectClassMeta<T>(
                        target,
                        instantiatorDefinitions,
                        Collections.<ConstructorPropertyMeta<T, ?>>emptyList(),
                        Collections.<String, String>emptyMap(),
                        properties,
                        reflectionService, 
                            false);
        } catch (NoSuchMethodException e) {
            ErrorHelper.rethrow(e);
            throw new IllegalStateException();
        }
    }

    public FastTupleClassMeta(ClassMeta<T> delegate, List<InstantiatorDefinition> instantiatorDefinitions) {
        this.delegate = delegate;
        this.instantiatorDefinitions = instantiatorDefinitions;
    }

    @Override
    public ClassMeta<T> withReflectionService(ReflectionService reflectionService) {
        return new FastTupleClassMeta<T>(reflectionService.<T>getClassMeta(delegate.getType()), instantiatorDefinitions);
    }

    private static <T> ArrayList<PropertyMeta<T, ?>> getPropertyMetas(Type ownerType, ReflectionService reflectionService) throws NoSuchMethodException {
        final ArrayList<PropertyMeta<T, ?>> propertyMetas = new ArrayList<PropertyMeta<T, ?>>();
        Class<?> clazz = TypeHelper.toClass(ownerType);

        if (isDirect(clazz)) {
            for(Method m : clazz.getDeclaredMethods()) {
                if (m.getParameterTypes().length == 0 && GetterHelper.isPublicMember(m.getModifiers())) {
                    String field = m.getName();

                    Method setter = clazz.getDeclaredMethod(field, m.getReturnType());

                    ObjectPropertyMeta<T, ?> propertyMeta = newPropertyMethod(field, m, setter, reflectionService, ownerType);
                    propertyMetas.add(propertyMeta);
                }
            }
        } else {
            for (Field f : clazz.getDeclaredFields()) {
                String field = f.getName();

                try {
                    Method getter = clazz.getDeclaredMethod(field);
                    Method setter = clazz.getDeclaredMethod(field, f.getType());

                    ObjectPropertyMeta<T, ?> propertyMeta = newPropertyMethod(field, getter, setter, reflectionService, ownerType);
                    propertyMetas.add(propertyMeta);
                } catch (NoSuchMethodException e) {
                    // field has no getter/setter ignore.
                }
            }
        }


        return propertyMetas;
    }

    private static boolean isDirect(Class<?> clazz) {
        try {
            Field unsafe = clazz.getDeclaredField("unsafe");
            int m = unsafe.getModifiers();
            return Modifier.isStatic(m);
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    private static <T, P> ObjectPropertyMeta<T, P> newPropertyMethod(String field, Method getter, Method setter, ReflectionService reflectionService, Type ownerType) {
        Getter<T, P> methodGetter = reflectionService.getObjectGetterFactory().getMethodGetter(getter);
        Setter<T, P> methodSetter = reflectionService.getObjectSetterFactory().getMethodSetter(setter);
        return new ObjectPropertyMeta<T, P>(field, ownerType, reflectionService,
                getter.getGenericReturnType(),
                ScoredGetter.of(methodGetter, 1),
                ScoredSetter.of(methodSetter, 1), null);
    }

    @Override
    public ReflectionService getReflectionService() {
        return delegate.getReflectionService();
    }

    @Override
    public PropertyFinder<T> newPropertyFinder() {
        return delegate.newPropertyFinder();
    }

    @Override
    public Type getType() {
        return delegate.getType();
    }

    @Override
    public List<InstantiatorDefinition> getInstantiatorDefinitions() {
        return instantiatorDefinitions;
    }

    @Override
    public void forEachProperties(Consumer<? super PropertyMeta<T, ?>> consumer) {
        delegate.forEachProperties(consumer);
    }

    @Override
    public int getNumberOfProperties() {
        return delegate.getNumberOfProperties();
    }

    @Override
    public boolean needTransformer() {
        return false;
    }

  

}
