package org.sfm.map;

public interface Mapper<S, T> {

	public abstract void map(S source, T target) throws Exception;

}