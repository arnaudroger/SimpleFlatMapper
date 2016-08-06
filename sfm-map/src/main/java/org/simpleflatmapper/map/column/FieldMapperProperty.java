package org.simpleflatmapper.map.column;

import org.simpleflatmapper.map.FieldMapper;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public class FieldMapperProperty implements ColumnProperty {
    private final FieldMapper<?, ?> fieldMapper;

    public FieldMapperProperty(FieldMapper<?, ?> fieldMapper) {
        this.fieldMapper = requireNonNull("fieldMapper", fieldMapper);
    }

    public FieldMapper<?, ?> getFieldMapper() {
        return fieldMapper;
    }

    @Override
    public String toString() {
        return "FieldMapper{FieldMapper}";
    }
}
