package org.sfm.reflect;

import org.sfm.reflect.meta.*;
import org.sfm.utils.ErrorHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class FastTupleClassMeta<T> implements ClassMeta<T> {

    private final ClassMeta<T> delegate;
    private final String[] headers;
    private final List<InstantiatorDefinition> instantiatorDefinitions;

    public FastTupleClassMeta(Type target, ReflectionService reflectionService) {

        try {
            Class<T> clazz = TypeHelper.toClass(target);
            instantiatorDefinitions = new ArrayList<InstantiatorDefinition>();
            instantiatorDefinitions.add(new InstantiatorDefinition(clazz.getConstructor()));
            final List<PropertyMeta<T, ?>> properties = getPropertyMetas(clazz, reflectionService);
            this.delegate = new ObjectClassMeta<T>(target,
                    instantiatorDefinitions, new ArrayList<ConstructorPropertyMeta<T, ?>>(), properties, reflectionService);
            this.headers = getHeaders(clazz, properties);
        } catch (NoSuchMethodException e) {
            ErrorHelper.rethrow(e);
            throw new IllegalStateException();
        }
    }

    private String[] getHeaders(Class<?> clazz, List<PropertyMeta<T, ?>> properties) {
        try {
            clazz.getDeclaredField("unsafe");
            return null;
        } catch(NoSuchFieldException e) {
            final Field[] declaredFields = clazz.getDeclaredFields();
            String[] headers = new String[properties.size()];

            for(int i = 0; i < declaredFields.length; i++) {

                String name = declaredFields[i].getName();
                if (isPresent(properties, name)) {
                    headers[i] = name;
                }
            }

            return headers;
        }

    }

    private boolean isPresent(List<PropertyMeta<T, ?>> properties, String name) {
        for(PropertyMeta<T, ?> pm : properties) {
            if (pm.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<PropertyMeta<T, ?>> getPropertyMetas(Class<T> clazz, ReflectionService reflectionService) throws NoSuchMethodException {
        final ArrayList<PropertyMeta<T, ?>> propertyMetas = new ArrayList<PropertyMeta<T, ?>>();

        for(Method m : clazz.getDeclaredMethods()) {
            if (m.getParameterTypes().length == 0 && GetterHelper.isPublicMember(m.getModifiers())) {
                String field = m.getName();

                Method setter = clazz.getDeclaredMethod(field, m.getReturnType());

                ObjectPropertyMeta<T, ?> propertyMeta = newPropertyMethod(field, m, setter, reflectionService);
                propertyMetas.add(propertyMeta);
            }
        }


        return propertyMetas;
    }

    private <P> ObjectPropertyMeta<T, P> newPropertyMethod(String field, Method getter, Method setter, ReflectionService reflectionService) {
        Getter<T, P> methodGetter = reflectionService.getObjectGetterFactory().getMethodGetter(getter);
        Setter<T, P> methodSetter = reflectionService.getObjectSetterFactory().getMethodSetter(setter);
        return new ObjectPropertyMeta<T, P>(field, reflectionService,
                getter.getGenericReturnType(),
                ScoredGetter.of(methodGetter, 1),
                ScoredSetter.of(methodSetter, 1));
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
    public String[] generateHeaders() {
        if (headers == null) {
            throw new UnsupportedOperationException("Cannot generate headers on directMemory tuple");
        }  else {
            return headers;
        }
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public List<InstantiatorDefinition> getInstantiatorDefinitions() {
        return instantiatorDefinitions;
    }

}
