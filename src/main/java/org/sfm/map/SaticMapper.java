package org.sfm.map;

public final class SaticMapper<S, T> implements Mapper<S, T> {
	
	private final Mapper<S, T>[] fieldMappers;
	
	public SaticMapper(Mapper<S, T>[] mappers) {
		this.fieldMappers = mappers;
	}

	@Override
	public void map(S source, T target) throws Exception {
		for(int i = 0; i < fieldMappers.length; i++) {
			fieldMappers[i].map(source, target);
		}
	}
}
