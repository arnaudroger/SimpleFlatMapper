package org.sfm.querydsl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.sfm.beans.DbObject;
import org.sfm.jdbc.DbHelper;
import org.sfm.jdbc.querydsl.QueryDslMappingProjection;
import org.springframework.dao.DataAccessException;

import com.mysema.query.Tuple;
import com.mysema.query.sql.HSQLDBTemplates;
import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.SQLQueryImpl;
import com.mysema.query.types.MappingProjection;

public class TesQueryDSL {
	QTestDbObject qTestDbObject = new QTestDbObject("o");
	
	public List<DbObject> getProductListByCategory()
	        throws DataAccessException, SQLException {
		
		
		Connection conn = DbHelper.objectDb();
		
		SQLQuery sqlquery = new SQLQueryImpl(conn, new HSQLDBTemplates());
		try {
			return sqlquery.from(qTestDbObject).where(qTestDbObject.id.eq(1l)).list(new QueryDslMappingProjection<DbObject>(DbObject.class, qTestDbObject.id,
					qTestDbObject.name, qTestDbObject.email, qTestDbObject.creationTime, qTestDbObject.typeName, qTestDbObject.typeOrdinal ));
		} finally {
			conn.close();
		}
	}

	public static void main(String[] args) throws DataAccessException, SQLException {
		System.out.println(new TesQueryDSL().getProductListByCategory());
	}
}
