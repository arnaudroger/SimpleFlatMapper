package org.sfm.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.sfm.map.Mapper;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterFactory;
import org.sfm.utils.Handler;
import org.sfm.utils.ListHandler;

public class DynamicJdbcMapper<T> implements JdbcMapper<T> {

	private final Mapper<ResultSet, T> adaptiveMapper;
	private final Map<String, Setter<T, Object>> setters;
	
	private final Instantiator<T> instantiator;
	private final Class<T> target;
	private final SetterFactory setterFactory;

	
	public DynamicJdbcMapper(Class<T> target, Instantiator<T> instantiator) {
		this.setterFactory = new SetterFactory();
		this.setters = setterFactory.getAllSetters(target);
		this.adaptiveMapper = new AdaptiveMapper<>(setters);
		this.instantiator = instantiator;
		this.target = target;
	}

	@Override
	public final void map(ResultSet source, T target) throws Exception {
		adaptiveMapper.map(source, target);
	}

	@Override
	public final <H extends Handler<T>> H forEach(final ResultSet rs, final H handle)
			throws Exception {
		
		final Mapper<ResultSet, T> mapper = buildMapper(rs.getMetaData());
		
		while(rs.next()) {
			final T t = instantiator.newInstance();
			mapper.map(rs, t);
			handle.handle(t);
		}
		
		return handle;
	}

	@Override
	public final <H extends Handler<T>> H forEach(PreparedStatement statement, H handle)
			throws Exception {
		ResultSet rs = statement.executeQuery();
		try {
			forEach(rs, handle);
		} finally {
			rs.close();
		}
		return handle;
	}

	private Mapper<ResultSet, T> buildMapper(ResultSetMetaData metaData) throws SQLException {
		CachedResultSetMapperBuilder<T> builder = new CachedResultSetMapperBuilder<T>(target, setters, setterFactory);

		for(int i = 1; i <= metaData.getColumnCount(); i++) {
			builder.addIndexedColumn(metaData.getColumnName(i));
		}
		
		return builder.mapper();
	}
	
	@Override
	public List<T> list(ResultSet rs) throws Exception {
		return forEach(rs, new ListHandler<T>()).getList();
	}

	@Override
	public List<T> list(PreparedStatement ps) throws Exception {
		return forEach(ps, new ListHandler<T>()).getList();
	}
}
