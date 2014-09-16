package org.sfm.querydsl;

import java.sql.SQLException;
import java.util.List;

import org.sfm.beans.DbObject;
import org.sfm.jdbc.DbHelper;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jdbc.query.QueryDslJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.mysema.query.Tuple;
import com.mysema.query.sql.SQLQuery;
import com.mysema.query.types.MappingProjection;

public class TesQueryDSL {
	QTestDbObject qTestDbObject = new QTestDbObject("o");
	
	public List<DbObject> getProductListByCategory()
	        throws DataAccessException, SQLException {

		QueryDslJdbcTemplate template = new QueryDslJdbcTemplate(new SingleConnectionDataSource(DbHelper.objectDb(), true));
		
	    SQLQuery sqlQuery = template.newSqlQuery().from(qTestDbObject)
	            .where(qTestDbObject.id.eq(1l));

	    return template.query(sqlQuery, new MappingProductProjection(qTestDbObject));
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
