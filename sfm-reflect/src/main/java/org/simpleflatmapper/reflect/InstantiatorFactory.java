package org.simpleflatmapper.reflect;

import org.simpleflatmapper.reflect.asm.AsmFactory;
import org.simpleflatmapper.reflect.asm.AsmFactoryProvider;
import org.simpleflatmapper.reflect.asm.BiInstantiatorKey;
import org.simpleflatmapper.reflect.asm.InstantiatorKey;
import org.simpleflatmapper.reflect.getter.IdentityGetter;
import org.simpleflatmapper.reflect.impl.BuilderInstantiator;
import org.simpleflatmapper.reflect.impl.EmptyConstructorBiInstantiator;
import org.simpleflatmapper.reflect.impl.EmptyConstructorInstantiator;
import org.simpleflatmapper.reflect.impl.EmptyStaticMethodBiInstantiator;
import org.simpleflatmapper.reflect.impl.EmptyStaticMethodInstantiator;
import org.simpleflatmapper.reflect.impl.InjectConstructorBiInstantiator;
import org.simpleflatmapper.reflect.impl.InjectConstructorInstantiator;
import org.simpleflatmapper.reflect.impl.InjectStaticMethodBiInstantiator;
import org.simpleflatmapper.reflect.impl.InjectStaticMethodInstantiator;
import org.simpleflatmapper.reflect.instantiator.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.reflect.instantiator.InstantiatorDefinitions;
import org.simpleflatmapper.reflect.instantiator.KotlinDefaultConstructorInstantiatorDefinition;
import org.simpleflatmapper.util.BiFunction;
import org.simpleflatmapper.util.ErrorHelper;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InstantiatorFactory {
	private final AsmFactoryProvider asmFactory;

	private final boolean failOnAsmError;
	
	public InstantiatorFactory(final AsmFactoryProvider asmFactory) {
		this(asmFactory, false);
	}

	public InstantiatorFactory(AsmFactoryProvider asmFactory, boolean faileOnAsmError) {
		this.asmFactory = asmFactory;
		this.failOnAsmError = faileOnAsmError;
	}


	@SuppressWarnings("unchecked")
	public <S1, S2, T> BiInstantiator<S1, S2, T> getBiInstantiator(Type target, final Class<?> s1, final Class<?> s2, List<InstantiatorDefinition> constructors, Map<Parameter, BiFunction<? super S1, ? super S2, ?>> injections, boolean useAsmIfEnabled, boolean builderIgnoresNullValues) throws SecurityException {
		final InstantiatorDefinition instantiatorDefinition = getSmallerConstructor(constructors, injections.keySet());

		if (instantiatorDefinition == null) {
			throw new IllegalArgumentException("No constructor available for " + target);
		}
		return this.getBiInstantiator(instantiatorDefinition, s1, s2, injections, useAsmIfEnabled, builderIgnoresNullValues);


	}

	@SuppressWarnings("unchecked")
	public <S1, S2, T> BiInstantiator<S1, S2, T> getBiInstantiator(InstantiatorDefinition instantiatorDefinition, Class<?> s1, Class<?> s2, Map<Parameter, BiFunction<? super S1, ? super S2, ?>> injections, boolean useAsmIfEnabled, boolean builderIgnoresNullValues) {
		checkParameters(instantiatorDefinition, injections.keySet());

		if (instantiatorDefinition instanceof KotlinDefaultConstructorInstantiatorDefinition) {
			KotlinDefaultConstructorInstantiatorDefinition kid = (KotlinDefaultConstructorInstantiatorDefinition) instantiatorDefinition;
			injections =  new HashMap<Parameter, BiFunction<? super S1, ? super S2, ?>>(injections);
			kid.addDefaultValueFlagBi(injections);
			instantiatorDefinition = kid.getDefaultValueConstructor();
		}
		
		if (asmFactory != null  && useAsmIfEnabled) {
			ClassLoader targetClassLoader = BiInstantiatorKey.getDeclaringClass(instantiatorDefinition).getClassLoader();
			AsmFactory asmFactory = this.asmFactory.getAsmFactory(targetClassLoader);
			if (asmFactory != null) {
				if (instantiatorDefinition instanceof ExecutableInstantiatorDefinition) {
					ExecutableInstantiatorDefinition executableInstantiatorDefinition = (ExecutableInstantiatorDefinition) instantiatorDefinition;
					Member executable = executableInstantiatorDefinition.getExecutable();
					if (Modifier.isPublic(executable.getModifiers())) {
						try {
							return asmFactory.createBiInstantiator(s1, s2, executableInstantiatorDefinition, injections, builderIgnoresNullValues);
						} catch (Exception e) {
							// fall back on reflection
							if (failOnAsmError) ErrorHelper.rethrow(e);
						}
					}
				} else {
					try {
						return asmFactory.createBiInstantiator(s1, s2, (BuilderInstantiatorDefinition) instantiatorDefinition, injections, builderIgnoresNullValues);
					} catch (Exception e) {
						// fall back on reflection
						if (failOnAsmError) ErrorHelper.rethrow(e);
					}
				}
			}
		}

		switch (instantiatorDefinition.getType()) {
			case CONSTRUCTOR:
				return constructorBiInstantiator((ExecutableInstantiatorDefinition)instantiatorDefinition, injections);
			case METHOD:
				return methodBiInstantiator((ExecutableInstantiatorDefinition)instantiatorDefinition, injections);
			case BUILDER:
				return builderBiInstantiator((BuilderInstantiatorDefinition)instantiatorDefinition, injections, useAsmIfEnabled, builderIgnoresNullValues);
			default:
				throw new IllegalArgumentException("Unsupported executable type " + instantiatorDefinition);
		}
	}


	@SuppressWarnings("unchecked")
	public <S, T> Instantiator<S, T> getInstantiator(Type target, final Class<S> source, List<InstantiatorDefinition> constructors, Map<Parameter, Getter<? super S, ?>> injections, boolean useAsmIfEnabled, boolean builderIgnoresNullValues) throws SecurityException {
		final InstantiatorDefinition instantiatorDefinition = getSmallerConstructor(constructors, injections.keySet());

		if (instantiatorDefinition == null) {
			throw new IllegalArgumentException("No constructor available for " + target);
		}
		return getInstantiator(instantiatorDefinition, source, injections, useAsmIfEnabled, builderIgnoresNullValues);


	}

	@SuppressWarnings("unchecked")
	public <S, T> Instantiator<S, T> getInstantiator(InstantiatorDefinition instantiatorDefinition, Class<S> source, Map<Parameter, Getter<? super S, ?>> injections, boolean useAsmIfEnabled, boolean builderIgnoresNullValues) {
		for(Parameter p : injections.keySet()) {
			if (!instantiatorDefinition.hasParam(p)) {
				throw new IllegalArgumentException("Could not find " + p + " in " + instantiatorDefinition + " raise issue");
			}
		}
		
		if (instantiatorDefinition instanceof KotlinDefaultConstructorInstantiatorDefinition) {
			KotlinDefaultConstructorInstantiatorDefinition kid = (KotlinDefaultConstructorInstantiatorDefinition) instantiatorDefinition;
			injections = new HashMap<Parameter, Getter<? super S, ?>>(injections);
			kid.addDefaultValueFlag(injections);
			instantiatorDefinition = kid.getDefaultValueConstructor();
		}
		
		if (asmFactory != null  && useAsmIfEnabled) {
			ClassLoader targetClassLoader = InstantiatorKey.getDeclaringClass(instantiatorDefinition).getClassLoader();
			AsmFactory asmFactory = this.asmFactory.getAsmFactory(targetClassLoader);
			if (asmFactory != null) {
				if (instantiatorDefinition instanceof ExecutableInstantiatorDefinition) {
					ExecutableInstantiatorDefinition executableInstantiatorDefinition = (ExecutableInstantiatorDefinition) instantiatorDefinition;
					Member executable = executableInstantiatorDefinition.getExecutable();
					if (Modifier.isPublic(executable.getModifiers())) {
						try {
							return asmFactory.createInstantiator(source, executableInstantiatorDefinition, injections, builderIgnoresNullValues);
						} catch (Exception e) {
							e.printStackTrace();
							// fall back on reflection
							if (failOnAsmError) ErrorHelper.rethrow(e);
						}
					}
				} else {
					try {
						return asmFactory.createInstantiator(source, (BuilderInstantiatorDefinition) instantiatorDefinition, injections, builderIgnoresNullValues);
					} catch (Exception e) {
						// fall back on reflection
						if (failOnAsmError) ErrorHelper.rethrow(e);
					}
				}
			}
		}

		switch (instantiatorDefinition.getType()) {
			case CONSTRUCTOR:
				return constructorInstantiator((ExecutableInstantiatorDefinition)instantiatorDefinition, injections);
			case METHOD:
				return methodInstantiator((ExecutableInstantiatorDefinition)instantiatorDefinition, injections);
			case BUILDER:
				return builderInstantiator((BuilderInstantiatorDefinition)instantiatorDefinition, injections, useAsmIfEnabled, builderIgnoresNullValues);
			default:
				throw new IllegalArgumentException("Unsupported executable type " + instantiatorDefinition);
		}
	}

	private static boolean checkParameters(InstantiatorDefinition instantiatorDefinition, Set<Parameter> parameters) {
		for(Parameter p : parameters) {
			if (!instantiatorDefinition.hasParam(p)) {
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings({"unchecked", "SuspiciousToArrayCall"})
	private <S, T> Instantiator<S, T> builderInstantiator(BuilderInstantiatorDefinition instantiatorDefinition,
														  Map<Parameter, Getter<? super S, ?>> injections, boolean useAsmIfEnabled, boolean builderIgnoresNullValues) {

		final Instantiator<Void, ?> buildInstantiator =
				getInstantiator(instantiatorDefinition.getBuilderInstantiator(), Void.class,
				 new HashMap<org.simpleflatmapper.reflect.Parameter, Getter<? super Void, ?>>(), useAsmIfEnabled, builderIgnoresNullValues);
		List<MethodGetterPair<S>> chainedArguments = new ArrayList<MethodGetterPair<S>>();
		List<MethodGetterPair<S>> unchainedArguments = new ArrayList<MethodGetterPair<S>>();

		for(Map.Entry<org.simpleflatmapper.reflect.Parameter, Getter<? super S, ?>> e : injections.entrySet()) {
			final MethodGetterPair<S> arguments =
				new MethodGetterPair<S>(instantiatorDefinition.getSetters().get(e.getKey()), e.getValue());
			if (Void.TYPE.equals(arguments.getMethod().getReturnType())) {
				unchainedArguments.add(arguments);
			} else {
				chainedArguments.add(arguments);
			}
		}

		return new BuilderInstantiator<S, T>(buildInstantiator,
				chainedArguments.toArray(new MethodGetterPair[0]),
				unchainedArguments.toArray(new MethodGetterPair[0]),
				instantiatorDefinition.getBuildMethod(), builderIgnoresNullValues);
	}

	private <S, T> Instantiator<S, T> methodInstantiator(
			ExecutableInstantiatorDefinition instantiatorDefinition,
			Map<org.simpleflatmapper.reflect.Parameter, Getter<? super S, ?>> injections) {
		Method m = (Method) instantiatorDefinition.getExecutable();
		if (m.getParameterTypes().length == 0) {
			return new EmptyStaticMethodInstantiator<S, T>(m);
		} else {
			return new InjectStaticMethodInstantiator<S, T>(instantiatorDefinition, injections);
		}
	}


	@SuppressWarnings("unchecked")
	private <S, T> Instantiator<S, T> constructorInstantiator(
			ExecutableInstantiatorDefinition instantiatorDefinition,
			Map<org.simpleflatmapper.reflect.Parameter, Getter<? super S, ?>> injections) {
		Constructor<? extends T> c = (Constructor<? extends T>) instantiatorDefinition.getExecutable();
		if (c.getParameterTypes().length == 0) {
			return new EmptyConstructorInstantiator<S, T>(c);
		} else {
			return new InjectConstructorInstantiator<S, T>(instantiatorDefinition, injections);
		}
	}


	@SuppressWarnings({"unchecked", "SuspiciousToArrayCall"})
	public <S1, S2, T> BuilderBiInstantiator<S1, S2, T>  builderBiInstantiator(BuilderInstantiatorDefinition instantiatorDefinition,
																		 Map<Parameter, BiFunction<? super S1, ? super S2, ?>> injections, boolean useAsmIfEnabled, boolean builderIgnoresNullValues) {

		final Instantiator<Void, ?> buildInstantiator =
				getInstantiator(instantiatorDefinition.getBuilderInstantiator(), Void.class,
						new HashMap<org.simpleflatmapper.reflect.Parameter, Getter<? super Void, ?>>(), useAsmIfEnabled, builderIgnoresNullValues);
		List<MethodBiFunctionPair<S1, S2>> chainedArguments = new ArrayList<MethodBiFunctionPair<S1, S2>>();
		List<MethodBiFunctionPair<S1, S2>> unchainedArguments = new ArrayList<MethodBiFunctionPair<S1, S2>>();

		int i = 0;
		for(Map.Entry<org.simpleflatmapper.reflect.Parameter, BiFunction<? super S1, ? super S2, ?>> e : injections.entrySet()) {
			final MethodBiFunctionPair<S1, S2> arguments =
					new MethodBiFunctionPair<S1, S2>(instantiatorDefinition.getSetters().get(e.getKey()), e.getValue());
			if (Void.TYPE.equals(arguments.getMethod().getReturnType())) {
				unchainedArguments.add(arguments);
			} else {
				chainedArguments.add(arguments);
			}
		}

		return new BuilderBiInstantiator<S1, S2, T>(buildInstantiator,
				chainedArguments.toArray(new MethodBiFunctionPair[0]),
				unchainedArguments.toArray(new MethodBiFunctionPair[0]),
				instantiatorDefinition.getBuildMethod(), builderIgnoresNullValues);
	}

	private <S1, S2, T> BiInstantiator<S1, S2, T>  methodBiInstantiator(
			ExecutableInstantiatorDefinition instantiatorDefinition,
			Map<org.simpleflatmapper.reflect.Parameter, BiFunction<? super S1, ? super S2, ?>> injections) {
		Method m = (Method) instantiatorDefinition.getExecutable();
		if (m.getParameterTypes().length == 0) {
			return new EmptyStaticMethodBiInstantiator<S1, S2, T>(m);
		} else {
			return new InjectStaticMethodBiInstantiator<S1, S2, T>(instantiatorDefinition, injections);
		}
	}


	@SuppressWarnings("unchecked")
	private <S1, S2, T> BiInstantiator<S1, S2, T>  constructorBiInstantiator(
			ExecutableInstantiatorDefinition instantiatorDefinition,
			Map<org.simpleflatmapper.reflect.Parameter, BiFunction<? super S1, ? super S2, ?>> injections) {
		Constructor<? extends T> c = (Constructor<? extends T>) instantiatorDefinition.getExecutable();
		if (c.getParameterTypes().length == 0) {
			return new EmptyConstructorBiInstantiator<S1, S2, T>(c);
		} else {
			return new InjectConstructorBiInstantiator<S1, S2, T>(instantiatorDefinition, injections);
		}
	}

	public static InstantiatorDefinition getSmallerConstructor(final List<InstantiatorDefinition> constructors, Set<Parameter> parameters) {
        if (constructors == null) {
            return null;
        }

		InstantiatorDefinition selectedConstructor = null;
		
		for(InstantiatorDefinition c : constructors) {
			if (checkParameters(c, parameters) && (selectedConstructor == null || InstantiatorDefinitions.COMPARATOR.compare(c, selectedConstructor) < 0)) {
				selectedConstructor = c;
			}
		}
		
		return selectedConstructor;
	}

	public <S, T> Instantiator<S, T> getArrayInstantiator(final Class<?> elementType, final int length) {
		return new ArrayInstantiator<S, T>(elementType, length);
	}

	public <S1, S2, T> BiInstantiator<S1, S2, T> getArrayBiInstantiator(final Class<?> elementType, final int length) {
		return new ArrayBiInstantiator<S1, S2, T>(elementType, length);
	}

	@SuppressWarnings("unchecked")
	public <S, T> Instantiator<S, T> getOneArgIdentityInstantiator(InstantiatorDefinition id, boolean builderIgnoresNullValues) {
		if (id.getParameters().length != 1) {
			throw new IllegalArgumentException("Definition does not have one param " + Arrays.asList(id.getParameters()));
		}
		Map<org.simpleflatmapper.reflect.Parameter, Getter<? super S, ?>> injections = new HashMap<org.simpleflatmapper.reflect.Parameter, Getter<? super S, ?>>();
		injections.put(id.getParameters()[0], new IdentityGetter<S>());
		return getInstantiator(id, (Class<S>) id.getParameters()[0].getType(), injections, true, builderIgnoresNullValues);
	}


	private static final class ArrayInstantiator<S, T> implements Instantiator<S, T> {
		private final Class<?> elementType;
		private final int length;

		public ArrayInstantiator(Class<?> elementType, int length) {
			this.elementType = elementType;
			this.length = length;
		}

		@SuppressWarnings("unchecked")
        @Override
        public T newInstance(S s) throws Exception {
            return (T) Array.newInstance(elementType, length);
        }
	}

	private static final class ArrayBiInstantiator<S1, S2, T> implements BiInstantiator<S1, S2, T> {
		private final Class<?> elementType;
		private final int length;

		public ArrayBiInstantiator(Class<?> elementType, int length) {
			this.elementType = elementType;
			this.length = length;
		}

		@SuppressWarnings("unchecked")
		@Override
		public T newInstance(S1 s1, S2 s2) throws Exception {
			return (T) Array.newInstance(elementType, length);
		}
	}
}
