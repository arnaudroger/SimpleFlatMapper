package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.ConstructorNotFoundException;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.ScoredGetter;
import org.simpleflatmapper.reflect.ScoredSetter;
import org.simpleflatmapper.reflect.instantiator.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.TupleHelper;
import org.simpleflatmapper.util.TypeHelper;
import org.simpleflatmapper.util.ErrorHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class TupleClassMeta<T> implements ClassMeta<T> {

	private final ReflectionService reflectionService;
	private final Type type;
	private final InstantiatorDefinition instantiatorDefinition;
    private final List<ConstructorPropertyMeta<T, ?>> propertyMetas;

	public TupleClassMeta(Type type, ReflectionService reflectionService) {
		this.type = type;
		this.reflectionService = reflectionService;

		try {
            this.instantiatorDefinition = getInstantiatorDefinition(type, reflectionService);
            this.propertyMetas = getPropertyMetas(instantiatorDefinition, reflectionService, type);
		} catch(Exception e) {
            ErrorHelper.rethrow(e);
            throw new IllegalStateException();
		}
	}

    public TupleClassMeta(ReflectionService reflectionService, Type type, InstantiatorDefinition instantiatorDefinition, List<ConstructorPropertyMeta<T, ?>> propertyMetas) {
        this.reflectionService = reflectionService;
        this.type = type;
        this.instantiatorDefinition = instantiatorDefinition;
        this.propertyMetas = propertyMetas;
    }

    @Override
    public ClassMeta<T> withReflectionService(ReflectionService reflectionService) {
        return new TupleClassMeta<T>(reflectionService, type, instantiatorDefinition, ObjectClassMeta.withReflectionServiceConstructor(propertyMetas, reflectionService));
    }

    private static <T> List<ConstructorPropertyMeta<T, ?>> getPropertyMetas(InstantiatorDefinition instantiatorDefinition, ReflectionService reflectionService, Type type) {
        int size = instantiatorDefinition.getParameters().length;
        List<ConstructorPropertyMeta<T, ?>> propertyMetas = new ArrayList<ConstructorPropertyMeta<T, ?>>();

        for(int i = 0; i < size; i++) {
            propertyMetas.add(TupleClassMeta.<T, Object>newConstructorPropertyMeta(instantiatorDefinition, i, reflectionService, type));
        }
        return propertyMetas;
    }

    private static <T, E> ConstructorPropertyMeta<T, E> newConstructorPropertyMeta(InstantiatorDefinition instantiatorDefinition, int i, ReflectionService reflectionService, Type type) {
        Class<T> tClass = TypeHelper.toClass(type);

        final Parameter parameter = instantiatorDefinition.getParameters()[i];

        Getter<T, E> getter = reflectionService.getObjectGetterFactory().getGetter(tClass, parameter.getName());
        return new ConstructorPropertyMeta<T, E>("element" + i, type, reflectionService,
                parameter,
                ScoredGetter.<T, E>of(getter, Integer.MAX_VALUE), ScoredSetter.<T, E>nullSetter(), instantiatorDefinition, null);
    }

    private InstantiatorDefinition getInstantiatorDefinition(Type type, ReflectionService reflectionService) throws java.io.IOException {
        final List<InstantiatorDefinition> definitions = reflectionService.extractInstantiator(type);

        ListIterator<InstantiatorDefinition> iterator = definitions.listIterator();
        while(iterator.hasNext()) {
            final InstantiatorDefinition definition = iterator.next();
            if (isTupleConstructor(type, definition)) {
                return respecifyParameterNames((ExecutableInstantiatorDefinition)definition);
            }
        }
        throw new ConstructorNotFoundException("Cannot find eligible tuple constructor definition for " + type);
    }

    @SuppressWarnings("unchecked")
    private InstantiatorDefinition respecifyParameterNames(ExecutableInstantiatorDefinition definition) {
        final Parameter[] parameters = definition.getParameters();
        if (parameters.length > 0 && parameters[0].getName() == null) {

            Parameter[] newParams = new Parameter[parameters.length];
            final ElementNameGenerator nameGenerator = elementNameGenerator(definition.getExecutable().getDeclaringClass());

            for(int i = 0; i < parameters.length; i++) {
                newParams[i] = new Parameter(i, nameGenerator.name(i), parameters[i].getType(), parameters[i].getGenericType());
            }

            return new ExecutableInstantiatorDefinition((Constructor<? extends T>) definition.getExecutable(), newParams);

        }
        return definition;
    }

    private boolean isTupleConstructor(Type type, InstantiatorDefinition definition) {
        if (type instanceof ParameterizedType && definition.getType() != InstantiatorDefinition.Type.BUILDER) {
            ParameterizedType pt = (ParameterizedType) type;
            return pt.getActualTypeArguments().length == definition.getParameters().length;
        }
        return true;
    }

    @Override
	public ReflectionService getReflectionService() {
		return reflectionService;
	}

	@Override
	public PropertyFinder<T> newPropertyFinder() {
		return new TuplePropertyFinder<T>(this, reflectionService.selfScoreFullName());
	}

	public Type getType() {
		return type;
	}

    private static ElementNameGenerator elementNameGenerator(Type type) {
        Class<?> clazz = TypeHelper.toClass(type);

        if (TupleHelper.isJoolTuple(clazz)) {
            return new JoolTupleNameGenerator();
        }

        return new SFMTupleNameGenerator();
    }


    public List<InstantiatorDefinition> getInstantiatorDefinitions() {
		return Arrays.asList(instantiatorDefinition);
	}

    @Override
    public void forEachProperties(Consumer<? super PropertyMeta<T, ?>> consumer) {
        for(PropertyMeta<T, ?> prop : propertyMetas) {
            consumer.accept(prop);
        }
    }

    @Override
    public int getNumberOfProperties() {
        return propertyMetas.size();
    }

    @Override
    public boolean needTransformer() {
        return false;
    }



    public int getTupleSize() {
		return instantiatorDefinition.getParameters().length;
	}


    interface ElementNameGenerator {
        String name(int i);
    }

    static class SFMTupleNameGenerator implements ElementNameGenerator {
        public String name(int i) {
            return "element" + i;
        }
    }
    static class JoolTupleNameGenerator implements ElementNameGenerator {
        public String name(int i) {
            return "v" + (i+1);
        }
    }

}
