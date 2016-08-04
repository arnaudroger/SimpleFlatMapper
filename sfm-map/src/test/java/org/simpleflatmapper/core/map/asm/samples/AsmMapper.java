package org.simpleflatmapper.core.map.asm.samples;

import org.simpleflatmapper.core.map.FieldMapper;
import org.simpleflatmapper.core.map.MappingContext;
import org.simpleflatmapper.core.map.context.MappingContextFactory;
import org.simpleflatmapper.core.map.fieldmapper.FieldMapperImpl;
import org.simpleflatmapper.core.map.fieldmapper.IntFieldMapper;
import org.simpleflatmapper.core.map.fieldmapper.LongFieldMapper;
import org.simpleflatmapper.core.map.mapper.AbstractMapper;
import org.simpleflatmapper.core.reflect.Instantiator;
import org.simpleflatmapper.test.beans.DbObject;

import java.io.InputStream;

public final class AsmMapper extends AbstractMapper<InputStream, DbObject> {

	private final LongFieldMapper<InputStream, DbObject> mapper1;
	private final IntFieldMapper<InputStream, DbObject> mapper2;
	private final FieldMapperImpl<InputStream, DbObject, ?> mapper3;
	private final FieldMapperImpl<InputStream, DbObject, ?> mapper4;
	
	@SuppressWarnings("unchecked")
	public AsmMapper(FieldMapper<InputStream, DbObject>[] mappers, FieldMapper<InputStream, DbObject>[] mappers2, Instantiator<InputStream, DbObject> instantiator, MappingContextFactory<InputStream> mappingContextFactory) {
		super(instantiator);
		mapper1 = (LongFieldMapper<InputStream, DbObject>) mappers[0];
		mapper2 = (IntFieldMapper<InputStream, DbObject>) mappers[2];
		mapper3 = (FieldMapperImpl<InputStream, DbObject, ?>) mappers[3];
		mapper4 = (FieldMapperImpl<InputStream, DbObject, ?>) mappers2[0];
	}
	
	protected final void mapFields(InputStream source, final DbObject target, MappingContext<? super InputStream> mappingContext) throws Exception {
		mapper1.mapTo(source, target, mappingContext);
		mapper2.mapTo(source, target, mappingContext);
		mapper3.mapTo(source, target, mappingContext);
		mapper4.mapTo(source, target, mappingContext);
	}

    @Override
    protected final void mapToFields(InputStream source, DbObject target, MappingContext<? super InputStream> mappingContext) throws Exception {
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
