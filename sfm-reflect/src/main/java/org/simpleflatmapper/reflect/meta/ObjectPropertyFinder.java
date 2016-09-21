package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.util.Consumer;

import java.util.*;

final class ObjectPropertyFinder<T> extends PropertyFinder<T> {
	
	private final List<InstantiatorDefinition> eligibleInstantiatorDefinitions;
	private final ObjectClassMeta<T> classMeta;
	private final Map<String, PropertyFinder<?>> subPropertyFinders = new HashMap<String, PropertyFinder<?>>();

    ObjectPropertyFinder(ObjectClassMeta<T> classMeta) {
		this.classMeta = classMeta;
		this.eligibleInstantiatorDefinitions = classMeta.getInstantiatorDefinitions() != null ? new ArrayList<InstantiatorDefinition>(classMeta.getInstantiatorDefinitions()) : null;
	}

	@Override
	protected void lookForProperties(PropertyNameMatcher propertyNameMatcher, PropertyFinder.MatchingProperties matchingProperties, PropertyMatchingScore score) {
		lookForConstructor(propertyNameMatcher, matchingProperties, score);
		lookForProperty(propertyNameMatcher, matchingProperties, score);
	}

	private void lookForConstructor(final PropertyNameMatcher propertyNameMatcher, PropertyFinder.MatchingProperties matchingProperties, PropertyMatchingScore score) {
		if (classMeta.getConstructorProperties() != null) {
			for (ConstructorPropertyMeta<T, ?> prop : classMeta.getConstructorProperties()) {
				String columnName = getColumnName(prop);
				if (propertyNameMatcher.matches(columnName)
						&& hasConstructorMatching(prop.getParameter())) {
					matchingProperties.found(prop, new Consumer<ConstructorPropertyMeta<T, ?>>() {
						@Override
						public void accept(ConstructorPropertyMeta<T, ?> o) {
							removeNonMatching(o.getParameter());
						}
					}, score);
				}

				PropertyNameMatcher subPropMatcher = propertyNameMatcher.partialMatch(columnName);
				if (subPropMatcher != null && hasConstructorMatching(prop.getParameter())) {
					PropertyMeta<?, ?> subProp = lookForSubProperty(subPropMatcher, prop);
					if (subProp != null) {
						matchingProperties.found(
								new SubPropertyMeta(classMeta.getReflectionService(), prop, subProp),
								new Consumer<Object>() {
									@Override
									public void accept(Object o) {
										removeNonMatching(prop.getParameter());
									}
								}, score.shift());
					}
				}
			}
		}
	}


	private void lookForProperty(final PropertyNameMatcher propertyNameMatcher, PropertyFinder.MatchingProperties matchingProperties, PropertyMatchingScore score) {
		for (PropertyMeta<T, ?> prop : classMeta.getProperties()) {
			String columnName = getColumnName(prop);
			if (propertyNameMatcher.matches(columnName)) {
				matchingProperties.found(prop, null, score.decrease(1));
			}
			PropertyNameMatcher subPropMatcher = propertyNameMatcher.partialMatch(columnName);
			if (subPropMatcher != null) {
				PropertyMeta<?, ?> subProp =  lookForSubProperty(subPropMatcher, prop);
				if (subProp != null) {
					matchingProperties.found(new SubPropertyMeta(classMeta.getReflectionService(), prop, subProp), null, score.shift().decrease(-1));
				}
			}
		}
	}

	private PropertyMeta<?, ?> lookForSubProperty(
			final PropertyNameMatcher propertyNameMatcher,
			final PropertyMeta<T, ?> prop) {
		PropertyFinder<?> subPropertyFinder = subPropertyFinders.get(getColumnName(prop));
		if (subPropertyFinder == null) {
			subPropertyFinder = prop.getPropertyClassMeta().newPropertyFinder();
			subPropertyFinders.put(prop.getName(), subPropertyFinder);
		}

		return subPropertyFinder.findProperty(propertyNameMatcher);
	}

    private String getColumnName(PropertyMeta<T, ?> prop) {
        return this.classMeta.getAlias(prop.getName());
    }


    private void removeNonMatching(Parameter param) {
		ListIterator<InstantiatorDefinition> li = eligibleInstantiatorDefinitions.listIterator();
		while(li.hasNext()){
			InstantiatorDefinition cd = li.next();
			if (!cd.hasParam(param)) {
				li.remove();
			}
		}
	}
	
	private boolean hasConstructorMatching(Parameter param) {
		for(InstantiatorDefinition cd : eligibleInstantiatorDefinitions) {
			if (cd.hasParam(param)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<InstantiatorDefinition> getEligibleInstantiatorDefinitions() {
		return eligibleInstantiatorDefinitions;
	}

	@Override
	public PropertyFinder<?> getSubPropertyFinder(String name) {
		return subPropertyFinders.get(name);
	}


}
