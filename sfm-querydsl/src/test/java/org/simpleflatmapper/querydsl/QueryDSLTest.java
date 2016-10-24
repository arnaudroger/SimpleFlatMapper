package org.simpleflatmapper.querydsl;

import com.mysema.query.sql.HSQLDBTemplates;
import com.mysema.query.sql.SQLQuery;
import org.junit.Test;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.jdbc.DbHelper;

import java.sql.Connection;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class QueryDSLTest {
	QTestDbObject qTestDbObject = new QTestDbObject("o");
	
	@Test
	public void testMappingProjection()
	        throws Exception {
		
		
		Connection conn = DbHelper.objectDb();
		
		SQLQuery sqlquery = new SQLQuery(conn, new HSQLDBTemplates());
		try {
			 List<DbObject> list =
					 sqlquery
							 .from(qTestDbObject)
							 .where(qTestDbObject.id.eq(1l))
							 .list(
							 		new QueryDslMappingProjection<DbObject>(
							 				DbObject.class,
											qTestDbObject.id, qTestDbObject.name, qTestDbObject.email,
											qTestDbObject.creationTime, qTestDbObject.typeName, qTestDbObject.typeOrdinal ));
			 
			 assertEquals(1, list.size());
			 DbHelper.assertDbObjectMapping(list.get(0));
		} finally {
			conn.close();
		}
	}

}
