package org.sfm.reflect.impl;

import org.sfm.reflect.EnumHelper;
import org.sfm.reflect.Getter;
import org.sfm.reflect.GetterHelper;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.InstantiatorDefinition;
import org.sfm.reflect.InstantiatorFactory;
import org.sfm.reflect.ObjectGetterFactory;
import org.sfm.reflect.Parameter;
import org.sfm.reflect.ReflectionInstantiatorDefinitionFactory;
import org.sfm.reflect.TypeHelper;
import org.sfm.reflect.meta.ClassVisitor;
import org.sfm.reflect.meta.FieldAndMethodCallBack;
import org.sfm.tuples.Tuple2;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParamNameDeductor<T> {
    private final Class<T> target;

    private List<Tuple2<Getter<T, ?>, String>> getters;
    private final InstantiatorFactory instantiatorFactory = new InstantiatorFactory(null);

    public ParamNameDeductor(Class<T>  target) {
        this.target = target;
    }


    public String findParamName(InstantiatorDefinition instantiatorDefinition, Parameter param) {

        if (getters == null) {
            init();
        }


        try {
            T value;

            Map<Parameter, Getter<? super Object, ?>> parameters = parametersWithExpectedValue(instantiatorDefinition, param, true);
            Instantiator<Object, T> instantiator = instantiatorFactory.getInstantiator(instantiatorDefinition, Object.class, parameters, false);

            try {
                value = instantiator.newInstance(null);
            } catch(NullPointerException e) {
                parameters = parametersWithExpectedValue(instantiatorDefinition, param, false);
                instantiator = instantiatorFactory.getInstantiator(instantiatorDefinition, Object.class, parameters, false);
                value = instantiator.newInstance(null);
            }

            if (value != null) {
                Object expectedValue = parameters.get(param).get(null);
                for (Tuple2<Getter<T, ?>, String> gn : getters) {
                    try {
                        if (expectedValue.equals(gn.getElement0().get(value))) {
                            return gn.getElement1();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        // IGNORE
                    }
                }
            }

        } catch(Exception e) {
            e.printStackTrace();

            // IGNORE
        }


        return null;
    }

    private Map<Parameter, Getter<? super Object, ?>> parametersWithExpectedValue(InstantiatorDefinition instantiatorDefinition, Parameter param, boolean allowNull) throws Exception {
        Map<Parameter, Getter<? super Object, ?>> parameterGetterMap = parameters(instantiatorDefinition, allowNull);
        parameterGetterMap.put(param, new ConstantGetter<Object, Object>(markValue(param.getGenericType())));
        return parameterGetterMap;
    }

    private Map<Parameter, Getter<? super Object, ?>> parameters(InstantiatorDefinition instantiatorDefinition, boolean allowNull) throws Exception {
        Map<Parameter, Getter<? super Object, ?>> parameterGetterMap = new HashMap<Parameter, Getter<? super Object, ?>>();
        for(Parameter parameter : instantiatorDefinition.getParameters()) {
            Object value = neutralValue(parameter.getGenericType(), allowNull);
            parameterGetterMap.put(parameter, new ConstantGetter<Object, Object>(value));
        }
        return parameterGetterMap;
    }


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

    @SuppressWarnings("unchecked")
    private <V> V markValue(Type type) throws Exception {
        if (TypeHelper.isPrimitive(type)) {
            return (V) primitivesMarkValue.get(type);
        }
        else if (TypeHelper.areEquals(type, String.class)) {
            return (V) "1";
        } else if (TypeHelper.isAssignable(Enum.class, type)) {
            Enum[] values = EnumHelper.getValues(TypeHelper.<Enum>toClass(type));
            return (V) (values.length > 1 ? values[1] : values[0]);
        } else {
            InstantiatorDefinition instantiatorDefinition = InstantiatorFactory.getSmallerConstructor(ReflectionInstantiatorDefinitionFactory.extractDefinitions(type));

            Instantiator<Object, V> instantiator = instantiatorFactory.getInstantiator(instantiatorDefinition, Object.class, parameters(instantiatorDefinition, true), false);
            try {
                return instantiator.newInstance(null);
            } catch (NullPointerException e) {
                instantiator = instantiatorFactory.getInstantiator(instantiatorDefinition, Object.class, parameters(instantiatorDefinition, false), false);
                return instantiator.newInstance(null);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <V> V neutralValue(Type type, boolean allowNull) throws Exception {
        if (TypeHelper.isPrimitive(type)) {
            return (V) primitivesNeutralValue.get(type);
        }
        if (allowNull)  return null;

        if (TypeHelper.areEquals(type, String.class)) {
            return (V) "0";
        } else if (TypeHelper.isAssignable(Enum.class, type)) {
            Enum[] values = EnumHelper.getValues(TypeHelper.<Enum>toClass(type));
            return (V) values[0];
        } else {
            InstantiatorDefinition instantiatorDefinition = InstantiatorFactory.getSmallerConstructor(ReflectionInstantiatorDefinitionFactory.extractDefinitions(type));

            Instantiator<Object, V> instantiator = instantiatorFactory.getInstantiator(instantiatorDefinition, Object.class, parameters(instantiatorDefinition, true), false);
            try {
                return instantiator.newInstance(null);
            } catch (NullPointerException e) {
                instantiator = instantiatorFactory.getInstantiator(instantiatorDefinition, Object.class, parameters(instantiatorDefinition, false), false);
                return instantiator.newInstance(null);
            }
        }
    }

    private void init() {
        getters = new ArrayList<Tuple2<Getter<T, ?>, String>>();
        ClassVisitor.visit(target, new FieldAndMethodCallBack() {
            ObjectGetterFactory objectGetterFactory = new ObjectGetterFactory(null);
            @Override
            public void method(Method method) {
                if (GetterHelper.isGetter(method)) {
                    Getter<T, Object> methodGetter = objectGetterFactory.getMethodGetter(method);
                    getters.add(new Tuple2<Getter<T, ?>, String>(methodGetter, GetterHelper.getPropertyNameFromMethodName(method.getName())));
                }
            }

            @Override
            public void field(Field field) {
                Getter<T, Object> fieldGetter = objectGetterFactory.getFieldGetter(field);
                getters.add(new Tuple2<Getter<T, ?>, String>(fieldGetter, field.getName()));
            }
        });
    }
}
