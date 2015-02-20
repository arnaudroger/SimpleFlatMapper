package org.sfm.map.impl;

import org.sfm.reflect.Instantiator;

import java.util.Arrays;

public class MapperImpl<S, T> extends AbstractMapperImpl<S, T> {
	
	private final FieldMapper<S, T>[] fieldMappers;

    private final FieldMapper<S, T>[] constructorMappers;

    public MapperImpl(final FieldMapper<S, T>[] fieldMappers, final FieldMapper<S, T>[] constructorMappers, final Instantiator<S, T> instantiator) {
		super(instantiator);
		this.fieldMappers = fieldMappers;
        this.constructorMappers = constructorMappers;
	}

	protected final void mapFields(final S source, final T target) throws Exception {
        for (FieldMapper<S, T> fieldMapper : fieldMappers) {
            fieldMapper.map(source, target);
        }
	}

    @Override
    protected final void mapToFields(S source, T target) throws Exception {
        for (FieldMapper<S, T> constructorMapper : constructorMappers) {
            constructorMapper.map(source, target);
        }

        mapFields(source, target);
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
