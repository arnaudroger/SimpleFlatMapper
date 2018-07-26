package org.simpleflatmapper.reflect;


import java.lang.reflect.Method;
import java.util.Map;

public final class BuilderInstantiatorDefinition implements InstantiatorDefinition {
	private final InstantiatorDefinition builderInstantiator;
	private final Map<Parameter, Method> setters;
	private final Method buildMethod;

	public BuilderInstantiatorDefinition(InstantiatorDefinition builderInstantiator,
										 Map<Parameter, Method> setters, Method buildMethod) {
		this.builderInstantiator = builderInstantiator;
		this.setters = setters;
		this.buildMethod = buildMethod;
	}


	@Override
	public Parameter[] getParameters() {

		Parameter[] parameters = new Parameter[setters.size()];

		int i = 0;
		for(Parameter p : setters.keySet()) {
			parameters[i++] = p;
		}
		return parameters;

	}

	public InstantiatorDefinition getBuilderInstantiator() {
		return builderInstantiator;
	}

	public Map<Parameter, Method> getSetters() {
		return setters;
	}

	public Method getBuildMethod() {
		return buildMethod;
	}

	@Override
	public boolean hasParam(Parameter param) {
		return setters.containsKey(param);
	}

	@Override
	public Type getType() {
		return Type.BUILDER;
	}

	@Override
	public String getName() {
		return buildMethod.getDeclaringClass().getName();
	}

	public boolean isMutable() {
		for(Method m : setters.values()) {
			Class<?> returnType = m.getReturnType();
			if (returnType != void.class && returnType != Void.class) {
				return false;
			}
		}
		return true;
	}
}
