package org.sfm.map;

public interface FieldMapper<S, T> {
	/**
	 * map source object to target object.
	 * @param source object to map from
	 * @param target object to map to
	 * @throws Exception
	 */
	void map(S source, T target) throws Exception;

}