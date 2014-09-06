package org.sfm.jdbc;

import static org.junit.Assert.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.Test;
import org.sfm.beans.DbListObject;
import org.sfm.utils.Handler;

public class JdbcMapperListTest {


	@Test
	public void testMapInnerObjectWithStaticMapper() throws Exception {
		DbHelper.testQuery(new Handler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement t) throws Exception {
				ResultSet rs = t.executeQuery();
				final JdbcMapper<DbListObject> mapper =  
						new ResultSetMapperBuilderImpl<DbListObject>(DbListObject.class)
							.addMapping(rs.getMetaData())
							.mapper();
				
				rs.next();
				
				DbListObject object = mapper.map(rs);
				
				assertEquals(12, object.getId());
				assertEquals(2, object.getObjects().size());
				DbHelper.assertDbObjectMapping(object.getObjects().get(0));
				DbHelper.assertDbObjectMapping(object.getObjects().get(1));
			}
		}, "select 12 as id, "
				+ " 1 as objects_1_id, 'name 1' as objects_1_name, 'name1@mail.com' as objects_1_email, TIMESTAMP'2014-03-04 11:10:03' as objects_1_creation_time, 2 as objects_1_type_ordinal, 'type4' as objects_1_type_name, "
				+ " 1 as objects_2_id, 'name 1' as objects_2_name, 'name1@mail.com' as objects_2_email, TIMESTAMP'2014-03-04 11:10:03' as objects_2_creation_time, 2 as objects_2_type_ordinal, 'type4' as objects_2_type_name "
				+ " from TEST_DB_OBJECT ");
	}
	
}
