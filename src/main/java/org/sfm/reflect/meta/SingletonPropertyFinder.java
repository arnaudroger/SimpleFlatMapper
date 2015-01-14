package org.sfm.reflect.meta;

import org.sfm.reflect.ConstructorDefinition;
import org.sfm.reflect.ConstructorParameter;
import org.sfm.reflect.TypeHelper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class SingletonPropertyFinder<T> implements PropertyFinder<T> {

	private final ClassMeta<T> classMeta;
	private final PropertyFinder<T> propertyFinder;

	private final List<String> selectedParameters = new ArrayList<String>();

	public SingletonPropertyFinder(ClassMeta<T> classMeta) {
		this.classMeta = classMeta;
		this.propertyFinder = classMeta.newPropertyFinder();
	}


	@Override
	public <E> PropertyMeta<T, E> findProperty(PropertyNameMatcher propertyNameMatcher) {

		PropertyMeta<T, E> property = propertyFinder.findProperty(propertyNameMatcher);

		if (property == null && ! selectedParameters.isEmpty()) {
			ConstructorDefinition<T> constructorDefinition = getSmallestConstructorWithAtLeast(selectedParameters.size() + 1);

			if (constructorDefinition != null) {
				ConstructorParameter param = constructorDefinition.getParameters()[selectedParameters.size()];
				selectedParameters.add(param.getName());
				return propertyFinder.findProperty(new DefaultPropertyNameMatcher(param.getName()));
			}
		}
		return property;
	}

	private ConstructorDefinition<T> getSmallestConstructorWithAtLeast(int nb) {
		ConstructorDefinition<T> selected = null;

		for(ConstructorDefinition<T> def : propertyFinder.getEligibleConstructorDefinitions()) {
			if (def.getParameters().length == 1 && (selected == null || selected.getParameters().length > def.getParameters().length)) {
				selected = def;
			}
		}

		return selected;
	}


	@Override
	public List<ConstructorDefinition<T>> getEligibleConstructorDefinitions() {
		return propertyFinder.getEligibleConstructorDefinitions();
	}

	@Override
	public Class<?> getClassToInstantiate() {
		return TypeHelper.toClass(classMeta.getType());
	}

}
