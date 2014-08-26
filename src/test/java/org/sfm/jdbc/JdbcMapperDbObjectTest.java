package org.sfm.jdbc;

import static org.junit.Assert.assertEquals;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.beans.FinalDbObject;
import org.sfm.utils.Handler;
import org.sfm.utils.ListHandler;

public class JdbcMapperDbObjectTest {
	
	@Test
	public void testPropertyToColumnMapping()
			throws SQLException, Exception, ParseException {
		
		ResultSetMapperBuilder<DbObject> builder = new ResultSetMapperBuilderImpl<DbObject>(DbObject.class);
		
		builder.addMapping("id", "id");
		builder.addMapping("name", "name");
		builder.addMapping("email", "email");
		builder.addMapping("creationTime", "creation_time");
		builder.addMapping("typeOrdinal", "type_ordinal");
		builder.addMapping("typeName", "type_name");
		
		final JdbcMapper<DbObject> mapper = builder.mapper();
		testDbObjectMapper(mapper);
	}

	@Test
	public void testColumn() throws Exception {
		ResultSetMapperBuilder<DbObject> builder = new ResultSetMapperBuilderImpl<DbObject>(DbObject.class);
		
		builder.addNamedColumn("id");
		builder.addNamedColumn("name");
		builder.addNamedColumn("email");
		builder.addNamedColumn("creation_time");
		builder.addNamedColumn("type_ordinal");
		builder.addNamedColumn("type_name");
		
		final JdbcMapper<DbObject> mapper = builder.mapper();
		
		testDbObjectMapper(mapper);
	}
	
	@Test
	public void testColumnFinalProperty() throws Exception {
		ResultSetMapperBuilder<FinalDbObject> builder = new ResultSetMapperBuilderImpl<FinalDbObject>(FinalDbObject.class);
		
		builder.addNamedColumn("id");
		builder.addNamedColumn("name");
		builder.addNamedColumn("email");
		builder.addNamedColumn("creation_time");
		builder.addNamedColumn("type_ordinal");
		builder.addNamedColumn("type_name");
		
		final JdbcMapper<FinalDbObject> mapper = builder.mapper();
		
		DbHelper.testDbObjectFromDb(new Handler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				List<FinalDbObject> objects = mapper.forEach(ps.executeQuery(), new ListHandler<FinalDbObject>()).getList();
				assertEquals(1, objects.size());
				DbHelper.assertDbObjectMapping(objects.get(0));
			}
		});
	}
	private void testDbObjectMapper(final JdbcMapper<DbObject> mapper)
			throws SQLException, Exception, ParseException {
		DbHelper.testDbObjectFromDb(new Handler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				List<DbObject> objects = mapper.forEach(ps.executeQuery(), new ListHandler<DbObject>()).getList();
				assertEquals(1, objects.size());
				DbHelper.assertDbObjectMapping(objects.get(0));
			}
		});
	}
}