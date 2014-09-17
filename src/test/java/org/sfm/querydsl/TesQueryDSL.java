package org.sfm.querydsl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.sfm.beans.DbObject;
import org.sfm.jdbc.DbHelper;
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
		
		SQLQuery sqlquery = new SQLQueryImpl(new HSQLDBTemplates());
		
		Connection conn = DbHelper.objectDb();
		
		try {
			return sqlquery.from(qTestDbObject).where(qTestDbObject.id.eq(1l)).clone(conn).list(new MappingProductProjection(qTestDbObject));
		} finally {
			conn.close();
		}
	}

	private class MappingProductProjection extends MappingProjection<DbObject> {

	    public MappingProductProjection(QTestDbObject qProduct) {
	        super(DbObject.class, qProduct.id,
	            qProduct.name, qProduct.email, qProduct.creationTime);
	    }

	    @Override
	    protected DbObject map(Tuple tuple) {
	    	DbObject product = new DbObject();

	        product.setId(tuple.get(qTestDbObject.id));
	        product.setName(tuple.get(qTestDbObject.name));
	        product.setEmail(tuple.get(qTestDbObject.email));
	        product.setCreationTime(tuple.get(qTestDbObject.creationTime));

	        return product;
	    }
	}
	
	public static void main(String[] args) throws DataAccessException, SQLException {
		System.out.println(new TesQueryDSL().getProductListByCategory());
	}
}
