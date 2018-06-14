package org.simpleflatmapper.map;

public class ResultFieldMapperErrorHandler<K> implements FieldMapperErrorHandler<K> {
	@SuppressWarnings("unchecked")
	public void errorMappingField(K key, Object source, Object target, Exception error) throws MappingException {
		Result.ResultBuilder<?, K> resultBuilder = (Result.ResultBuilder<?, K>) ((RootCurrentInstanceProvider)source).rootCurrentInstance();
		resultBuilder.addError(new Result.FieldError<K>(key, error));
	}
}
