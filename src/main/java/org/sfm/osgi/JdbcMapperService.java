package org.sfm.osgi;

import org.sfm.jdbc.JdbcMapperFactory;

public interface JdbcMapperService {
	public JdbcMapperFactory newFactory();
}
