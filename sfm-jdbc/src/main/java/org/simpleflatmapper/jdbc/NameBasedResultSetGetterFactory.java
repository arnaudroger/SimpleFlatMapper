package org.simpleflatmapper.jdbc;


import org.simpleflatmapper.map.mapper.AbstractColumnNameDiscriminatorMapperFactory;
import org.simpleflatmapper.reflect.Getter;

import java.sql.ResultSet;

public final class NameBasedResultSetGetterFactory implements AbstractColumnNameDiscriminatorMapperFactory.ColumnNameGetterFactory<ResultSet> {
	public static final NameBasedResultSetGetterFactory INSTANCE = new NameBasedResultSetGetterFactory();

	private NameBasedResultSetGetterFactory() {
	}

	

	@SuppressWarnings("unchecked")
	@Override
	public <T> Getter<? super ResultSet, ? extends T> getGetter(String discriminatorColumn, Class<T> discriminatorType) {
		return new Getter<ResultSet, T>() {
			@Override
			public T get(ResultSet target) throws Exception {
				return target.getObject(discriminatorColumn, discriminatorType);
			}
		};
	}
}
