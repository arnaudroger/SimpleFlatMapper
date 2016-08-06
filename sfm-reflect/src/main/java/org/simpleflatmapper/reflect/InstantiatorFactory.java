package org.simpleflatmapper.reflect;

import org.simpleflatmapper.reflect.asm.AsmFactory;
import org.simpleflatmapper.reflect.impl.BuilderInstantiator;
import org.simpleflatmapper.reflect.impl.EmptyConstructorInstantiator;
import org.simpleflatmapper.reflect.impl.EmptyStaticMethodInstantiator;
import org.simpleflatmapper.reflect.impl.InjectConstructorInstantiator;
import org.simpleflatmapper.reflect.impl.InjectStaticMethodInstantiator;
import org.simpleflatmapper.util.ErrorHelper;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstantiatorFactory {
	private final AsmFactory asmFactory;

	private final boolean failOnAsmError;
	
	public InstantiatorFactory(final AsmFactory asmFactory) {
		this(asmFactory, false);
	}

	public InstantiatorFactory(AsmFactory asmFactory, boolean faileOnAsmError) {
		this.asmFactory = asmFactory;
		this.failOnAsmError = faileOnAsmError;
	}

	@SuppressWarnings("unchecked")
	public <S, T> Instantiator<S, T> getInstantiator(Type target, final Class<?> source, List<InstantiatorDefinition> constructors, Map<Parameter, Getter<? super S, ?>> injections, boolean useAsmIfEnabled) throws SecurityException {
		final InstantiatorDefinition instantiatorDefinition = getSmallerConstructor(constructors);

		if (instantiatorDefinition == null) {
			throw new IllegalArgumentException("No constructor available for " + target);
		}
		return getInstantiator(instantiatorDefinition, source, injections, useAsmIfEnabled);


	}

	@SuppressWarnings("unchecked")
	public <S, T> Instantiator<S, T> getInstantiator(InstantiatorDefinition instantiatorDefinition, Class<?> source, Map<Parameter, Getter<? super S, ?>> injections, boolean useAsmIfEnabled) {
		if (asmFactory != null  && useAsmIfEnabled) {
			if (instantiatorDefinition instanceof ExecutableInstantiatorDefinition) {
				ExecutableInstantiatorDefinition executableInstantiatorDefinition = (ExecutableInstantiatorDefinition) instantiatorDefinition;
				Member executable = executableInstantiatorDefinition.getExecutable();
				if (Modifier.isPublic(executable.getModifiers())) {
					try {
						return asmFactory.createInstantiator(source, executableInstantiatorDefinition, injections);
					} catch (Exception e) {
						// fall back on reflection
						if (failOnAsmError) ErrorHelper.rethrow(e);
					}
				}
			} else {
				try {
					return asmFactory.createInstantiator(source, (BuilderInstantiatorDefinition)instantiatorDefinition, injections);
				} catch (Exception e) {
					// fall back on reflection
					if (failOnAsmError) ErrorHelper.rethrow(e);
				}
			}
		}

		switch (instantiatorDefinition.getType()) {
			case CONSTRUCTOR:
				return constructorInstantiator((ExecutableInstantiatorDefinition)instantiatorDefinition, injections);
			case METHOD:
				return methodInstantiator((ExecutableInstantiatorDefinition)instantiatorDefinition, injections);
			case BUILDER:
				return builderInstantiator((BuilderInstantiatorDefinition)instantiatorDefinition, injections, useAsmIfEnabled);
			default:
				throw new IllegalArgumentException("Unsupported executable type " + instantiatorDefinition);
		}
	}

	@SuppressWarnings({"unchecked", "SuspiciousToArrayCall"})
	private <S, T> Instantiator<S, T> builderInstantiator(BuilderInstantiatorDefinition instantiatorDefinition,
														  Map<Parameter, Getter<? super S, ?>> injections, boolean useAsmIfEnabled) {

		final Instantiator<Void, ?> buildInstantiator =
				getInstantiator(instantiatorDefinition.getBuilderInstantiator(), Void.class,
				 new HashMap<Parameter, Getter<? super Void, ?>>(), useAsmIfEnabled);
		List<MethodGetterPair<S>> chainedArguments = new ArrayList<MethodGetterPair<S>>();
		List<MethodGetterPair<S>> unchainedArguments = new ArrayList<MethodGetterPair<S>>();

		int i = 0;
		for(Map.Entry<Parameter, Getter<? super S, ?>> e : injections.entrySet()) {
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
				instantiatorDefinition.getBuildMethod());
	}

	private <S, T> Instantiator<S, T> methodInstantiator(
			ExecutableInstantiatorDefinition instantiatorDefinition,
			Map<Parameter, Getter<? super S, ?>> injections) {
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
			Map<Parameter, Getter<? super S, ?>> injections) {
		Constructor<? extends T> c = (Constructor<? extends T>) instantiatorDefinition.getExecutable();
		if (c.getParameterTypes().length == 0) {
			return new EmptyConstructorInstantiator<S, T>(c);
		} else {
			return new InjectConstructorInstantiator<S, T>(instantiatorDefinition, injections);
		}
	}

	public static InstantiatorDefinition getSmallerConstructor(final List<InstantiatorDefinition> constructors) {
        if (constructors == null) {
            return null;
        }

		InstantiatorDefinition selectedConstructor = null;
		
		for(InstantiatorDefinition c : constructors) {
			if (selectedConstructor == null || InstantiatorDefinitions.COMPARATOR.compare(c, selectedConstructor) < 0) {
				selectedConstructor = c;
			}
		}
		
		return selectedConstructor;
	}

	public <S, T> Instantiator<S, T> getArrayInstantiator(final Class<?> elementType, final int length) {
		return new ArrayInstantiator<S, T>(elementType, length);
	}

	@SuppressWarnings("unchecked")
	public <S, T> Instantiator<S, T> getOneArgIdentityInstantiator(InstantiatorDefinition id) {
		Map<Parameter, Getter<? super S, ?>> injections = new HashMap<Parameter, Getter<? super S, ?>>();
		injections.put(id.getParameters()[0], new IdentityGetter());
		return getInstantiator(id, id.getParameters()[0].getType(), injections, true);
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
}
