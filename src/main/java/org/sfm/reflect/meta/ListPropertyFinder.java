package org.sfm.reflect.meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sfm.reflect.asm.ConstructorDefinition;
import org.sfm.reflect.asm.ConstructorParameter;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ListPropertyFinder<T> implements PropertyFinder<List<T>> {

	private final ListClassMeta<T> listClassMeta;
	private final Map<Integer, ElementPropertyMeta<T>> properties = new HashMap<Integer, ElementPropertyMeta<T>>();
	private final Map<String, PropertyFinder<T>> subPropertyFinders = new HashMap<String, PropertyFinder<T>>();
	private static final List constructors;
	
	static {
		constructors = new ArrayList();
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
		
		String propertyName = propertyNameMatcher.getColumn();
		
		int listIndexStart = propertyNameMatcher.getFrom();
		while(listIndexStart < propertyName.length() &&  !Character.isDigit(propertyName.charAt(listIndexStart))) {
			listIndexStart++;
		}
		
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
			prop = new ElementPropertyMeta<T>(String.valueOf(index), listClassMeta.getReflectionService(), index, listClassMeta);
			properties.put(index, prop);
		}
		
		if (listIndexEnd == propertyName.length()) {
			return prop;
		}
		
		String subPropName = propertyName.substring(listIndexEnd);
		
		PropertyFinder<T> propertyFinder = subPropertyFinders.get(subPropName);
		
		if (propertyFinder == null) {
			propertyFinder = listClassMeta.getElementClassMeta().newPropertyFinder();
			subPropertyFinders.put(subPropName, propertyFinder);
		}
		
		PropertyMeta<?, ?> subProp = propertyFinder.findProperty(subPropName);

		
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

	@Override
	public Class<?> getClassToInstantiate() {
		return ArrayList.class;
	}

}
