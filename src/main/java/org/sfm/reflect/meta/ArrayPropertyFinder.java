package org.sfm.reflect.meta;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sfm.reflect.asm.ConstructorDefinition;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ArrayPropertyFinder<T> implements PropertyFinder<T[]> {

	private final ArrayClassMeta<T> arrayClassMeta;
	private final Map<Integer, ArrayElementPropertyMeta<T>> properties = new HashMap<Integer, ArrayElementPropertyMeta<T>>();
	private final Map<String, PropertyFinder<T>> subPropertyFinders = new HashMap<String, PropertyFinder<T>>();
	private int maxIndex = -1;
	
	
	public ArrayPropertyFinder(ArrayClassMeta<T> arrayClassMeta) {
		this.arrayClassMeta = arrayClassMeta;
	}

	@Override
	public PropertyMeta<T[], ?> findProperty(PropertyNameMatcher propertyNameMatcher) {
		
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
		
		maxIndex = Math.max(index,  maxIndex);

		ArrayElementPropertyMeta<T> prop = properties.get(index);
		if (prop == null) {
			prop = new ArrayElementPropertyMeta<T>(String.valueOf(index), arrayClassMeta.getReflectionService(), index, arrayClassMeta);
			properties.put(index, prop);
		}
		
		if (listIndexEnd == propertyName.length()) {
			return prop;
		}
		
		String subPropName = propertyName.substring(listIndexEnd);
		
		PropertyFinder<T> propertyFinder = subPropertyFinders.get(subPropName);
		
		if (propertyFinder == null) {
			propertyFinder = arrayClassMeta.getElementClassMeta().newPropertyFinder();
			subPropertyFinders.put(subPropName, propertyFinder);
		}
		
		PropertyMeta<?, ?> subProp = propertyFinder.findProperty(subPropName);

		
		if (subProp != null) {
			return new SubPropertyMeta(arrayClassMeta.getReflectionService(), prop, subProp);
		}
		
		return null;
	}
	
	@Override
	public PropertyMeta<T[], ?> findProperty(String propertyName) {
		return findProperty(new PropertyNameMatcher(propertyName));
	}

	@Override
	public List<ConstructorDefinition<T[]>> getEligibleConstructorDefinitions() {
		return Collections.emptyList();
	}

	@Override
	public Class<T[]> getClassToInstantiate() {
		return arrayClassMeta.getType();
	}

	public Class<?> getElementType() {
		return arrayClassMeta.getElementTarget();
	}
	
	public int getLength() {
		return maxIndex + 1;
	}

}
