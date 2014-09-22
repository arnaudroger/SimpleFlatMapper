package org.sfm.querydsl;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.jdbc.DbHelper;
import org.sfm.jdbc.querydsl.QueryDslMappingProjection;
import org.springframework.dao.DataAccessException;

import com.mysema.query.sql.HSQLDBTemplates;
import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.SQLQueryImpl;

public class TestQueryDSL {
	QTestDbObject qTestDbObject = new QTestDbObject("o");
	
	@Test
	public void testMappingProjection()
	        throws DataAccessException, SQLException, ParseException {
		
		
		Connection conn = DbHelper.objectDb();
		
		SQLQuery sqlquery = new SQLQueryImpl(conn, new HSQLDBTemplates());
		try {
			 List<DbObject> list = sqlquery.from(qTestDbObject).where(qTestDbObject.id.eq(1l)).list(new QueryDslMappingProjection<DbObject>(DbObject.class, qTestDbObject.id,
					qTestDbObject.name, qTestDbObject.email, qTestDbObject.creationTime, qTestDbObject.typeName, qTestDbObject.typeOrdinal ));
			 
			 assertEquals(1, list.size());
			 DbHelper.assertDbObjectMapping(list.get(0));
		} finally {
			conn.close();
		}
	}

}
