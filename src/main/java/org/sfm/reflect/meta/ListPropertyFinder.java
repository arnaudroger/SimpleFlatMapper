package org.sfm.reflect.meta;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sfm.reflect.asm.ConstructorDefinition;
import org.sfm.reflect.asm.ConstructorParameter;
import org.sfm.utils.PropertyNameMatcher;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ListPropertyFinder<T> implements PropertyFinder<List<T>> {

	private final ListClassMeta<T> listClassMeta;
	private final Map<Integer, ElementPropertyMeta<T>> properties = new HashMap<>();
	private final Map<String, PropertyFinder<?>> subPropertyFinders = new HashMap<>();
	private static final List constructors;
	
	static {
		constructors = new ArrayList<>();
		try {
			constructors.add(new ConstructorDefinition(ArrayList.class.getConstructor(), new ConstructorParameter[] {}));
		} catch(Exception e) {
			throw new Error(e.getMessage(), e);
		}
	}

	
	public ListPropertyFinder(ListClassMeta<T> listClassMeta) {
		this.listClassMeta = listClassMeta;
	}

	@Override
	public PropertyMeta<List<T>, ?> findProperty(PropertyNameMatcher propertyNameMatcher) {
		
		String propertyName = propertyNameMatcher.getPropertyName();
		
		String prefix = listClassMeta.getPrefix();
		
		int listIndexStart = prefix != null ? prefix.length() : 0;
		int listIndexEnd = listIndexStart;
		while(listIndexEnd < propertyName.length() &&  Character.isDigit(propertyName.charAt(listIndexEnd))) {
			listIndexEnd++;
		}
		if (listIndexStart == listIndexEnd) {
			return null;
		}
		
		int index = Integer.parseInt(propertyName.substring(listIndexStart, listIndexEnd));

		ElementPropertyMeta<T> prop = properties.get(index);
		if (prop == null) {
			prop = new ElementPropertyMeta<>(prefix + index, listClassMeta.getReflectionService(), index, listClassMeta);
			properties.put(index, prop);
		}
		
		if (listIndexEnd == propertyName.length()) {
			return prop;
		}
		
		String propertyNName = propertyName.substring(0, listIndexStart)  + propertyName.substring(listIndexEnd);
		PropertyFinder<T> propertyFinder = listClassMeta.getElementClassMeta().newPropertyFinder();
		PropertyMeta<?, ?> subProp = propertyFinder.findProperty(propertyNName);

		
		if (subProp != null) {
			return new SubPropertyMeta(listClassMeta.getReflectionService(), prop, subProp);
		}
		
		return null;
	}
	
	@Override
	public PropertyMeta<List<T>, ?> findProperty(String propertyName) {
		return findProperty(new PropertyNameMatcher(propertyName));
	}

	@Override
	public List<ConstructorDefinition<List<T>>> getEligibleConstructorDefinitions() {
		return constructors ;
	}

}
