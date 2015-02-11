package org.sfm.map.impl;

import org.sfm.map.ColumnDefinition;
import org.sfm.map.FieldKey;
import org.sfm.reflect.meta.ArrayElementPropertyMeta;
import org.sfm.reflect.meta.PropertyMeta;
import org.sfm.reflect.meta.SubPropertyMeta;
import org.sfm.utils.ForEachCallBack;

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
				propMeta = ((SubPropertyMeta<T, ?>)propMeta).getOwnerProperty();
			}
			
			if (propMeta instanceof ArrayElementPropertyMeta<?, ?>) {
				currentIndex = ((ArrayElementPropertyMeta<?, ?>) propMeta).getIndex();
			}
		}
		maxIndex = Math.max(currentIndex, maxIndex);
	}
}