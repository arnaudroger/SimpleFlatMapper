package org.sfm.reflect.asm.sample;

import java.sql.ResultSet;

import org.sfm.beans.DbObject;
import org.sfm.map.FieldMapper;
import org.sfm.map.Mapper;
import org.sfm.map.primitive.IntFieldMapper;
import org.sfm.map.primitive.LongFieldMapper;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.IntGetter;
import org.sfm.reflect.primitive.IntSetter;
import org.sfm.reflect.primitive.LongGetter;
import org.sfm.reflect.primitive.LongSetter;

public class AsmMapper implements Mapper<ResultSet, DbObject>{

	private final LongGetter<ResultSet> getter1;
	private final LongSetter<DbObject> setter1;

	private final IntGetter<ResultSet> getter2;
	private final IntSetter<DbObject> setter2;
	
	private final Getter<ResultSet, String> getter3;
	private final Setter<DbObject, String> setter3;

	private final Getter<ResultSet, String> getter4;
	private final Setter<DbObject, String> setter4;

	public AsmMapper(Mapper<ResultSet, DbObject>[] mappers) {
		getter1 = ((LongFieldMapper)mappers[0]).getGetter();
		setter1 = ((LongFieldMapper)mappers[0]).getSetter();
		getter2 = ((IntFieldMapper)mappers[1]).getGetter();
		setter2 = ((IntFieldMapper)mappers[1]).getSetter();
		getter3 = ((FieldMapper)mappers[2]).getGetter();
		setter3 = ((FieldMapper)mappers[2]).getSetter();
		getter4 = ((FieldMapper)mappers[6]).getGetter();
		setter4 = ((FieldMapper)mappers[6]).getSetter();
	}
	
	@Override
	public void map(ResultSet source, DbObject target) throws Exception {
		//setter1.setLong(target, getter1.getLong(source));
		setter2.setInt(target, getter2.getInt(source));
		//setter3.set(target, getter3.get(source));
		//setter4.set(target, getter4.get(source));
	}

}
