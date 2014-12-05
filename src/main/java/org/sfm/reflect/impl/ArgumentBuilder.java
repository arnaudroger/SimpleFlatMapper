package org.sfm.reflect.impl;

import java.util.HashMap;
import java.util.Map;

import org.sfm.reflect.Getter;
import org.sfm.reflect.TypeHelper;
import org.sfm.reflect.ConstructorDefinition;
import org.sfm.reflect.ConstructorParameter;

public final class ArgumentBuilder<S, T> {

	@SuppressWarnings("rawtypes")
	private static final Getter NULL_GETTER = new Getter() {
		@Override
		public Object get(Object target) throws Exception {
			return null;
		}
	};
	@SuppressWarnings({ "serial", "rawtypes" })
	private static final Map<Class<?>, Getter> DEFAULT_GETTERS = new HashMap<Class<?>, Getter>() {
		{
			put(boolean.class, new Getter() {
				@Override
				public Object get(Object target) throws Exception {
					return Boolean.TRUE;
				}
			});
			put(byte.class,  new Getter() {
				@Override
				public Object get(Object target) throws Exception {
					return new Byte((byte) 0);
				}
			});
			put(char.class, new Getter() {
				@Override
				public Object get(Object target) throws Exception {
					return new Character((char)0);
				}
			});
			put(short.class, new Getter() {
				@Override
				public Object get(Object target) throws Exception {
					return new Short((short)0);
				}
			});
			put(int.class, new Getter() {
				@Override
				public Object get(Object target) throws Exception {
					return new Integer(0);
				}
			});
			put(long.class, new Getter() {
				@Override
				public Object get(Object target) throws Exception {
					return new Long(0);
				}
			});
			put(float.class, new Getter() {
				@Override
				public Object get(Object target) throws Exception {
					return new Float(0.0);
				}
			});
			put(double.class, new Getter() {
				@Override
				public Object get(Object target) throws Exception {
					return new Double(0.0d);
				}
			});
		}
	};	
	
	
	private final Getter<S, ?>[] getters;
	@SuppressWarnings("unchecked")
	public ArgumentBuilder(ConstructorDefinition<T> constructorDefinition,
			Map<ConstructorParameter, Getter<S, ?>> injections) {
		ConstructorParameter[] parameters = constructorDefinition.getParameters();
		getters = new Getter[parameters.length];
		for (int i = 0; i < getters.length; i++) {
			ConstructorParameter param = parameters[i];
			Getter<S, ?> getter = injections.get(param);
			if (getter == null) {
				if (TypeHelper.isPrimitive(param.getType())) {
					getter = DEFAULT_GETTERS.get(param.getType());
				} else {
					getter = NULL_GETTER;
				}
			}
			getters[i] = getter;
		}
		
	}
	
	public Object[] build(S source) throws Exception {
		Object[] args = new Object[getters.length];
		
		for(int i = 0; i < args.length; i++) {
			args[i] = getters[i].get(source);
		}
		
		return args;
	}

}
