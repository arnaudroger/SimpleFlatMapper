package org.sfm.jdbc.impl;

import org.sfm.jdbc.*;
import org.sfm.jdbc.impl.getter.ResultSetGetterFactory;
import org.sfm.map.*;
import org.sfm.map.impl.ColumnsMapperKey;
import org.sfm.map.impl.FieldMapperColumnDefinition;
import org.sfm.map.impl.MapperCache;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.PropertyNameMatcherFactory;
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

	private final FieldMapperErrorHandler<JdbcColumnKey> fieldMapperErrorHandler;

	private final MapperBuilderErrorHandler mapperBuilderErrorHandler;
	private final ColumnDefinitionProvider<FieldMapperColumnDefinition<JdbcColumnKey, ResultSet>, JdbcColumnKey> columnDefinitions;
	private final PropertyNameMatcherFactory propertyNameMatcherFactory;
	private final RowHandlerErrorHandler rowHandlerErrorHandler;
    private final boolean failOnAsm;

    private final MapperCache<ColumnsMapperKey, JdbcMapper<T>> mapperCache = new MapperCache<ColumnsMapperKey, JdbcMapper<T>>();

	public DynamicJdbcMapper(final ClassMeta<T> classMeta,
							 final FieldMapperErrorHandler<JdbcColumnKey> fieldMapperErrorHandler,
							 final MapperBuilderErrorHandler mapperBuilderErrorHandler,
							 RowHandlerErrorHandler rowHandlerErrorHandler,
							 final ColumnDefinitionProvider<FieldMapperColumnDefinition<JdbcColumnKey, ResultSet>, JdbcColumnKey> columnDefinitions,
							 PropertyNameMatcherFactory propertyNameMatcherFactory,
                             boolean failOnAsm) {
		this.classMeta = classMeta;
		this.fieldMapperErrorHandler = fieldMapperErrorHandler;
		this.mapperBuilderErrorHandler = mapperBuilderErrorHandler;
		this.columnDefinitions = columnDefinitions;
		this.propertyNameMatcherFactory = propertyNameMatcherFactory;
		this.rowHandlerErrorHandler = rowHandlerErrorHandler;
        this.failOnAsm = failOnAsm;
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
            return JdbcMapperHelper.rethrowOnlyRuntime(e);
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
			final JdbcMapperBuilder<T> builder = new JdbcMapperBuilder<T>(classMeta, mapperBuilderErrorHandler,columnDefinitions, propertyNameMatcherFactory, new ResultSetGetterFactory(), failOnAsm, new JdbcMappingContextFactoryBuilder());

			builder.jdbcMapperErrorHandler(rowHandlerErrorHandler);
			builder.fieldMapperErrorHandler(fieldMapperErrorHandler);
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
