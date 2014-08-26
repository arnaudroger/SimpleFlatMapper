package org.sfm.reflect.asm.sample;

import java.sql.ResultSet;

import org.sfm.beans.DbObject;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.map.FieldMapper;
import org.sfm.map.FieldMapperImpl;
import org.sfm.map.primitive.IntFieldMapper;
import org.sfm.map.primitive.LongFieldMapper;
import org.sfm.reflect.Instantiator;
import org.sfm.utils.Handler;

public final class AsmMapper implements JdbcMapper<DbObject>{

	private final LongFieldMapper<ResultSet, DbObject> mapper1;
	private final IntFieldMapper<ResultSet, DbObject> mapper2;
	private final FieldMapperImpl<ResultSet, DbObject, ?> mapper3;
	private final FieldMapperImpl<ResultSet, DbObject, ?> mapper4;
	
	private final Instantiator<ResultSet, DbObject> instantiator;

	@SuppressWarnings("unchecked")
	public AsmMapper(FieldMapper<ResultSet, DbObject>[] mappers, Instantiator<ResultSet, DbObject> instantiator) {
		mapper1 = (LongFieldMapper<ResultSet, DbObject>) mappers[0];
		mapper2 = (IntFieldMapper<ResultSet, DbObject>) mappers[2];
		mapper3 = (FieldMapperImpl<ResultSet, DbObject, ?>) mappers[3];
		mapper4 = (FieldMapperImpl<ResultSet, DbObject, ?>) mappers[4];
		this.instantiator = instantiator;
	}
	
	@Override
	public DbObject map(ResultSet source) throws Exception {
		DbObject target = instantiator.newInstance(source);
		mapper1.map(source, target);
		mapper2.map(source, target);
		mapper3.map(source, target);
		mapper4.map(source, target);
		return target;
	}

	@Override
	public <H extends Handler<DbObject>> H forEach(ResultSet rs, H handler)
			throws Exception {
		while(rs.next()) {
			DbObject t = map(rs);
			handler.handle(t);
		}
		return handler;
	}


}
