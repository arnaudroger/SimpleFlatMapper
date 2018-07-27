package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.SourceFieldMapper;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.util.Function;

public class DefaultTransfromerSetRowMapperBuilder<ROW, SET, I, O, K extends FieldKey<K>, E extends Exception> 
        extends TransfromerSetRowMapperBuilder<SetRowMapper<ROW, SET, O, E>, SetRowMapper<ROW, SET, I, E>, ROW, SET, I, O, K, E> {


    public DefaultTransfromerSetRowMapperBuilder(
            final SetRowMapperBuilder<SetRowMapper<ROW, SET, I, E>, ROW, SET, I, K, E> delegate, 
            final Function<I, O> transformer) {
        super(delegate, new Function<SetRowMapper<ROW, SET, I, E>, SetRowMapper<ROW, SET, O, E>>() {
            @Override
            public SetRowMapper<ROW, SET, O, E> apply(SetRowMapper<ROW, SET, I, E> setRowMapper) {
                return new TransformSetRowMapper<ROW, SET, I, O, E>(setRowMapper, transformer);
            }
        });
    }
}
