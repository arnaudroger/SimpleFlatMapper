package org.sfm.map;

public abstract class AbstractFieldMapper<S, T>  implements Mapper<S, T>  {

	private final String name;
	private final FieldMapperErrorHandler errorHandler;

	public AbstractFieldMapper(String name, FieldMapperErrorHandler errorHandler) {
		super();
		this.name = name;
		this.errorHandler = errorHandler;
	}

	@Override
	public final void map(S source, T target) throws Exception {
		try {
			mapUnsafe(source, target);
		} catch(Exception ge) {
			errorHandler.errorMappingField(name, source, target, ge);
		}
	}
	
	protected abstract void mapUnsafe(S source, T target) throws Exception;

}