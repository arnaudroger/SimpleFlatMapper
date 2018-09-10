package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.reflect.meta.ArrayElementPropertyMeta;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.reflect.meta.SubPropertyMeta;
import org.simpleflatmapper.util.ForEachCallBack;

public final class CalculateMaxIndex<T, K extends FieldKey<K>> implements
		ForEachCallBack<PropertyMapping<T, ?, K>> {
	public int maxIndex = -1;

	@SuppressWarnings("unchecked")
	@Override
	public void handle(PropertyMapping<T, ?, K> e) {
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