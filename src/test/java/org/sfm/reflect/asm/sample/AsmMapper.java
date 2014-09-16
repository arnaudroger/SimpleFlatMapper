package org.sfm.reflect.asm.sample;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.beans.DbObject;
import org.sfm.jdbc.ColumnKey;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.jdbc.JdbcMapperErrorHandler;
import org.sfm.map.FieldMapper;
import org.sfm.map.FieldMapperImpl;
import org.sfm.map.InstantiationMappingException;
import org.sfm.map.MappingException;
import org.sfm.map.primitive.IntFieldMapper;
import org.sfm.map.primitive.LongFieldMapper;
import org.sfm.reflect.Instantiator;
import org.sfm.utils.RowHandler;

public final class AsmMapper implements JdbcMapper<DbObject>{

	private final LongFieldMapper<ResultSet, DbObject, ColumnKey> mapper1;
	private final IntFieldMapper<ResultSet, DbObject, ColumnKey> mapper2;
	private final FieldMapperImpl<ResultSet, DbObject, ?, ColumnKey> mapper3;
	private final FieldMapperImpl<ResultSet, DbObject, ?, ColumnKey> mapper4;
	
	private final Instantiator<ResultSet, DbObject> instantiator;
	
	private final JdbcMapperErrorHandler errorHandler;

	@SuppressWarnings("unchecked")
	public AsmMapper(FieldMapper<ResultSet, DbObject>[] mappers, Instantiator<ResultSet, DbObject> instantiator, JdbcMapperErrorHandler errorHandler) {
		mapper1 = (LongFieldMapper<ResultSet, DbObject, ColumnKey>) mappers[0];
		mapper2 = (IntFieldMapper<ResultSet, DbObject, ColumnKey>) mappers[2];
		mapper3 = (FieldMapperImpl<ResultSet, DbObject, ?, ColumnKey>) mappers[3];
		mapper4 = (FieldMapperImpl<ResultSet, DbObject, ?, ColumnKey>) mappers[4];
		this.instantiator = instantiator;
		this.errorHandler = errorHandler;
	}
	
	@Override
	public DbObject map(ResultSet source) throws MappingException {
		final DbObject target;
		try {
			target = instantiator.newInstance(source);
		} catch(Exception e) {
			throw new InstantiationMappingException(e.getMessage(), e);
		}
		mapFields(source, target);
		return target;
	}

	private void mapFields(ResultSet source, final DbObject target) {
		mapper1.map(source, target);
		mapper2.map(source, target);
		mapper3.map(source, target);
		mapper4.map(source, target);
	}

	@Override
	public <H extends RowHandler<DbObject>> H forEach(ResultSet rs, H handler)
			throws MappingException, SQLException {
		while(rs.next()) {
			DbObject t = map(rs);
			try {
				handler.handle(t);
			} catch(Throwable error) {
				errorHandler.handlerError(error, t);
			}
		}
		return handler;
	}


}
