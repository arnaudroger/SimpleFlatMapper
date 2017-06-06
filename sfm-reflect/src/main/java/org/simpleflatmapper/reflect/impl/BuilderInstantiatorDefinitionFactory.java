package org.simpleflatmapper.reflect.impl;

import org.simpleflatmapper.reflect.BuilderInstantiatorDefinition;
import org.simpleflatmapper.reflect.instantiator.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.setter.SetterHelper;
import org.simpleflatmapper.util.TypeHelper;

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
        Map<org.simpleflatmapper.reflect.Parameter, Method> setters = new HashMap<org.simpleflatmapper.reflect.Parameter, Method>();
        List<Method> buildMethods = new ArrayList<Method>();

        int i = 0;

        Class<?> builderClass = TypeHelper.toClass(builderType);
        for(Method m : builderClass.getMethods()) {
            if (!Modifier.isStatic(m.getModifiers()) && Object.class != m.getDeclaringClass()) {
                Type returnType = m.getGenericReturnType();
                if ((TypeHelper.areEquals(returnType, void.class)
                    ||TypeHelper.areEquals(returnType, builderType)) && m.getParameterTypes().length == 1) {
                    // setter
                    org.simpleflatmapper.reflect.Parameter p = new org.simpleflatmapper.reflect.Parameter(i++, SetterHelper.getPropertyNameFromMethodName(m.getName()), m.getParameterTypes()[0], m.getGenericParameterTypes()[0]);
                    setters.put(p, m);
                } else if (TypeHelper.areEquals(returnType, target) && m.getParameterTypes().length == 0) {
                    // build function
                    buildMethods.add(m);
                }
            }
        }
        
        Method buildMethod = selectBuildMethod(buildMethods);

        if (!setters.isEmpty() && buildMethod != null) {
            return new BuilderInstantiatorDefinition(def, setters, buildMethod);
        }
        return null;
    }

    private static Method selectBuildMethod(List<Method> buildMethods) {
        if (buildMethods.isEmpty()) 
            return null;
        
        if (buildMethods.size() == 1)
            return buildMethods.get(0);

        for(Method m : buildMethods) {
            if (m.getName().equals("build")) {
                return m;
            }
        }

        throw new IllegalStateException("Multiple potential build methods candidate " + buildMethods + ", cannot use the builder on that object");
    }
}
