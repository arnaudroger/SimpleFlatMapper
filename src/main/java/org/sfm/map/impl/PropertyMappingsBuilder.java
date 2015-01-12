package org.sfm.map.impl;

import java.util.ArrayList;
import java.util.List;

import org.sfm.map.ColumnDefinition;
import org.sfm.map.MapperBuildingException;
import org.sfm.reflect.meta.*;
import org.sfm.utils.ForEachCallBack;

public final class PropertyMappingsBuilder<T, K extends FieldKey<K>, D extends ColumnDefinition<K>> {

	protected final PropertyFinder<T> propertyFinder;
	
	protected final List<PropertyMapping<T, ?, K, D>> properties = new ArrayList<PropertyMapping<T, ?, K, D>>();

	protected final PropertyNameMatcherFactory propertyNameMatcherFactory;
	
	protected boolean modifiable = true;

	public PropertyMappingsBuilder(final ClassMeta<T> classMeta, final PropertyNameMatcherFactory propertyNameMatcherFactory) throws MapperBuildingException {
		this.propertyFinder = classMeta.newPropertyFinder();
		this.propertyNameMatcherFactory = propertyNameMatcherFactory;
	}

	
	public <P> boolean addProperty(final K key, final D columnDefinition) {
		
		if (!modifiable) throw new IllegalStateException("Builder not modifiable");
		
		@SuppressWarnings("unchecked")
		final PropertyMeta<T, P> prop = (PropertyMeta<T, P>) propertyFinder.findProperty(propertyNameMatcherFactory.newInstance(key));
		
		addProperty(key, columnDefinition, prop);
		
		return prop != null;
	}

	public <P> void addProperty(final K key, final D columnDefinition, final PropertyMeta<T, P> prop) {
		properties.add(new PropertyMapping<T, P, K, D>(prop, key, columnDefinition));
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
			if ((prop.getColumnKey().getIndex() >= start || start == -1)
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
