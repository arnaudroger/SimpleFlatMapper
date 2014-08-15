package org.sfm.jdbc;

import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterFactory;
import org.sfm.utils.PropertyNameMatcher;

public class ResultSetMapperBuilderImpl<T> extends AbstractResultSetMapperBuilder<T> {

	private final SetterFactory setterFactory;

	public ResultSetMapperBuilderImpl(Class<T> target) {
		this(target, new SetterFactory());
	}

	public ResultSetMapperBuilderImpl(Class<T> target, SetterFactory setterFactory) {
		super(target, setterFactory);
		this.setterFactory = setterFactory;
	}

	protected Setter<T, Object> findSetter(String column) {
		return setterFactory.findSetter(new PropertyNameMatcher(column), getTarget());
	}
	protected  Setter<T, Object> getSetter(String property) {
		return setterFactory.getSetter(getTarget(), property);
	}

}
