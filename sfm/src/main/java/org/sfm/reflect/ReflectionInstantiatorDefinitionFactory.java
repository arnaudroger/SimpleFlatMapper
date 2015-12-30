package org.sfm.reflect;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

public class ReflectionInstantiatorDefinitionFactory {

    @SuppressWarnings("unchecked")
    public static List<InstantiatorDefinition> extractDefinitions(Type target) {
        Class<?> clazz = TypeHelper.toClass(target);
        List<InstantiatorDefinition> instantiatorDefinitions = new ArrayList<InstantiatorDefinition>();

        for(Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (Modifier.isPublic(constructor.getModifiers())) {
                InstantiatorDefinition definition = new InstantiatorDefinition(constructor, getParameters(constructor, target));
                instantiatorDefinitions.add(definition);
            }
        }

        for(Method m : clazz.getDeclaredMethods()) {
            if (Modifier.isPublic(m.getModifiers())
                && Modifier.isStatic(m.getModifiers())
                && clazz.isAssignableFrom(m.getReturnType())) {
                InstantiatorDefinition definition = new InstantiatorDefinition(m, getParameters(m, target));
                instantiatorDefinitions.add(definition);
            }
        }

        return instantiatorDefinitions;
    }

    private static Parameter[] getParameters(Constructor<?> constructor, Type target) {
        return buildParameters(target, constructor.getParameterTypes(), constructor.getGenericParameterTypes(), TypeHelper.toClass(target).getTypeParameters());
    }


    private static Parameter[] getParameters(Method method, Type target) {
        return buildParameters(target, method.getParameterTypes(), method.getGenericParameterTypes(), TypeHelper.toClass(target).getTypeParameters());
    }

    private static Parameter[] buildParameters(Type target, Class<?>[] parameterTypes, Type[] parameterGenericTypes, TypeVariable<Class<Object>>[] targetClassTypeParameters) {
        Parameter[] parameters = new Parameter[parameterTypes.length];

        for(int i = 0; i < parameters.length; i++) {
            Type paramType = parameterGenericTypes[i];
            Type resolvedParamType = null;
            if (paramType instanceof TypeVariable) {
                TypeVariable<?> tv = (TypeVariable<?>) paramType;
                paramType = parameterTypes[i];
                ParameterizedType pt = (ParameterizedType)target;
                for (TypeVariable<Class<Object>> typeParameter : targetClassTypeParameters) {
                    if (typeParameter.getName().equals(tv.getName())) {
                        resolvedParamType = pt.getActualTypeArguments()[i];
                        break;
                    }
                }
            }
            if (resolvedParamType == null) {
                resolvedParamType = paramType;
            }
            parameters[i] = new Parameter(i, null, TypeHelper.toClass(paramType), resolvedParamType);
        }

        return parameters;
    }
}
