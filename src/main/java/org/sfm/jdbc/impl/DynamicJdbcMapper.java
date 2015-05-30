package org.sfm.jdbc.impl;

import org.sfm.jdbc.*;
import org.sfm.map.*;
import org.sfm.map.impl.*;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.utils.ErrorHelper;
import org.sfm.utils.RowHandler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
//IFJAVA8_START
import java.util.stream.Stream;
//IFJAVA8_END


public final class DynamicJdbcMapper<T> implements JdbcMapper<T> {

	private final ClassMeta<T> classMeta;

    private final MapperCache<ColumnsMapperKey, JdbcMapper<T>> mapperCache =
			new MapperCache<ColumnsMapperKey, JdbcMapper<T>>();

	private final MapperConfig<JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey, ResultSet>> mapperConfig;
	private final GetterFactory<ResultSet,JdbcColumnKey> getterFactory;

	public DynamicJdbcMapper(final ClassMeta<T> classMeta,
							 MapperConfig<JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey, ResultSet>> mapperConfig, GetterFactory<ResultSet, JdbcColumnKey> getterFactory) {
		this.classMeta = classMeta;
		this.mapperConfig = mapperConfig;
		this.getterFactory = getterFactory;
	}

	@Override
	public T map(ResultSet rs) throws MappingException {
		return getJdbcMapper(rs).map(rs);
	}

	@Override
	public T map(ResultSet source, MappingContext<ResultSet> context) throws MappingException {
		return getJdbcMapper(source).map(source, context);
	}

	@Override
	public <H extends RowHandler<? super T>> H forEach(ResultSet rs, H handler) throws SQLException, MappingException {
		return getJdbcMapper(rs).forEach(rs, handler);
	}

	@Override
	public void mapTo(ResultSet source, T target, MappingContext<ResultSet> context) throws Exception {
		getJdbcMapper(source).mapTo(source, target, context);
	}

	protected JdbcMapper<T> getJdbcMapper(ResultSet rs) {
		try {
			return getJdbcMapper(getMapperKey(rs));
		} catch (SQLException e) {
			return ErrorHelper.rethrow(e);
		}
 	}

	@Override
	public Iterator<T> iterator(ResultSet rs) throws SQLException, MappingException {
		return getJdbcMapper(rs).iterator(rs);
	}

	//IFJAVA8_START
	@Override
	public Stream<T> stream(ResultSet rs) throws SQLException, MappingException {
		return getJdbcMapper(rs).stream(rs);
	}
	//IFJAVA8_END

	@Override
	public MappingContext<ResultSet> newMappingContext(ResultSet rs) throws SQLException {
		return getJdbcMapper(rs).newMappingContext(rs);
	}

	@Override
	public String toString() {
		return "DynamicJdbcMapper{target=" + classMeta.getType()
				+  ", " + mapperCache +
				'}';
	}

	public JdbcMapper<T> getMapper(ResultSetMetaData resultSetMetaData) throws SQLException {
		return getJdbcMapper(JdbcColumnKey.mapperKey(resultSetMetaData));
	}

	protected ColumnsMapperKey<JdbcColumnKey> getMapperKey(ResultSet rs) throws SQLException {
		return JdbcColumnKey.mapperKey(rs.getMetaData());
	}

	private JdbcMapper<T> getJdbcMapper(ColumnsMapperKey<JdbcColumnKey> key) throws SQLException {
		JdbcMapper<T> mapper = mapperCache.get(key);

		if (mapper == null) {
			final JdbcMapperBuilder<T> builder =
					new JdbcMapperBuilder<T>(
							classMeta,
							mapperConfig,
							getterFactory,
							new JdbcMappingContextFactoryBuilder());

			for(JdbcColumnKey k : key.getColumns()) {
				builder.addMapping(k, FieldMapperColumnDefinition.<JdbcColumnKey, ResultSet>identity());
			}

			mapper = builder.mapper();

			mapperCache.add(key, mapper);
		}
		return mapper;
	}
}
