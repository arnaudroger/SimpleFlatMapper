package org.sfm.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DbHelper {
	
	private static boolean objectDb;
	public static Connection objectDb() throws SQLException {
		Connection c = DriverManager.getConnection("jdbc:hsqldb:mem:mymemdb", "SA", "");
		
		if (!objectDb) {
			Statement st = c.createStatement();
			
			try {
				st.execute("create table test_db_object("
						+ " id bigint not null primary key,"
						+ " name varchar(100), "
						+ " email varchar(100),"
						+ " creation_Time datetime  )");

				st.execute("insert into test_db_object values(1, 'name 1', 'name1@mail.com', TIMESTAMP'2014-03-04 11:10:03')");
			} finally {
				st.close();
			}
		}
	
		
		objectDb = true;
		return c;
	}

}
