package org.sfm.jooq;

import java.lang.reflect.Type;

import org.jooq.Record;
import org.sfm.map.Mapper;
import org.sfm.map.MapperBuildingException;
import org.sfm.map.impl.AbstractFieldMapperMapperBuilder;
import org.sfm.map.impl.MapperImpl;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;


public class JooqMapperBuilder<R extends Record, E> extends
		AbstractFieldMapperMapperBuilder<R, E, JooqFieldKey> {

	public JooqMapperBuilder(final Type target) throws MapperBuildingException {
		this(target, new ReflectionService());
	}
	
	@SuppressWarnings("unchecked")
	public JooqMapperBuilder(final Type target, ReflectionService reflectService) throws MapperBuildingException {
		this(target, (ClassMeta<E>) reflectService.getClassMeta(target));
	}
	
	public JooqMapperBuilder(final Type target, final ClassMeta<E> classMeta) throws MapperBuildingException {
		super(target, Record.class, classMeta, new RecordGetterFactory<R>(), new RecordFieldMapperFactory<R>(new RecordGetterFactory<R>()), null, null);
	}

	@Override
	protected <ST> AbstractFieldMapperMapperBuilder<R, ST, JooqFieldKey> newSubBuilder(Type type, ClassMeta<ST> classMeta) {
		return new JooqMapperBuilder<R, ST>(type, classMeta);
	}
	
	public JooqMapperBuilder<R, E> addField(JooqFieldKey key) {
		super.addMapping(key);
		return this;
	}
	
	@Override
	public Mapper<R, E> mapper() {
		return new MapperImpl<R, E>(fields(), getInstantiator());
	}

}
