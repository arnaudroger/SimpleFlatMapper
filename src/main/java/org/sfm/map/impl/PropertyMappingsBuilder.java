package org.sfm.map.impl;

import java.util.ArrayList;
import java.util.List;

import org.sfm.map.MapperBuildingException;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.PropertyFinder;
import org.sfm.reflect.meta.PropertyMeta;
import org.sfm.utils.ForEachCallBack;

public final class PropertyMappingsBuilder<T, K extends FieldKey<K>> {

	protected final PropertyFinder<T> propertyFinder;
	
	protected final List<PropertyMapping<T, ?, K>> properties = new ArrayList<PropertyMapping<T, ?, K>>();
	
	protected boolean modifiable = true;

	public PropertyMappingsBuilder(final ClassMeta<T> classMeta) throws MapperBuildingException {
		this.propertyFinder = classMeta.newPropertyFinder();
	}

	
	public <P> boolean addProperty(final K key) {
		
		if (!modifiable) throw new IllegalStateException("Builder not modifiable");
		
		@SuppressWarnings("unchecked")
		final PropertyMeta<T, P> prop = (PropertyMeta<T, P>) propertyFinder.findProperty(key.getName());
		
		addProperty(key, prop);
		
		return prop != null;
	}


	public <P> void addProperty(final K key, final PropertyMeta<T, P> prop) {
		while(properties.size() <= key.getIndex()) {
			properties.add(null);
		}
		properties.set(key.getIndex(), new PropertyMapping<T, P, K>(prop, key));
	}
	
	public List<K> getKeys() {
		modifiable = false;
		
		List<K>  keys = new ArrayList<K>(properties.size());
		for(int i = 0; i < properties.size(); i++) {
			PropertyMapping<T, ?, K> propMapping = properties.get(i);
			if (propMapping != null) {
				keys.add(propMapping.getColumnKey());
			} else {
				keys.add(null);
			}
				
		}
		return keys;
	}

	public void forEachConstructorProperties(ForEachCallBack<PropertyMapping<T, ?, K>> handler)  {
		modifiable = false;

		for(int i = 0; i < properties.size(); i++) {
			PropertyMapping<T, ?, K> property = properties.get(i);
			if (property != null) {
				PropertyMeta<T, ?> propertyMeta = property.getPropertyMeta();
				if (propertyMeta != null && propertyMeta.isConstructorProperty()) {
					handler.handle(property, i);
				}
			}
 		}
	}
	
	public void forEachSubProperties(ForEachCallBack<PropertyMapping<T, ?, K>> handler)  {
		modifiable = false;

		for(int i = 0; i < properties.size(); i++) {
			PropertyMapping<T, ?, K> property = properties.get(i);
			if (property != null) {
				PropertyMeta<T, ?> propertyMeta = property.getPropertyMeta();
				if (propertyMeta != null && propertyMeta.isSubProperty()) {
					handler.handle(property, i);
				} 
			}
 		}
	}

	public <H extends ForEachCallBack<PropertyMapping<T, ?, K>>> H forEachProperties(H handler)  {
		forEachProperties(handler, 0);
		return handler;
	}
	
	public void forEachProperties(ForEachCallBack<PropertyMapping<T, ?, K>> handler, int start)  {
		forEachProperties(handler, start, properties.size());
	}
	
	public void forEachProperties(ForEachCallBack<PropertyMapping<T, ?, K>> handler, int start, int end)  {
		modifiable = false;
		for(int i = start; i < end; i++) {
			PropertyMapping<T, ?, K> prop = properties.get(i);
			handler.handle(prop, i);
 		}
	}


	public PropertyFinder<T> getPropertyFinder() {
		modifiable = false;
		return propertyFinder;
	}


	public int size() {
		return properties.size();
	}


	public PropertyMapping<T, ?, K> get(int i) {
		modifiable = false;
		return properties.get(i);
	}
	
}
