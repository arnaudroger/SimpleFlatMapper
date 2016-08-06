package org.simpleflatmapper.reflect.impl;

import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.util.TypeHelper;

import java.util.HashMap;
import java.util.Map;

public final class ArgumentBuilder<S> {

	@SuppressWarnings("rawtypes")
	private static final Getter NULL_GETTER = new Getter() {
		@Override
		public Object get(Object target) throws Exception {
			return null;
		}
	};
	@SuppressWarnings({  "rawtypes" })
	private static final Map<Class<?>, Getter> DEFAULT_GETTERS = new HashMap<Class<?>, Getter>();
	static {
		DEFAULT_GETTERS.put(boolean.class, new Getter() {
			@Override
			public Object get(Object target) throws Exception {
				return Boolean.TRUE;
			}
		});
		DEFAULT_GETTERS.put(byte.class, new Getter() {
			@Override
			public Object get(Object target) throws Exception {
				return (byte) 0;
			}
		});
		DEFAULT_GETTERS.put(char.class, new Getter() {
			@Override
			public Object get(Object target) throws Exception {
				return (char) 0;
			}
		});
		DEFAULT_GETTERS.put(short.class, new Getter() {
			@Override
			public Object get(Object target) throws Exception {
				return (short) 0;
			}
		});
		DEFAULT_GETTERS.put(int.class, new Getter() {
			@Override
			public Object get(Object target) throws Exception {
				return 0;
			}
		});
		DEFAULT_GETTERS.put(long.class, new Getter() {
			@Override
			public Object get(Object target) throws Exception {
				return (long) 0;
			}
		});
		DEFAULT_GETTERS.put(float.class, new Getter() {
			@Override
			public Object get(Object target) throws Exception {
				return 0.0f;
			}
		});
		DEFAULT_GETTERS.put(double.class, new Getter() {
				@Override
				public Object get(Object target) throws Exception {
					return 0.0d;
				}
			});
	}
	
	
	private final Getter<? super S, ?>[] getters;

	@SuppressWarnings("unchecked")
	public ArgumentBuilder(InstantiatorDefinition instantiatorDefinition,
			Map<Parameter, Getter<? super S, ?>> injections) {
		Parameter[] parameters = instantiatorDefinition.getParameters();
		getters = new Getter[parameters.length];
		for (int i = 0; i < getters.length; i++) {
			Parameter param = parameters[i];
			Getter<? super S, ?> getter = injections.get(param);
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
