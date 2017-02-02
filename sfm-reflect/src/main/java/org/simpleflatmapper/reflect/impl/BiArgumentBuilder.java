package org.simpleflatmapper.reflect.impl;

import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.util.BiFunction;
import org.simpleflatmapper.util.TypeHelper;

import java.util.HashMap;
import java.util.Map;

public final class BiArgumentBuilder<S1, S2> {

	@SuppressWarnings("rawtypes")
	private static final BiFunction NULL_FACTORIES = new BiFunction() {
		@Override
		public Object apply(Object o, Object o2) {
			return null;
		}
	};
	@SuppressWarnings({  "rawtypes" })
	private static final Map<Class<?>, BiFunction> DEFAULT_FACTORIES = new HashMap<Class<?>, BiFunction>();
	static {
		DEFAULT_FACTORIES.put(boolean.class, new BiFunction() {
			@Override
			public Object apply(Object o, Object o2) {
				return Boolean.TRUE;
			}
		});
		DEFAULT_FACTORIES.put(byte.class, new BiFunction() {
			@Override
			public Object apply(Object o, Object o2) {
				return (byte) 0;
			}
		});
		DEFAULT_FACTORIES.put(char.class, new BiFunction() {
			@Override
			public Object apply(Object o, Object o2) {
				return (char) 0;
			}
		});
		DEFAULT_FACTORIES.put(short.class, new BiFunction() {
			@Override
			public Object apply(Object o, Object o2) {
				return (short) 0;
			}
		});
		DEFAULT_FACTORIES.put(int.class, new BiFunction() {
			@Override
			public Object apply(Object o, Object o2) {
				return 0;
			}
		});
		DEFAULT_FACTORIES.put(long.class, new BiFunction() {
			@Override
			public Object apply(Object o, Object o2) {
				return (long) 0;
			}
		});
		DEFAULT_FACTORIES.put(float.class, new BiFunction() {
			@Override
			public Object apply(Object o, Object o2) {
				return 0.0f;
			}
		});
		DEFAULT_FACTORIES.put(double.class, new BiFunction() {
				@Override
				public Object apply(Object o, Object o2) {
					return 0.0d;
				}
			});
	}


	private final BiFunction<? super S1, ? super S2, ?>[] factories;

	@SuppressWarnings("unchecked")
	public BiArgumentBuilder(InstantiatorDefinition instantiatorDefinition,
                             Map<Parameter, BiFunction<? super S1, ? super S2, ?>> injections) {
		Parameter[] parameters = instantiatorDefinition.getParameters();
		factories = new BiFunction[parameters.length];
		for (int i = 0; i < factories.length; i++) {
			Parameter param = parameters[i];
			BiFunction<? super S1, ? super S2, ?> factory = injections.get(param);
			if (factory == null) {
				if (TypeHelper.isPrimitive(param.getType())) {
					factory = DEFAULT_FACTORIES.get(param.getType());
				} else {
					factory = NULL_FACTORIES;
				}
			}
			factories[i] = factory;
		}
		
	}
	
	public Object[] build(S1 s1, S2 s2) throws Exception {
		Object[] args = new Object[factories.length];
		
		for(int i = 0; i < args.length; i++) {
			args[i] = factories[i].apply(s1, s2);
		}
		
		return args;
	}

}
