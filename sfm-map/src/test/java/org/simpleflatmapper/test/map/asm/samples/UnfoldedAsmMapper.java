package org.simpleflatmapper.test.map.asm.samples;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.fieldmapper.ConstantSourceFieldMapper;
import org.simpleflatmapper.map.fieldmapper.ConstantTargetFieldMapper;
import org.simpleflatmapper.map.fieldmapper.IntConstantTargetFieldMapper;
import org.simpleflatmapper.map.fieldmapper.LongConstantTargetFieldMapper;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.mapper.AbstractMapper;
import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.test.beans.DbObject;

import java.io.InputStream;

public final class UnfoldedAsmMapper extends AbstractMapper<InputStream, DbObject> {

	private final LongConstantTargetFieldMapper<InputStream, DbObject> mapper1;
	private final IntConstantTargetFieldMapper<InputStream, DbObject> mapper2;
	private final int mapperGetterIndex3;
	private final Setter<? super DbObject, Object> mapperSetter3;
	private final ConstantTargetFieldMapper<InputStream, DbObject, ?> mapper4;
	
	@SuppressWarnings("unchecked")
	public UnfoldedAsmMapper(FieldMapper<InputStream, DbObject>[] mappers, FieldMapper<InputStream, DbObject>[] mappers2, BiInstantiator<InputStream, MappingContext<? super InputStream>, DbObject> instantiator) {
		super(instantiator);
		mapper1 = (LongConstantTargetFieldMapper<InputStream, DbObject>) mappers[0];
		mapper2 = (IntConstantTargetFieldMapper<InputStream, DbObject>) mappers[2];
		mapperGetterIndex3 = 3;
		ConstantSourceFieldMapper<InputStream, DbObject, ?> mapper = (ConstantSourceFieldMapper<InputStream, DbObject, ?>) mappers[3];
		mapperSetter3 = (Setter<? super DbObject, Object>) mapper.setter;
		mapper4 = (ConstantTargetFieldMapper<InputStream, DbObject, ?>) mappers2[0];
	}
	
	protected final void mapFields(InputStream source, final DbObject target, MappingContext<? super InputStream> mappingContext) throws Exception {
		mapper1.mapTo(source, target, mappingContext);
		mapper2.mapTo(source, target, mappingContext);
		mapperSetter3.set(target, MyGetter.get(source, mappingContext, 3));
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
                ", mapper4=" + mapper4 +
                '}';
    }
    
    
    public static class MyGetter implements ContextualGetter<InputStream, Object> {

		public final int index;

		public MyGetter(int index) {
			this.index = index;
		}

		@Override
		public Object get(InputStream target, Context context) throws Exception {
			return null;
		}
		
		public static Object get(InputStream target, Context context, int index) {
			return null;
		}
	}
}
