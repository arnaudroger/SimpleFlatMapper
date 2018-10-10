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
	public <T> Getter<? super ResultSet, ? extends T> getGetter(final String discriminatorColumn, final Class<T> discriminatorType) {
		return new Getter<ResultSet, T>() {
			@Override
			public T get(ResultSet target) throws Exception {
				try {
					return target.getObject(discriminatorColumn, discriminatorType);
				} catch (NoSuchMethodError e) {
					throw jdbc40Error(target, e);	
				} catch (UnsupportedOperationException e) {
					throw jdbc40Error(target, e);
				}
			}

			private Exception jdbc40Error(ResultSet target, Throwable e) {
				String message = "The ResultSet " + target.getClass().getName() + " does not support the T getObject(String, Class<T>) method, " +
						"you will need to use the JdbcMapperFactory.discriminator(Class<T> commonType, final String discriminatorColumn, CheckedBiFunction<S, String, V> discriminatorFieldAccessor, Consumer<DiscriminatorConditionBuilder<S, V, T>> consumer) call passing ResultSet::getXXX as the discriminatorFieldAccessor reason : " + e.getMessage();
				return new UnsupportedOperationException(message, e);
			}
		};
	}
}
