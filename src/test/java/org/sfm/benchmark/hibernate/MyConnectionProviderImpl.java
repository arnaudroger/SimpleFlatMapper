package org.sfm.benchmark.hibernate;

import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.engine.jdbc.connections.internal.UserSuppliedConnectionProviderImpl;

@SuppressWarnings("serial")
public class MyConnectionProviderImpl extends UserSuppliedConnectionProviderImpl {

	private static Connection _conn;
	
	private Connection conn;

	public MyConnectionProviderImpl() {
		if (_conn == null) {
			throw new IllegalStateException("No connection set");
		}
		this.conn = _conn;
	}
	
	@Override
	public void closeConnection(Connection conn) throws SQLException {
	}

	@Override
	public Connection getConnection() throws SQLException {
		return conn;
	}

	@Override
	public boolean supportsAggressiveRelease() {
		return false;
	}

	public static void setConnection(Connection conn) {
		MyConnectionProviderImpl._conn = conn;
	}
}
