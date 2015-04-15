package org.sfm.map.column;

import org.sfm.map.FieldMapper;

public class FieldMapperProperty implements ColumnProperty {
    private final FieldMapper<?, ?> fieldMapper;

    public FieldMapperProperty(FieldMapper<?, ?> fieldMapper) {
        if (fieldMapper == null) throw new NullPointerException();
        this.fieldMapper = fieldMapper;
    }

    public FieldMapper<?, ?> getFieldMapper() {
        return fieldMapper;
    }

    @Override
    public String toString() {
        return "FieldMapper{FieldMapper}";
    }
}
