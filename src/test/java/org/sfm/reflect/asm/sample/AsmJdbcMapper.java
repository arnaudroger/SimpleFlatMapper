package org.sfm.reflect.asm.sample;

import org.sfm.beans.DbObject;
import org.sfm.map.MappingContext;
import org.sfm.map.MappingContextFactory;
import org.sfm.map.FieldMapper;
import org.sfm.map.impl.AbstractMapper;
import org.sfm.map.impl.fieldmapper.FieldMapperImpl;
import org.sfm.map.impl.fieldmapper.IntFieldMapper;
import org.sfm.map.impl.fieldmapper.LongFieldMapper;
import org.sfm.reflect.Instantiator;

import java.sql.ResultSet;

public final class AsmJdbcMapper extends AbstractMapper<ResultSet, DbObject> {

	private final LongFieldMapper<ResultSet, DbObject> mapper1;
	private final IntFieldMapper<ResultSet, DbObject> mapper2;
	private final FieldMapperImpl<ResultSet, DbObject, ?> mapper3;
	private final FieldMapperImpl<ResultSet, DbObject, ?> mapper4;
	
	@SuppressWarnings("unchecked")
	public AsmJdbcMapper(FieldMapper<ResultSet, DbObject>[] mappers, FieldMapper<ResultSet, DbObject>[] mappers2, Instantiator<ResultSet, DbObject> instantiator, MappingContextFactory<ResultSet> mappingContextFactory) {
		super(instantiator, mappingContextFactory);
		mapper1 = (LongFieldMapper<ResultSet, DbObject>) mappers[0];
		mapper2 = (IntFieldMapper<ResultSet, DbObject>) mappers[2];
		mapper3 = (FieldMapperImpl<ResultSet, DbObject, ?>) mappers[3];
		mapper4 = (FieldMapperImpl<ResultSet, DbObject, ?>) mappers2[0];
	}
	
	protected final void mapFields(ResultSet source, final DbObject target, MappingContext<ResultSet> mappingContext) throws Exception {
		mapper1.mapTo(source, target, mappingContext);
		mapper2.mapTo(source, target, mappingContext);
		mapper3.mapTo(source, target, mappingContext);
		mapper4.mapTo(source, target, mappingContext);
	}

    @Override
    protected final void mapToFields(ResultSet source, DbObject target, MappingContext<ResultSet> mappingContext) throws Exception {
        mapFields(source, target, mappingContext);
    }

    @Override
    public String toString() {
        return "AsmJdbcMapper{" +
                "mapper1=" + mapper1 +
                ", mapper2=" + mapper2 +
                ", mapper3=" + mapper3 +
                ", mapper4=" + mapper4 +
                '}';
    }
}
