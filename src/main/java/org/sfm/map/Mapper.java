package org.sfm.map;

public interface Mapper<S, T> {
	/**
	 * map source object to a new instance of T
	 * @param source object to map from
	 * @return a new mapped instance of T
	 * @throws MappingException
	 */
	T map(S source) throws MappingException;


    /**
     * map source object on an existing target
     * @param source object to map from
     * @param target object to map to
     * @throws MappingException
     */
    void mapTo(S source, T target) throws MappingException;

}