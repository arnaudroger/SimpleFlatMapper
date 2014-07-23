package org.sfm.map;

public class SaticMapper<S, T> implements Mapper<S, T> {
	
	private final FieldMapper<S, T>[] mappers;
	
	public SaticMapper(FieldMapper<S, T>[] mappers) {
		this.mappers = mappers;
	}

	@Override
	public void map(S source, T target) throws Exception {
		for(int i = 0; i < mappers.length; i++) {
			mappers[i].map(source, target);
		}
	}
}
