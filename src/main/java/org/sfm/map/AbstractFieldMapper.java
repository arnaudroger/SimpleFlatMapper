package org.sfm.map;

public abstract class AbstractFieldMapper<S, T, K>  implements FieldMapper<S, T>  {

	private final K key;
	private final FieldMapperErrorHandler<K> errorHandler;

	public AbstractFieldMapper(final K key, final FieldMapperErrorHandler<K> errorHandler) {
		this.key = key;
		this.errorHandler = errorHandler;
	}

	@Override
	public final void map(final S source, final T target) throws MappingException {
		try {
			mapUnsafe(source, target);
		} catch(Exception ge) {
			errorHandler.errorMappingField(key, source, target, ge);
		}
	}
	
	protected abstract void mapUnsafe(S source, T target) throws Exception;

}