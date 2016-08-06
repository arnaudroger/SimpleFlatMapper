package org.simpleflatmapper.map;

public interface FieldMapper<S, T> {
	/**
	 * map source object to target object.
	 * @param source object to map from
	 * @param target object to map to
     * @param context the mapping context
	 * @throws Exception if an exception occurs
	 */
	void mapTo(S source, T target, MappingContext<? super S> context) throws Exception;
}