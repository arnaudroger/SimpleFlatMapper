package org.sfm.osgi;

import org.sfm.jdbc.JdbcMapperFactory;

public class JdbcMapperServiceImpl implements JdbcMapperService {


	public JdbcMapperServiceImpl() {
	}

	@Override
	public JdbcMapperFactory newFactory() {
		return new JdbcMapperFactory(true);
	}

}
