package org.simpleflatmapper.map;

public class ResultFieldMapperErrorHandler<K> implements FieldMapperErrorHandler<K> {
	private final FieldMapperErrorHandler<K> delegate;

	public ResultFieldMapperErrorHandler(FieldMapperErrorHandler<K> delegate) {
		this.delegate = delegate;
	}

	public void errorMappingField(K key, Object source, Object target, Exception error) throws MappingException {
		if (target instanceof Result) {
			Result r = (Result) target;
			r.getErrors().add(new Result.FieldError<K>(key, error));
		} else if (delegate != null) {
			delegate.errorMappingField(key, source, target, error);
		}
	}
}
