package org.simpleflatmapper.jdbc;


import org.simpleflatmapper.map.mapper.AbstractColumnNameDiscriminatorMapperFactory;
import org.simpleflatmapper.reflect.Getter;

import java.sql.ResultSet;

public final class NameBasedResultSetGetterFactory implements AbstractColumnNameDiscriminatorMapperFactory.DiscriminatorNamedGetterFactory<ResultSet> {
	public static final NameBasedResultSetGetterFactory INSTANCE = new NameBasedResultSetGetterFactory();

	private NameBasedResultSetGetterFactory() {
	}

	@Override
	public <T> AbstractColumnNameDiscriminatorMapperFactory.DiscriminatorNamedGetter<ResultSet, T> newGetter(final Class<T> type) {
		return new AbstractColumnNameDiscriminatorMapperFactory.DiscriminatorNamedGetter<ResultSet, T>() {
			@Override
			public T get(ResultSet resultSet, String discriminatorColumn) throws Exception {
				try {
					return resultSet.getObject(discriminatorColumn, type);
				} catch (NoSuchMethodError e) {
					throw jdbc40Error(resultSet, e);
				} catch (UnsupportedOperationException e) {
					throw jdbc40Error(resultSet, e);
				}
			}
		};
	}

	private Exception jdbc40Error(ResultSet target, Throwable e) {
		String message = "The ResultSet " + target.getClass().getName() + " does not support the T getObject(String, Class<T>) method, " +
				"you will need to use the JdbcMapperFactory.discriminator(Class<T> commonType, final String discriminatorColumn, CheckedBiFunction<S, String, V> discriminatorFieldAccessor, Consumer<DiscriminatorConditionBuilder<S, V, T>> consumer) call passing ResultSet::getXXX as the discriminatorFieldAccessor reason : " + e.getMessage();
		return new UnsupportedOperationException(message, e);
	}

}
