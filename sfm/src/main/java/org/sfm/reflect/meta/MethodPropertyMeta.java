package org.sfm.reflect.meta;

import org.sfm.reflect.Getter;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterHelper;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class MethodPropertyMeta<T, P> extends PropertyMeta<T, P> {

	private final Method setter;
    private final Method getter;
	private final Type type;

	public MethodPropertyMeta(String name, ReflectionService reflectService, Method method, Type type) {
		super(name, reflectService);
		this.setter = method;
		this.type = type;
        this.getter = null;
	}

    public MethodPropertyMeta(String name, ReflectionService reflectService, Method setter, Method getter, Type type) {
        super(name, reflectService);
        this.setter = setter;
        this.getter = getter;
        this.type = type;
    }

	@Override
	protected Setter<T, P> newSetter() {
		return reflectService.getObjectSetterFactory().getMethodSetter(setter);
	}

    @SuppressWarnings("unchecked")
    @Override
    protected Getter<T, P> newGetter() {
        if (getter == null) {
            return reflectService.getObjectGetterFactory().<T, P>getGetter((Class<? super T>) setter.getDeclaringClass(), SetterHelper.getPropertyNameFromMethodName(setter.getName()));
        } else {
            return reflectService.getObjectGetterFactory().<T, P>getMethodGetter(getter);
        }
    }

    @Override
	public Type getPropertyType() {
		return type;
	}

	@Override
	public String getPath() {
		return getName();
	}

    @Override
    public String toString() {
        return "MethodPropertyMeta{" +
                "setter=" + setter +
                ", getter=" + getter +
                ", type=" + type +
                '}';
    }
}
