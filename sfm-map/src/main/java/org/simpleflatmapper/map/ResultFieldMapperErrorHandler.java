package org.simpleflatmapper.map;

public class ResultFieldMapperErrorHandler<K> implements FieldMapperErrorHandler<K> {
	private final FieldMapperErrorHandler<? super K> delegate;

	public ResultFieldMapperErrorHandler(FieldMapperErrorHandler<? super K> delegate) {
		this.delegate = delegate;
	}

	@SuppressWarnings("unchecked")
	public void errorMappingField(K key, Object source, Object target, Exception error) throws MappingException {
		if (target instanceof Result.ResultBuilder) {
			Result.ResultBuilder<?, K> r = (Result.ResultBuilder<?, K>) target;
			r.addError(new Result.FieldError<K>(key, error));
		} else if (delegate != null) {
			delegate.errorMappingField(key, source, target, error);
		}
	}
}
