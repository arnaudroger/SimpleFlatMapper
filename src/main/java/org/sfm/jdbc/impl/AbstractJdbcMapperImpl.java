package org.sfm.jdbc.impl;

import org.sfm.jdbc.JdbcMapper;
import org.sfm.map.MappingException;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.map.impl.AbstractMapperImpl;
import org.sfm.reflect.Instantiator;
import org.sfm.utils.RowHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
//IFJAVA8_START
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
//IFJAVA8_END



public abstract class AbstractJdbcMapperImpl<T> extends AbstractMapperImpl<ResultSet, T> implements JdbcMapper<T> {
	
	private final RowHandlerErrorHandler errorHandler; 
	
	public AbstractJdbcMapperImpl(final Instantiator<ResultSet, T> instantiator, final RowHandlerErrorHandler errorHandler) {
		super(instantiator);
		this.errorHandler = errorHandler;
	}

	@Override
	public final <H extends RowHandler<? super T>> H forEach(final ResultSet rs, final H handler)
			throws SQLException, MappingException {
		while(rs.next()) {
			T t = map(rs);
			try {
				handler.handle(t);
			} catch(Throwable error) {
				errorHandler.handlerError(error, t);
			}
		}
		return handler;
	}
	
	@Override
    @Deprecated
	public Iterator<T> iterate(ResultSet rs) throws SQLException,
			MappingException {
		return new ResultSetIterator<T>(rs, this);
	}

	@Override
    @SuppressWarnings("deprecation")
    public Iterator<T> iterator(ResultSet rs) throws SQLException,
			MappingException {
		return iterate(rs);
	}
	
	//IFJAVA8_START
	@Override
	public Stream<T> stream(ResultSet rs) throws SQLException, MappingException {
		return StreamSupport.stream(new JdbcSpliterator<T>(rs, this), false);
	}

	public static class JdbcSpliterator<T> implements Spliterator<T> {
		private final ResultSet resultSet;
		private final JdbcMapper<T> mapper;

		public JdbcSpliterator(ResultSet resultSet, JdbcMapper<T> mapper) {
			this.resultSet = resultSet;
			this.mapper = mapper;
		}

		@Override
		public boolean tryAdvance(Consumer<? super T> action) {
			try {
				if (resultSet.next()) {
					action.accept(mapper.map(resultSet));
					return true;
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			return false;
		}

		@Override
		public void forEachRemaining(Consumer<? super T> action) {
			try {
				mapper.forEach(resultSet, new RowHandler<T>() {
					@Override
					public void handle(T t) throws Exception {
						action.accept(t);
					}
				});
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public Spliterator<T> trySplit() {
			return null;
		}

		@Override
		public long estimateSize() {
			return Long.MAX_VALUE;
		}

		@Override
		public int characteristics() {
			return Spliterator.ORDERED | Spliterator.NONNULL;
		}
	}	//IFJAVA8_END

}
