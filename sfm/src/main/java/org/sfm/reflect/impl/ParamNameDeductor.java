package org.sfm.reflect.impl;

import org.sfm.reflect.EnumHelper;
import org.sfm.reflect.Getter;
import org.sfm.reflect.GetterHelper;
import org.sfm.reflect.InstantiatorDefinition;
import org.sfm.reflect.ObjectGetterFactory;
import org.sfm.reflect.Parameter;
import org.sfm.reflect.ReflectionInstantiatorDefinitionFactory;
import org.sfm.reflect.meta.ClassVisitor;
import org.sfm.reflect.meta.FieldAndMethodCallBack;
import org.sfm.tuples.Tuple2;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParamNameDeductor<T> {
    private final Class<T> target;

    private List<Tuple2<Getter<T, ?>, String>> getters;

    public ParamNameDeductor(Class<T>  target) {
        this.target = target;
    }


    public String findParamName(InstantiatorDefinition instantiatorDefinition, Parameter param) {

        if (getters == null) {
            init();
        }

        Member member  = instantiatorDefinition.getExecutable();

        T value = null;

        try {
            Object expectedValue = newInstance(param);

            Map<Parameter, Getter<? super T, ?>> params = new HashMap<Parameter, Getter<? super T, ?>>();
            params.put(param, new ConstantGetter<T, Object>(expectedValue));
            ArgumentBuilder<T> argumentBuilder = new ArgumentBuilder<T>(instantiatorDefinition, params);

            if (member instanceof Constructor) {
                value = ((Constructor<T>)member).newInstance(argumentBuilder.build(value));
            } else if (member instanceof Method) {
                value = target.cast(((Method) member).invoke(null, argumentBuilder.build(value)));
            }

            if (value != null) {
                for (Tuple2<Getter<T, ?>, String> gn : getters) {
                    try {
                        if (expectedValue.equals(gn.getElement0().get(value))) {
                            return gn.getElement1();
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

    private static final Map<Class<?>, Object> primitives = new HashMap<Class<?>, Object>();
    static {
        primitives.put(byte.class, (byte)1);
        primitives.put(char.class, (char)1);
        primitives.put(short.class, (short)1);
        primitives.put(int.class, (int)1);
        primitives.put(long.class, (long)1);
        primitives.put(float.class, (float)1);
        primitives.put(double.class, (double)1);
    }
    private Object newInstance(Parameter p) throws Exception {
        if (p.getType().isPrimitive()) {
            return primitives.get(p.getType());
        } else if (String.class.equals(p.getType())) {
            return "1";
        } else if (Enum.class.isAssignableFrom(p.getType())) {
            return EnumHelper.getValues((Class)p.getType())[0];
        } else {
            List<InstantiatorDefinition> instantiatorDefinitions = ReflectionInstantiatorDefinitionFactory.extractDefinitions(p.getGenericType());
            Collections.sort(instantiatorDefinitions, new Comparator<InstantiatorDefinition>() {
                @Override
                public int compare(InstantiatorDefinition o1, InstantiatorDefinition o2) {
                    return o1.getParameters().length  - o2.getParameters().length;
                }
            });

            ArgumentBuilder<Object> argumentBuilder = new ArgumentBuilder<Object>(instantiatorDefinitions.get(0), new HashMap<Parameter, Getter<? super Object, ?>>());

            Member member = instantiatorDefinitions.get(0).getExecutable();
            if (member instanceof Constructor) {
                return ((Constructor<?>)member).newInstance(argumentBuilder.build(null));
            } else if (member instanceof Method) {
                return ((Method) member).invoke(null, argumentBuilder.build(null));
            }

        }
        return null;
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
