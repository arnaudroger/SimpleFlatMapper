package org.sfm.reflect.meta;

import org.sfm.map.MapperBuildingException;
import org.sfm.reflect.ConstructorDefinition;
import org.sfm.reflect.ConstructorParameter;
import org.sfm.reflect.ReflectionService;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class TupleClassMeta<T> implements ClassMeta<T> {

	public static final String[] EMPTY_STRING_ARRAY = new String[0];
	private final ReflectionService reflectionService;
	private final Type type;
	private final List<ConstructorDefinition<T>> constructorDefinitions;

	public TupleClassMeta(Type type, ReflectionService reflectionService) {
		this.type = type;
		this.reflectionService = reflectionService;

		try {
			this.constructorDefinitions = getConstructorDefinitions(type, reflectionService);

            if (constructorDefinitions.size() != 1) {
                throw new IllegalStateException("More than one eligible constructor");
            }
		} catch(Exception e) {
			throw new MapperBuildingException(e.getMessage(), e);
		}
	}

    private List<ConstructorDefinition<T>> getConstructorDefinitions(Type type, ReflectionService reflectionService) throws java.io.IOException {
        final List<ConstructorDefinition<T>> definitions = reflectionService.extractConstructors(type);

        if (definitions.size() == 1) return definitions;

        ListIterator<ConstructorDefinition<T>> iterator = definitions.listIterator();
        while(iterator.hasNext()) {
            final ConstructorDefinition<T> definition = iterator.next();
            if (!isTupleConstructor(type, definition)) {
                iterator.remove();
            }
        }

        return definitions;
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

		int i = 0;
		for(ConstructorParameter cp : constructorDefinitions.get(0).getParameters()) {
			String prefix = "element" + i;

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


	public List<ConstructorDefinition<T>> getConstructorDefinitions() {
		return constructorDefinitions;
	}

	public int getTupleSize() {
		return constructorDefinitions.get(0).getParameters().length;
	}
}
