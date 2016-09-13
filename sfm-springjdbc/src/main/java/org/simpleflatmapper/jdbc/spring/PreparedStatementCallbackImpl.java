package org.simpleflatmapper.jdbc.spring;

import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.util.CheckedConsumer;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public final class PreparedStatementCallbackImpl<T> implements PreparedStatementCallback<List<T>> {
	private final ResultSetExtractorImpl<T> resultSetExtractor;

	public PreparedStatementCallbackImpl(JdbcMapper<T> mapper) {
		this.resultSetExtractor = new ResultSetExtractorImpl<T>(mapper);
	}

	@Override
	public List<T> doInPreparedStatement(PreparedStatement ps) throws SQLException,
			DataAccessException {
		ResultSet rs = ps.executeQuery();
		try {
			return resultSetExtractor.extractData(rs);
		} finally {
			rs.close();
		}
	}


	public <H extends CheckedConsumer<T>> PreparedStatementCallback<H> newPreparedStatementCallback(final H handler) {
		return new PreparedStatementCallback<H>() {
			@Override
			public H doInPreparedStatement(
					PreparedStatement ps)
					throws SQLException, DataAccessException {
				ResultSet rs = ps.executeQuery();
				ResultSetExtractor<H> extractor = resultSetExtractor.newResultSetExtractor(handler);
				try {
					return extractor.extractData(rs);
				} finally {
					rs.close();
				}
			}
		};
	}



}
