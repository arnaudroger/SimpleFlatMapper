package org.sfm.map.impl;

import org.sfm.reflect.meta.ArrayElementPropertyMeta;
import org.sfm.reflect.meta.PropertyMeta;
import org.sfm.reflect.meta.SubPropertyMeta;
import org.sfm.utils.ForEachCallBack;

public final class CaclculateMaxIndex<T, K> implements
		ForEachCallBack<PropertyMapping<T, ?, K>> {
	public int maxIndex = -1;

	@Override
	public void handle(PropertyMapping<T, ?, K> e, int index) {
		int currentIndex = -1;
		if (e != null) {
			PropertyMeta<T, ?> propMeta = e.getPropertyMeta();
			if (propMeta.isSubProperty()) {
				propMeta = ((SubPropertyMeta<T, ?>)propMeta).getOwnerProperty(); 
			}
			
			if (propMeta instanceof ArrayElementPropertyMeta<?>) {
				currentIndex = ((ArrayElementPropertyMeta<?>) propMeta).getIndex();
			}
		}
		maxIndex = Math.max(currentIndex, maxIndex);
	}
}