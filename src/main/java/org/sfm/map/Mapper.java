package org.sfm.map;

public class Mapper<S, T> {
	
	private final FieldMapper<S, T>[] mappers;
	
	public Mapper(FieldMapper<S, T>[] mappers) {
		this.mappers = mappers;
	}

	public void map(S source, T target) throws Exception {
		for(int i = 0; i < mappers.length; i++) {
			mappers[i].map(source, target);
		}
	}
}
