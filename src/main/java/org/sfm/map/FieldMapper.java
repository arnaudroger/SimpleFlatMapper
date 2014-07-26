package org.sfm.map;

import org.sfm.reflect.Getter;
import org.sfm.reflect.Setter;

public class FieldMapper<S, T, P> implements Mapper<S, T> {
	
	private final String name; 
	private final Getter<S, ? extends P> getter;
	private final Setter<T, P> setter;
	private final FieldMapperErrorHandler errorHandler;
	
	public FieldMapper(String name, Getter<S, ? extends P> getter, Setter<T, P> setter, FieldMapperErrorHandler errorHandler) {
		this.getter = getter;
		this.setter = setter;
		this.errorHandler = errorHandler;
		this.name = name;
	}
	
	@Override
	public void map(S source, T target) throws Exception {
		try {
			P value = getter.get(source);
			try {
				setter.set(target, value);
			} catch(Exception se) {
				errorHandler.errorSettingValue(name, source, target, se);
			}
		} catch(Exception ge) {
			errorHandler.errorGettingValue(name, source, target, ge);
		}
	}
}
