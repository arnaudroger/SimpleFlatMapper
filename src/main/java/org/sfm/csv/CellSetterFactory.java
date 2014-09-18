package org.sfm.csv;

import java.util.HashMap;
import java.util.Map;

import org.sfm.reflect.Setter;
import org.sfm.reflect.TypeHelper;

public class CellSetterFactory {

	@SuppressWarnings("serial")
	private static final Map<Class<?>, CellValueTransfomer<?>> transformers = new HashMap<Class<?>, CellValueTransfomer<?>>(){{
		put(Integer.class, new IntegerCellValueTransformer());
		put(int.class, new IntegerCellValueTransformer());
		put(Long.class, new LongCellValueTransformer());
		put(long.class, new LongCellValueTransformer());
	}};
	public <T,P> CellSetter<T> getCellSetter(Setter<T, P> setter) {
		Class<P> propertyType = TypeHelper.toClass(setter.getPropertyType());
		CellValueTransfomer<P> cellValueTransformer = getCellValueTransformer(propertyType);
		return new CellSetterImpl<T, P>(cellValueTransformer, setter) ;
	}

	@SuppressWarnings("unchecked")
	private <P> CellValueTransfomer<P> getCellValueTransformer(Class<P> propertyType) {
		return (CellValueTransfomer<P>) transformers.get(propertyType);
	}
}
