package org.sfm.reflect;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuilderInstantiatorDefinitionFactory {
    public static List<InstantiatorDefinition> extractDefinitions(Type target) {

        List<InstantiatorDefinition> instantiatorDefinitions = new ArrayList<InstantiatorDefinition>();
        Class<?> clazz = TypeHelper.toClass(target);

        for(Method m : clazz.getDeclaredMethods()) {
            if (isPotentialBuilderMethod(m)) {
                BuilderInstantiatorDefinition def = getDefinitionForBuilderFromMethod(m, target);
                if (def != null) {
                    instantiatorDefinitions.add(def);
                }
            }
        }

        return instantiatorDefinitions;
    }

    private static boolean isPotentialBuilderMethod(Method m) {
        return
                Modifier.isStatic(m.getModifiers())
                        && m.getParameterTypes().length == 0
                && ! Void.TYPE.equals(m.getReturnType())
                && ! TypeHelper.areEquals(m.getReturnType(), m.getDeclaringClass());
    }

    private static BuilderInstantiatorDefinition getDefinitionForBuilderFromMethod(Method m, Type target) {
        if (Modifier.isStatic(m.getModifiers())
                && Modifier.isPublic(m.getModifiers())
                && m.getParameterTypes().length == 0
                && !(Void.TYPE.equals(m.getReturnType()))) {
            return  getDefinitionForBuilder(m , target);
        }
        return null;
    }

    public static BuilderInstantiatorDefinition getDefinitionForBuilder(Member e, Type target) {
        ExecutableInstantiatorDefinition def = new ExecutableInstantiatorDefinition(e);
        Type builderType;
        if (e instanceof Constructor) {
            builderType = ((Constructor)e).getDeclaringClass();
        } else {
            builderType = ((Method)e).getGenericReturnType();

        }
        return getDefinitionForBuilder(def, builderType, target);
    }


    private static BuilderInstantiatorDefinition getDefinitionForBuilder(ExecutableInstantiatorDefinition def,
                                                                         Type builderType, Type target) {
        Map<Parameter, Method> setters = new HashMap<Parameter, Method>();
        Method buildMethod = null;

        int i = 0;

        for(Method m : TypeHelper.toClass(builderType).getMethods()) {
            if (!Modifier.isStatic(m.getModifiers())) {
                Type returnType = m.getGenericReturnType();
                if (TypeHelper.areEquals(returnType, builderType) && m.getParameterTypes().length == 1) {
                    // setter
                    Parameter p = new Parameter(i++, m.getName(), m.getParameterTypes()[0], m.getGenericParameterTypes()[0]);
                    setters.put(p, m);
                } else if (TypeHelper.areEquals(returnType, target) && m.getParameterTypes().length == 0) {
                    // build function
                    if (buildMethod != null) {
                        throw new IllegalStateException("Duplicate potential build method " + buildMethod + " and " + m);
                    }
                    buildMethod= m;
                }
            }
        }


        if (!setters.isEmpty() || buildMethod != null) {
            return new BuilderInstantiatorDefinition(def, setters, buildMethod);
        }
        return null;
    }
}
