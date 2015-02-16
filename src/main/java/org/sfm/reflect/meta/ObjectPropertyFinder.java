package org.sfm.reflect.meta;

import org.sfm.map.MapperBuildingException;
import org.sfm.reflect.ConstructorDefinition;
import org.sfm.reflect.ConstructorParameter;
import org.sfm.utils.Predicate;

import java.util.*;

final class ObjectPropertyFinder<T> implements PropertyFinder<T> {
	
	private final List<ConstructorDefinition<T>> eligibleConstructorDefinitions;
	private final ObjectClassMeta<T> classMeta;
	private final Map<String, PropertyFinder<?>> subPropertyFinders = new HashMap<String, PropertyFinder<?>>();
    private final Predicate<PropertyMeta<?, ?>> isJoinProperty;

    ObjectPropertyFinder(ObjectClassMeta<T> classMeta, Predicate<PropertyMeta<?, ?>> isJoinProperty) throws MapperBuildingException {
		this.classMeta = classMeta;
        this.isJoinProperty = isJoinProperty;
		this.eligibleConstructorDefinitions = classMeta.getConstructorDefinitions() != null ? new ArrayList<ConstructorDefinition<T>>(classMeta.getConstructorDefinitions()) : null;
	}

	/* (non-Javadoc)
	 * @see org.sfm.reflect.PropertyFinder#findProperty(org.sfm.utils.PropertyNameMatcher)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PropertyMeta<T, ?> findProperty(final PropertyNameMatcher propertyNameMatcher) {
		// check for constructor
		PropertyMeta<T, ?> prop = lookForConstructor(propertyNameMatcher);

		if (prop == null) {
			prop = lookForProperty(propertyNameMatcher);
			
			if (prop == null) {
				prop = lookForSubPropertyInConstructors(propertyNameMatcher);
				
				if (prop == null) {
					prop = lookForSubProperty(propertyNameMatcher);
					
				} else {
					ConstructorPropertyMeta<T, ?> constructorProperty = (ConstructorPropertyMeta<T, ?>) ((SubPropertyMeta<T, ?>)prop).getOwnerProperty();
					removeNonMatching(constructorProperty.getConstructorParameter());
				}
			}
			
		} else {
			ConstructorPropertyMeta<T, ?> constructorProperty = (ConstructorPropertyMeta<T, ?>) prop;
			removeNonMatching(constructorProperty.getConstructorParameter());
		}

		return prop;
	}
	
	private ConstructorPropertyMeta<T, ?> lookForConstructor(final PropertyNameMatcher propertyNameMatcher) {
		if (classMeta.getConstructorProperties() != null) {
			for (ConstructorPropertyMeta<T, ?> prop : classMeta.getConstructorProperties()) {
				if (propertyNameMatcher.matches(prop.getColumn())
						&& hasConstructorMatching(prop.getConstructorParameter())) {
					return prop;
				}
			}
		}
		
		return null;
	}

	private PropertyMeta<T, ?> lookForProperty(final PropertyNameMatcher propertyNameMatcher) {
		for (PropertyMeta<T, ?> prop : classMeta.getProperties()) {
			if (propertyNameMatcher.matches(prop.getColumn())) {
				return prop;
			}
		}
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private PropertyMeta<T, ?> lookForSubPropertyInConstructors(final PropertyNameMatcher propertyNameMatcher) {
		if (classMeta.getConstructorProperties() != null) {
			for (ConstructorPropertyMeta<T, ?> prop : classMeta.getConstructorProperties()) {
				PropertyNameMatcher subPropMatcher = propertyNameMatcher.partialMatch(prop.getColumn());
				if (subPropMatcher != null && hasConstructorMatching(prop.getConstructorParameter())) {
					PropertyMeta<?, ?> subProp = lookForSubProperty(subPropMatcher, prop);
					if (subProp != null) {
						return new SubPropertyMeta(classMeta.getReflectionService(), prop, subProp);
					}
				}
			}
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private PropertyMeta<T, ?> lookForSubProperty(final PropertyNameMatcher propertyNameMatcher) {
		for (PropertyMeta<T, ?> prop : classMeta.getProperties()) {
			PropertyNameMatcher subPropMatcher = propertyNameMatcher.partialMatch(prop.getColumn());
			if (subPropMatcher != null) {
				PropertyMeta<?, ?> subProp =  lookForSubProperty(subPropMatcher, prop);
				if (subProp != null) {
					return new SubPropertyMeta(classMeta.getReflectionService(), prop, subProp);
				}
			}
		}

		return null;
	}
	
	private PropertyMeta<?, ?> lookForSubProperty(
			final PropertyNameMatcher propertyNameMatcher,
			final PropertyMeta<T, ?> prop) {
		PropertyFinder<?> subPropertyFinder = subPropertyFinders.get(prop.getColumn());
		if (subPropertyFinder == null) {
			subPropertyFinder = prop.getClassMeta().newPropertyFinder(isJoinProperty);
			subPropertyFinders.put(prop.getName(), subPropertyFinder);
		}

		return subPropertyFinder.findProperty(propertyNameMatcher);
	}

	private void removeNonMatching(ConstructorParameter param) {
		ListIterator<ConstructorDefinition<T>> li = eligibleConstructorDefinitions.listIterator();
		while(li.hasNext()){
			ConstructorDefinition<T> cd = li.next();
			if (!cd.hasParam(param)) {
				li.remove();
			}
		}
	}
	
	private boolean hasConstructorMatching(ConstructorParameter param) {
		for(ConstructorDefinition<T> cd : eligibleConstructorDefinitions) {
			if (cd.hasParam(param)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<ConstructorDefinition<T>> getEligibleConstructorDefinitions() {
		return eligibleConstructorDefinitions;
	}

}
