package org.simpleflatmapper.reflect.impl;

import org.simpleflatmapper.reflect.getter.GetterHelper;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Instantiator;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.InstantiatorFactory;
import org.simpleflatmapper.reflect.ObjectGetterFactory;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.reflect.ReflectionInstantiatorDefinitionFactory;
import org.simpleflatmapper.reflect.meta.ClassVisitor;
import org.simpleflatmapper.util.TypeHelper;
import org.simpleflatmapper.reflect.getter.ConstantGetter;
import org.simpleflatmapper.reflect.meta.FieldAndMethodCallBack;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParamNameDeductor<T> {

    private static final Map<Class<?>, Object> primitivesMarkValue = new HashMap<Class<?>, Object>();
    static {
        primitivesMarkValue.put(byte.class, (byte) 1);
        primitivesMarkValue.put(char.class, (char) 1);
        primitivesMarkValue.put(short.class, (short) 1);
        primitivesMarkValue.put(int.class, (int) 1);
        primitivesMarkValue.put(long.class, (long) 1);
        primitivesMarkValue.put(float.class, (float) 1);
        primitivesMarkValue.put(double.class, (double) 1);
    }
    private static final Map<Class<?>, Object> primitivesNeutralValue = new HashMap<Class<?>, Object>();
    static {
        primitivesNeutralValue.put(byte.class, (byte) 0);
        primitivesNeutralValue.put(char.class, (char) 0);
        primitivesNeutralValue.put(short.class, (short) 0);
        primitivesNeutralValue.put(int.class, (int) 0);
        primitivesNeutralValue.put(long.class, (long) 0);
        primitivesNeutralValue.put(float.class, (float) 0);
        primitivesNeutralValue.put(double.class, (double) 0);
    }
    
    private final Class<T> target;

    private List<Accessor<T>> accessors;
    private final InstantiatorFactory instantiatorFactory = new InstantiatorFactory(null);

    public ParamNameDeductor(Class<T>  target) {
        this.target = target;
    }


    public String findParamName(InstantiatorDefinition instantiatorDefinition, Parameter param, boolean builderIgnoresNullValues) {
        if (accessors == null) {
            accessors = listAccessors();
        }
        try {
            T value;

            Map<Parameter, Getter<? super Object, ?>> parameters = parametersWithExpectedValue(instantiatorDefinition, param, true, builderIgnoresNullValues);
            Instantiator<Object, T> instantiator = instantiatorFactory.getInstantiator(instantiatorDefinition, Object.class, parameters, false, builderIgnoresNullValues);

            try {
                // try with null values
                value = instantiator.newInstance(null);
            } catch(NullPointerException e) {
                // try with non null explicit values
                parameters = parametersWithExpectedValue(instantiatorDefinition, param, false, builderIgnoresNullValues);
                instantiator = instantiatorFactory.getInstantiator(instantiatorDefinition, Object.class, parameters, false, builderIgnoresNullValues);
                value = instantiator.newInstance(null);
            }

            if (value != null) {
                Object expectedPropertyValue = parameters.get(param).get(null);
                // iterate through all the accessor to find one that returns a matching value
                for (Accessor<T> accessor : accessors) {
                    try {
                        final Object propertyValue = accessor.getter.get(value);
                        if (expectedPropertyValue.equals(propertyValue)) {
                            return accessor.name;
                        }
                    } catch (Exception e) {
                        // IGNORE
                    }
                }
            }

        } catch(Exception e) {
            // IGNORE
        }
        return null;
    }

    private Map<Parameter, Getter<? super Object, ?>> parametersWithExpectedValue(InstantiatorDefinition instantiatorDefinition, Parameter param, boolean allowNull, boolean builderIgnoresNullValues) throws Exception {
        Map<Parameter, Getter<? super Object, ?>> parameterGetterMap = parameters(instantiatorDefinition, allowNull, builderIgnoresNullValues);
        parameterGetterMap.put(param, new ConstantGetter<Object, Object>(markValue(param.getGenericType(), builderIgnoresNullValues)));
        return parameterGetterMap;
    }

    private Map<Parameter, Getter<? super Object, ?>> parameters(InstantiatorDefinition instantiatorDefinition, boolean allowNull, boolean builderIgnoresNullValues) throws Exception {
        Map<Parameter, Getter<? super Object, ?>> parameterGetterMap = new HashMap<Parameter, Getter<? super Object, ?>>();
        for(Parameter parameter : instantiatorDefinition.getParameters()) {
            Object value = neutralValue(parameter.getGenericType(), allowNull, builderIgnoresNullValues);
            parameterGetterMap.put(parameter, new ConstantGetter<Object, Object>(value));
        }
        return parameterGetterMap;
    }

    @SuppressWarnings("unchecked")
    private <V> V markValue(Type type, boolean builderIgnoresNullValues) throws Exception {
        if (TypeHelper.isPrimitive(type)) {
            return (V) primitivesMarkValue.get(type);
        }
        else if (TypeHelper.areEquals(type, String.class)) {
            return (V) "1";
        } else if (TypeHelper.isAssignable(Enum.class, type)) {
            Enum[] values = TypeHelper.<Enum>toClass(type).getEnumConstants();
            return (V) (values.length > 1 ? values[1] : values[0]);
        } else {
            return createValueFromInstantiator(type, builderIgnoresNullValues);
        }
    }

    @SuppressWarnings("unchecked")
    private <V> V neutralValue(Type type, boolean allowNull, boolean builderIgnoresNullValues) throws Exception {
        if (TypeHelper.isPrimitive(type)) {
            return (V) primitivesNeutralValue.get(type);
        }
        if (allowNull)  return null;

        if (TypeHelper.areEquals(type, String.class)) {
            return (V) "0";
        } else if (TypeHelper.isAssignable(Enum.class, type)) {
            Enum[] values = TypeHelper.<Enum>toClass(type).getEnumConstants();
            return (V) values[0];
        } else {
            return createValueFromInstantiator(type, builderIgnoresNullValues);
        }
    }

    private <V> V createValueFromInstantiator(Type type, boolean builderIgnoresNullValues) throws Exception {
        InstantiatorDefinition instantiatorDefinition = InstantiatorFactory.getSmallerConstructor(ReflectionInstantiatorDefinitionFactory.extractDefinitions(type), Collections.<Parameter>emptySet());

        Instantiator<Object, V> instantiator = instantiatorFactory.getInstantiator(instantiatorDefinition, Object.class, parameters(instantiatorDefinition, true, builderIgnoresNullValues), false, builderIgnoresNullValues);
        try {
            return instantiator.newInstance(null);
        } catch (NullPointerException e) {
            instantiator = instantiatorFactory.getInstantiator(instantiatorDefinition, Object.class, parameters(instantiatorDefinition, false, builderIgnoresNullValues), false, builderIgnoresNullValues);
            return instantiator.newInstance(null);
        }
    }

    private List<Accessor<T>> listAccessors() {
        final List<Accessor<T>> list = new ArrayList<Accessor<T>>();
        ClassVisitor.visit(target, new FieldAndMethodCallBack() {
            ObjectGetterFactory objectGetterFactory = new ObjectGetterFactory(null);
            @Override
            public void method(Method method) {
                if (GetterHelper.isGetter(method)) {
                    Getter<T, Object> methodGetter = objectGetterFactory.getMethodGetter(method);
                    list.add(new Accessor<T>(GetterHelper.getPropertyNameFromMethodName(method.getName()), methodGetter));
                }
            }

            @Override
            public void field(Field field) {
                Getter<T, Object> fieldGetter = objectGetterFactory.getFieldGetter(field);
                list.add(new Accessor<T>(field.getName(), fieldGetter));
            }
        });
        return list;
    }

    private static class Accessor<T> {
        private final Getter<T, ?> getter;
        private final String name;

        private Accessor(String name, Getter<T, ?> getter) {
            this.getter = getter;
            this.name = name;
        }

        @Override
        public String toString() {
            return "Accessor{" +
                    "getter=" + getter +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
