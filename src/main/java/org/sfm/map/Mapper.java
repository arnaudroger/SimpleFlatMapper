package org.sfm.map;

public interface Mapper<S, T> {
	/**
	 * map source object to a new instance of T
	 * @param source object to map from
	 * @return a new mapped instance of T
	 * @throws Exception
	 */
	T map(S source) throws Exception;

}