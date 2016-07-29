package org.sfm.reflect.meta;

import org.sfm.map.MapperBuildingException;
import org.sfm.reflect.ExecutableInstantiatorDefinition;
import org.sfm.reflect.InstantiatorDefinition;
import org.sfm.reflect.Parameter;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.TypeHelper;
import org.sfm.tuples.Tuples;
import org.sfm.utils.ErrorHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class TupleClassMeta<T> implements ClassMeta<T> {

	public static final String[] EMPTY_STRING_ARRAY = new String[0];
	private final ReflectionService reflectionService;
	private final Type type;
	private final InstantiatorDefinition instantiatorDefinition;

	public TupleClassMeta(Type type, ReflectionService reflectionService) {
		this.type = type;
		this.reflectionService = reflectionService;

		try {
            this.instantiatorDefinition = getInstantiatorDefinition(type, reflectionService);
		} catch(Exception e) {
            ErrorHelper.rethrow(e);
            throw new IllegalStateException();
		}
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
        throw new MapperBuildingException("Cannot find eligible constructor definition for " + type);
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
		return new TuplePropertyFinder<T>(this);
	}

	public Type getType() {
		return type;
	}

	@Override
	public String[] generateHeaders() {
		List<String> strings = new ArrayList<String>();

        ElementNameGenerator nameGenerator = new SFMTupleNameGenerator();

		int i = 0;
		for(Parameter cp : instantiatorDefinition.getParameters()) {
            String prefix = nameGenerator.name(i);
			ClassMeta<?> classMeta = reflectionService.getClassMeta(cp.getGenericType());

            for(String prop : classMeta.generateHeaders()) {
                String name = prop.length() == 0 ? prefix : prefix + "_" + prop;
                strings.add(name);
            }

			i++;
		}

		return strings.toArray(EMPTY_STRING_ARRAY);
	}

    private static ElementNameGenerator elementNameGenerator(Type type) {
        Class<?> clazz = TypeHelper.toClass(type);

        if (Tuples.isJoolTuple(clazz)) {
            return new JoolTupleNameGenerator();
        }

        return new SFMTupleNameGenerator();
    }


    public List<InstantiatorDefinition> getInstantiatorDefinitions() {
		return Arrays.asList(instantiatorDefinition);
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

    @Override
    public boolean isLeaf() {
        return false;
    }
}
