package org.sfm.osgi;

import org.sfm.jdbc.JdbcMapperFactory;
import org.sfm.reflect.asm.AsmFactory;

public class JdbcMapperServiceImpl implements JdbcMapperService {


	public JdbcMapperServiceImpl() {
	}

	@Override
	public JdbcMapperFactory newFactory() {
		return new JdbcMapperFactory(new AsmFactory(getClass().getClassLoader()), true);
	}

}
