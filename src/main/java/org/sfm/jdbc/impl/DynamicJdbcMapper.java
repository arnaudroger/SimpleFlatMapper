package org.sfm.jdbc.impl;

import org.sfm.jdbc.*;
import org.sfm.jdbc.impl.getter.ResultSetGetterFactory;
import org.sfm.map.*;
import org.sfm.map.impl.ColumnsMapperKey;
import org.sfm.map.impl.FieldMapperColumnDefinition;
import org.sfm.map.impl.MapperCache;
import org.sfm.map.impl.MapperConfig;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.PropertyNameMatcherFactory;
import org.sfm.utils.ErrorHelper;
import org.sfm.utils.RowHandler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
//IFJAVA8_START
import java.util.stream.Stream;
//IFJAVA8_END


public final class DynamicJdbcMapper<T> extends AbstractDynamicJdbcMapper<T> {

	private final ClassMeta<T> classMeta;

    private final MapperCache<ColumnsMapperKey, JdbcMapper<T>> mapperCache = new MapperCache<ColumnsMapperKey, JdbcMapper<T>>();

	private final MapperConfig<JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey, ResultSet>> mapperConfig;

	public DynamicJdbcMapper(final ClassMeta<T> classMeta, MapperConfig<JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey, ResultSet>> mapperConfig) {
		this.classMeta = classMeta;
		this.mapperConfig = mapperConfig;
	}

	@Override
	public final <H extends RowHandler<? super T>> H forEach(final ResultSet rs, final H handle)
			throws SQLException, MappingException {
		return getMapper(rs).forEach(rs, handle);
	}

	@Override
    @Deprecated
	public final Iterator<T> iterate(final ResultSet rs)
			throws SQLException, MappingException {
		return getMapper(rs).iterator(rs);
	}

	@Override
    @SuppressWarnings("deprecation")
    public final Iterator<T> iterator(final ResultSet rs)
			throws SQLException, MappingException {
		return iterate(rs);
	}

	//IFJAVA8_START
	@Override
	public Stream<T> stream(ResultSet rs) throws SQLException, MappingException {
		return getMapper(rs).stream(rs);
	}
    //IFJAVA8_END

    @Override
    public MappingContext<ResultSet> newMappingContext(ResultSet source) throws MappingException {
        try {
            return getMapper(source).newMappingContext(source);
        } catch(Exception e) {
            return ErrorHelper.rethrow(e);
        }
    }

    @Override
	public JdbcMapper<T> getMapper(final ResultSet rs) throws MapperBuildingException, SQLException {
        return getMapper(rs.getMetaData());
    }

    public JdbcMapper<T> getMapper(final ResultSetMetaData metaData) throws MapperBuildingException, SQLException {
        final ColumnsMapperKey key = mapperKey(metaData);
		
		JdbcMapper<T> mapper = mapperCache.get(key);
		
		if (mapper == null) {
			final JdbcMapperBuilder<T> builder =
                    new JdbcMapperBuilder<T>(
							classMeta,
							mapperConfig,
                            new ResultSetGetterFactory(),
							new JdbcMappingContextFactoryBuilder());

			builder.addMapping(metaData);
			
			mapper = builder.mapper();
			mapperCache.add(key, mapper);
		}
		return mapper;
	}

	private static ColumnsMapperKey mapperKey(final ResultSetMetaData metaData) throws SQLException {
		final String[] columns = new String[metaData.getColumnCount()];
		
		for(int i = 0; i < columns.length; i++) {
			columns[i] = metaData.getColumnLabel(i + 1);
		}
		
		return new ColumnsMapperKey(columns);
	}

    @Override
    public String toString() {
        return "DynamicJdbcMapper{target=" + classMeta.getType()
                +  ", " + mapperCache +
                '}';
    }
}
