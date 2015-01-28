package org.sfm.map.impl;

import org.sfm.map.ColumnDefinition;
import org.sfm.map.FieldKey;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.MapperBuildingException;
import org.sfm.reflect.TypeHelper;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.PropertyFinder;
import org.sfm.reflect.meta.PropertyMeta;
import org.sfm.reflect.meta.PropertyNameMatcherFactory;
import org.sfm.utils.ForEachCallBack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public final class PropertyMappingsBuilder<T, K extends FieldKey<K>, D extends ColumnDefinition<K>> {

	protected final PropertyFinder<T> propertyFinder;
	
	protected final List<PropertyMapping<T, ?, K, D>> properties = new ArrayList<PropertyMapping<T, ?, K, D>>();

	protected final PropertyNameMatcherFactory propertyNameMatcherFactory;

	private final MapperBuilderErrorHandler mapperBuilderErrorHandler;
	private final ClassMeta<T> classMeta;

	protected boolean modifiable = true;

	public PropertyMappingsBuilder(final ClassMeta<T> classMeta,
								   final PropertyNameMatcherFactory propertyNameMatcherFactory,
								   final MapperBuilderErrorHandler mapperBuilderErrorHandler) throws MapperBuildingException {
		this.mapperBuilderErrorHandler = mapperBuilderErrorHandler;
		this.propertyFinder = classMeta.newPropertyFinder();
		this.propertyNameMatcherFactory = propertyNameMatcherFactory;
		this.classMeta = classMeta;
	}

	
	public <P> PropertyMeta<T, P> addProperty(final K key, final D columnDefinition) {
		
		if (!modifiable) throw new IllegalStateException("Builder not modifiable");
		
		@SuppressWarnings("unchecked")
		final PropertyMeta<T, P> prop = (PropertyMeta<T, P>) propertyFinder.findProperty(propertyNameMatcherFactory.newInstance(key));

		if (prop == null) {
			mapperBuilderErrorHandler.propertyNotFound(classMeta.getType(), key.getName());
			properties.add(null);
		} else {
			addProperty(key, columnDefinition, prop);
		}
		return prop;
	}

	public <P> void addProperty(final K key, final D columnDefinition, final PropertyMeta<T, P> prop) {
		if (columnDefinition.hasCustomSource()) {
			if (!checkTypeCompatibility(key, columnDefinition.getCustomSourceReturnType(), prop.getType())) {
				properties.add(null);
				return;
			}
		}
		properties.add(new PropertyMapping<T, P, K, D>(prop, key, columnDefinition));
	}

	private boolean checkTypeCompatibility(K key, Type customSourceReturnType, Type propertyMetaType) {
		if (customSourceReturnType == null) {
			// cannot determine type
			return true;
		} else if(!areCompatible(propertyMetaType, customSourceReturnType)) {
			mapperBuilderErrorHandler.customFieldError(key, "Incompatible customreader type " + customSourceReturnType +  " expected " + propertyMetaType);
			return false;
		}
		return true;
	}

	private boolean areCompatible(Type propertyMetaType, Type customSourceReturnType) {
		Class<?> propertyMetaClass = TypeHelper.toBoxedClass(TypeHelper.toClass(propertyMetaType));
		Class<?> customSourceReturnClass = TypeHelper.toBoxedClass(TypeHelper.toClass(customSourceReturnType));
		return propertyMetaClass.isAssignableFrom(customSourceReturnClass);
	}


	public List<K> getKeys() {
		modifiable = false;
		
		List<K>  keys = new ArrayList<K>(properties.size());
		for(int i = 0; i < properties.size(); i++) {
			PropertyMapping<T, ?, K, D> propMapping = properties.get(i);
			if (propMapping != null) {
				keys.add(propMapping.getColumnKey());
			} else {
				keys.add(null);
			}
				
		}
		return keys;
	}

	public void forEachConstructorProperties(ForEachCallBack<PropertyMapping<T, ?, K, D>> handler)  {
		modifiable = false;

		for(int i = 0; i < properties.size(); i++) {
			PropertyMapping<T, ?, K, D> property = properties.get(i);
			if (property != null) {
				PropertyMeta<T, ?> propertyMeta = property.getPropertyMeta();
				if (propertyMeta != null && propertyMeta.isConstructorProperty()) {
					handler.handle(property);
				}
			}
 		}
	}
	
	public void forEachSubProperties(ForEachCallBack<PropertyMapping<T, ?, K, D>> handler)  {
		modifiable = false;

		for(int i = 0; i < properties.size(); i++) {
			PropertyMapping<T, ?, K, D> property = properties.get(i);
			if (property != null) {
				PropertyMeta<T, ?> propertyMeta = property.getPropertyMeta();
				if (propertyMeta != null && propertyMeta.isSubProperty()) {
					handler.handle(property);
				} 
			}
 		}
	}

	public <H extends ForEachCallBack<PropertyMapping<T, ?, K, D>>> H forEachProperties(H handler)  {
		return forEachProperties(handler, -1 );
	}
	
	public <F extends ForEachCallBack<PropertyMapping<T, ?, K, D>>> F forEachProperties(F handler, int start)  {
		return forEachProperties(handler, start, -1 );
	}
	
	public <F extends ForEachCallBack<PropertyMapping<T, ?, K, D>>> F forEachProperties(F handler, int start, int end)  {
		modifiable = false;
		for(int i = 0; i < properties.size(); i++) {
			PropertyMapping<T, ?, K, D> prop = properties.get(i);
			if (prop != null
			         && (prop.getColumnKey().getIndex() >= start || start == -1)
					 && (prop.getColumnKey().getIndex() < end || end == -1)) {
				handler.handle(prop);
			}
 		}
		return handler;
	}


	public PropertyFinder<T> getPropertyFinder() {
		modifiable = false;
		return propertyFinder;
	}


	public int size() {
		return properties.size();
	}


	public PropertyMapping<T, ?, K, D> get(int i) {
		modifiable = false;
		return properties.get(i);
	}
	
}
