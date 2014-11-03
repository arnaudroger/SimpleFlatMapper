package org.sfm.reflect.asm.sample;

import java.sql.ResultSet;

import org.sfm.beans.DbObject;
import org.sfm.jdbc.impl.AbstractJdbcMapperImpl;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.map.impl.FieldMapper;
import org.sfm.map.impl.fieldmapper.FieldMapperImpl;
import org.sfm.map.impl.fieldmapper.IntFieldMapper;
import org.sfm.map.impl.fieldmapper.LongFieldMapper;
import org.sfm.reflect.Instantiator;

public final class AsmJdbcMapper extends AbstractJdbcMapperImpl<DbObject> {

	private final LongFieldMapper<ResultSet, DbObject> mapper1;
	private final IntFieldMapper<ResultSet, DbObject> mapper2;
	private final FieldMapperImpl<ResultSet, DbObject, ?> mapper3;
	private final FieldMapperImpl<ResultSet, DbObject, ?> mapper4;
	
	@SuppressWarnings("unchecked")
	public AsmJdbcMapper(FieldMapper<ResultSet, DbObject>[] mappers, Instantiator<ResultSet, DbObject> instantiator, RowHandlerErrorHandler errorHandler) {
		super(instantiator, errorHandler);
		mapper1 = (LongFieldMapper<ResultSet, DbObject>) mappers[0];
		mapper2 = (IntFieldMapper<ResultSet, DbObject>) mappers[2];
		mapper3 = (FieldMapperImpl<ResultSet, DbObject, ?>) mappers[3];
		mapper4 = (FieldMapperImpl<ResultSet, DbObject, ?>) mappers[4];
	}
	
	protected final void mapFields(ResultSet source, final DbObject target) throws Exception {
		mapper1.map(source, target);
		mapper2.map(source, target);
		mapper3.map(source, target);
		mapper4.map(source, target);
	}
}
