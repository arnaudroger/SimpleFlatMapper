package org.sfm.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.map.Mapper;
import org.sfm.utils.Handler;

public class ResultSetMapperFactoryTest {

	@Test
	public void testDbObjectMappingFromDbWithMetaData()
			throws SQLException, Exception, ParseException {
		DbHelper.testDbObjectFromDb(new Handler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				ResultSet rs = ps.executeQuery();
				Mapper<ResultSet, DbObject> mapper = ResultSetMapperFactory.newMapper(DbObject.class, rs.getMetaData());
				rs.next();
				DbObject dbObject = new DbObject();
				mapper.map(rs, dbObject);
				DbHelper.assertDbObjectMapping(dbObject);
			}
		});
	}
	
}
