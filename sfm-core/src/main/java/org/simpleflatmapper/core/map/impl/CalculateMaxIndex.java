package org.simpleflatmapper.core.map.impl;

import org.simpleflatmapper.core.map.mapper.ColumnDefinition;
import org.simpleflatmapper.core.map.FieldKey;
import org.simpleflatmapper.core.map.mapper.PropertyMapping;
import org.simpleflatmapper.core.reflect.meta.ArrayElementPropertyMeta;
import org.simpleflatmapper.core.reflect.meta.PropertyMeta;
import org.simpleflatmapper.core.reflect.meta.SubPropertyMeta;
import org.simpleflatmapper.core.utils.ForEachCallBack;

public final class CalculateMaxIndex<T, K extends FieldKey<K>, D extends ColumnDefinition<K, D>> implements
		ForEachCallBack<PropertyMapping<T, ?, K, D>> {
	public int maxIndex = -1;

	@SuppressWarnings("unchecked")
	@Override
	public void handle(PropertyMapping<T, ?, K, D> e) {
		int currentIndex = -1;
		if (e != null) {
			PropertyMeta<T, ?> propMeta = e.getPropertyMeta();
			if (propMeta.isSubProperty()) {
				propMeta = ((SubPropertyMeta<T, ?, ?>)propMeta).getOwnerProperty();
			}
			
			if (propMeta instanceof ArrayElementPropertyMeta<?, ?>) {
				currentIndex = ((ArrayElementPropertyMeta<?, ?>) propMeta).getIndex();
			}
		}
		maxIndex = Math.max(currentIndex, maxIndex);
	}
}