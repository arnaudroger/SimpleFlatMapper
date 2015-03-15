package org.sfm.reflect.meta;

import org.sfm.map.MapperBuildingException;
import org.sfm.reflect.ConstructorDefinition;
import org.sfm.reflect.ConstructorParameter;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.TypeHelper;
import org.sfm.tuples.Tuples;

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
	private final ConstructorDefinition<T> constructorDefinition;

	public TupleClassMeta(Type type, ReflectionService reflectionService) {
		this.type = type;
		this.reflectionService = reflectionService;

		try {
            this.constructorDefinition = getConstructorDefinition(type, reflectionService);
        } catch(RuntimeException e) {
            throw e;
		} catch(Exception e) {
			throw new MapperBuildingException(e.getMessage(), e);
		}
	}

    private ConstructorDefinition<T> getConstructorDefinition(Type type, ReflectionService reflectionService) throws java.io.IOException {
        final List<ConstructorDefinition<T>> definitions = reflectionService.extractConstructors(type);

        ListIterator<ConstructorDefinition<T>> iterator = definitions.listIterator();
        while(iterator.hasNext()) {
            final ConstructorDefinition<T> definition = iterator.next();
            if (isTupleConstructor(type, definition)) {
                return respecifyParameterNames(definition);
            }
        }
        throw new MapperBuildingException("Cannot find eligible constructor definition for " + type);
    }

    private ConstructorDefinition<T> respecifyParameterNames(ConstructorDefinition<T> definition) {
        final ConstructorParameter[] parameters = definition.getParameters();
        if (parameters.length > 0 && parameters[0].getName().equals("arg0")) {

            ConstructorParameter[] newParams = new ConstructorParameter[parameters.length];
            final ElementNameGenerator nameGenerator = elementNameGenerator(definition.getConstructor().getDeclaringClass());

            for(int i = 0; i < parameters.length; i++) {
                newParams[i] = new ConstructorParameter(nameGenerator.name(i), parameters[i].getType(), parameters[i].getResolvedType());
            }

            return new ConstructorDefinition<T>(definition.getConstructor(), newParams);

        }
        return definition;
    }

    private boolean isTupleConstructor(Type type, ConstructorDefinition<T> definition) {
        if (type instanceof ParameterizedType) {
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
		for(ConstructorParameter cp : constructorDefinition.getParameters()) {
            String prefix = nameGenerator.name(i);
			ClassMeta<?> classMeta = reflectionService.getClassMeta(cp.getResolvedType(), false);

			if (classMeta != null) {
				for(String prop : classMeta.generateHeaders()) {
					strings.add(prefix + "_" + prop);
				}
			} else {
				strings.add(prefix);
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


    public List<ConstructorDefinition<T>> getConstructorDefinitions() {
		return Arrays.asList(constructorDefinition);
	}

	public int getTupleSize() {
		return constructorDefinition.getParameters().length;
	}


    static interface ElementNameGenerator {
        public String name(int i);
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
