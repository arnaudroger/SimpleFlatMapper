package org.sfm.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

public class ReflectionConstructorDefinitionFactory {

    @SuppressWarnings("unchecked")
    public static <T> List<ConstructorDefinition<T>> extractConstructors(Type target) {
        Class<T> clazz = TypeHelper.toClass(target);
        List<ConstructorDefinition<T>> constructorDefinitions = new ArrayList<ConstructorDefinition<T>>();

        for(Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            ConstructorDefinition definition = new ConstructorDefinition(constructor, getConstructorParameters(constructor, target));
            constructorDefinitions.add(definition);
        }


        return constructorDefinitions;
    }

    private static ConstructorParameter[] getConstructorParameters(Constructor<?> constructor, Type target) {
        ConstructorParameter[] parameters = new ConstructorParameter[constructor.getParameterTypes().length];
        TypeVariable<Class<Object>>[] typeParameters = TypeHelper.toClass(target).getTypeParameters();

        for(int i = 0; i < parameters.length; i++) {
            String name = getName(i, constructor);
            Type paramType = constructor.getGenericParameterTypes()[i];
            Type resolvedParamType = null;
            if (paramType instanceof TypeVariable) {
                TypeVariable<?> tv = (TypeVariable<?>) paramType;
                paramType = constructor.getParameterTypes()[i];
                ParameterizedType pt = (ParameterizedType) target;
                for (TypeVariable<Class<Object>> typeParameter : typeParameters) {
                    if (typeParameter.getName().equals(tv.getName())) {
                        resolvedParamType = pt.getActualTypeArguments()[i];
                        break;
                    }
                }
            }
            if (resolvedParamType == null) {
                resolvedParamType = paramType;
            }
            parameters[i] = new ConstructorParameter(name, paramType, resolvedParamType);
        }

        return parameters;
    }

    private static String getName(int i, Constructor<?> constructor) {
        return "arg" + i;
    }
}
