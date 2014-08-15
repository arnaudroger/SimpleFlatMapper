package org.sfm.reflect.asm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.jdbc.AbstractResultSetMapperBuilder;
import org.sfm.jdbc.DbHelper;
import org.sfm.jdbc.ResultSetMapperBuilderImpl;
import org.sfm.map.Mapper;
import org.sfm.reflect.SetterFactory;
import org.sfm.utils.Handler;

public class AsmMapperTest {

	@Test
	public void testMapperBuilder() throws Exception {
		AsmFactory factory = new AsmFactory();
		
		AbstractResultSetMapperBuilder<DbObject> builder = new ResultSetMapperBuilderImpl<>(DbObject.class, new SetterFactory());
		builder.addIndexedColumn("id");
		builder.addIndexedColumn("name");
		builder.addIndexedColumn("email");
		builder.addIndexedColumn("creation_time");
		
		final Mapper<ResultSet, DbObject> mapper = factory.createMapper(builder.fields(), ResultSet.class, DbObject.class);
		DbHelper.testDbObjectFromDb(new Handler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				ResultSet rs = ps.executeQuery();
				rs.next();
				DbObject object = new DbObject();
				mapper.map(rs, object);
				DbHelper.assertDbObjectMapping(object);
			}
		});
	}

}
