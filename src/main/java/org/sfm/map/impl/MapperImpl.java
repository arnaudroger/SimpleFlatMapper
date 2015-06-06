package org.sfm.map.impl;

import org.sfm.map.FieldMapper;
import org.sfm.map.MappingContext;
import org.sfm.reflect.Instantiator;

import java.util.Arrays;

public class MapperImpl<S, T> extends AbstractMapper<S, T> {
	
	private final FieldMapper<S, T>[] fieldMappers;

    private final FieldMapper<S, T>[] constructorMappers;

    public MapperImpl(final FieldMapper<S, T>[] fieldMappers,
                      final FieldMapper<S, T>[] constructorMappers,
                      final Instantiator<S, T> instantiator) {
		super(instantiator);
		this.fieldMappers = fieldMappers;
        this.constructorMappers = constructorMappers;
	}



    protected final void mapFields(final S source, final T target, final MappingContext<S> mappingContext) throws Exception {
        for (FieldMapper<S, T> fieldMapper : fieldMappers) {
            fieldMapper.mapTo(source, target, mappingContext);
        }
	}

    @Override
    protected final void mapToFields(S source, T target, final MappingContext<S> mappingContext) throws Exception {
        for (FieldMapper<S, T> constructorMapper : constructorMappers) {
            constructorMapper.mapTo(source, target, mappingContext);
        }

        mapFields(source, target, mappingContext);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(getClass().getSimpleName())
                .append("{");

        super.appendToStringBuilder(sb);
        sb.append(", fieldMappers=").append(Arrays.toString(fieldMappers));
        sb.append("}");

        return sb.toString();
    }
}
