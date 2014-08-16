package org.sfm.reflect.asm.sample;

import java.sql.ResultSet;

import org.sfm.beans.DbObject;
import org.sfm.map.FieldMapper;
import org.sfm.map.Mapper;
import org.sfm.map.primitive.IntFieldMapper;
import org.sfm.map.primitive.LongFieldMapper;
import org.sfm.reflect.primitive.LongGetter;
import org.sfm.reflect.primitive.LongSetter;

public class AsmMapper implements Mapper<ResultSet, DbObject>{

	private final LongFieldMapper<ResultSet, DbObject> mapper1;
	private final IntFieldMapper<ResultSet, DbObject> mapper2;
	private final FieldMapper<ResultSet, DbObject, ?> mapper3;
	private final FieldMapper<ResultSet, DbObject, ?> mapper4;

	public AsmMapper(Mapper<ResultSet, DbObject>[] mappers) {
		mapper1 = (LongFieldMapper<ResultSet, DbObject>) mappers[0];
		mapper2 = (IntFieldMapper<ResultSet, DbObject>) mappers[2];
		mapper3 = (FieldMapper<ResultSet, DbObject, ?>) mappers[3];
		mapper4 = (FieldMapper<ResultSet, DbObject, ?>) mappers[4];
	}
	
	@Override
	public void map(ResultSet source, DbObject target) throws Exception {
		mapper1.map(source, target);
		mapper2.map(source, target);
		mapper3.map(source, target);
		mapper4.map(source, target);
	}


}
