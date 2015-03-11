package org.sfm.reflect;

import org.sfm.map.MapperBuildingException;
import org.sfm.reflect.meta.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class FastTupleClassMeta<T> implements ClassMeta<T> {

    private final ClassMeta<T> delegate;
    private final String[] headers;

    public FastTupleClassMeta(Type target, ReflectionService reflectionService) {

        try {
            Class<T> clazz = TypeHelper.toClass(target);
            final List<ConstructorDefinition<T>> constructorDefinitions = new ArrayList<ConstructorDefinition<T>>();
            constructorDefinitions.add(new ConstructorDefinition<T>(clazz.getConstructor()));
            final List<PropertyMeta<T, ?>> properties = getPropertyMetas(clazz, reflectionService);
            this.delegate = new ObjectClassMeta<T>(target,
                    constructorDefinitions, new ArrayList<>(), properties, reflectionService);
            this.headers = getHeaders(clazz);
        } catch (NoSuchMethodException e) {
            throw new MapperBuildingException(e.getMessage(), e);
        }
    }

    private String[] getHeaders(Class<?> clazz) {
        try {
            clazz.getDeclaredField("unsafe");
            return null;
        } catch(NoSuchFieldException e) {
            final Field[] declaredFields = clazz.getDeclaredFields();
            String[] headers = new String[declaredFields.length];

            for(int i = 0; i < declaredFields.length; i++) {
                headers[i] = declaredFields[i].getName();
            }

            return headers;
        }

    }

    private ArrayList<PropertyMeta<T, ?>> getPropertyMetas(Class<T> clazz, ReflectionService reflectionService) throws NoSuchMethodException {
        final ArrayList<PropertyMeta<T, ?>> propertyMetas = new ArrayList<PropertyMeta<T, ?>>();

        for(Method m : clazz.getDeclaredMethods()) {
            if (m.getParameterCount() == 0) {
                String field = m.getName();

                Method getter = m;
                Method setter = clazz.getDeclaredMethod(field, getter.getReturnType());

                MethodPropertyMeta<T, ?> propertyMeta = newPropertyMethod(field, getter, setter, reflectionService);
                propertyMetas.add(propertyMeta);
            }
        }


        return propertyMetas;
    }

    private <P> MethodPropertyMeta<T, P> newPropertyMethod(String field, Method getter, Method setter, ReflectionService reflectionService) {
        return new MethodPropertyMeta<T, P>(field, field, reflectionService, setter, getter, getter.getGenericReturnType());
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
}
