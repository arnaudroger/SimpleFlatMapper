package org.flatmap.map;

public interface FieldMapper<S, T> {

	public abstract void map(S source, T target) throws Exception;

}