package org.sfm.poi;


import org.apache.poi.ss.usermodel.Row;
import org.sfm.map.Mapper;
import org.sfm.map.MappingContext;
import org.sfm.map.MappingException;

public class PoiMapper<T> implements Mapper<Row, T> {

    private final Mapper<Row, T> mapper;

    public PoiMapper(Mapper<Row, T> mapper) {
        this.mapper = mapper;
    }

    @Override
    public T map(Row source) throws MappingException {
        return mapper.map(source);
    }

    @Override
    public T map(Row source, MappingContext<Row> context) throws MappingException {
        return mapper.map(source, context);
    }

    @Override
    public MappingContext<Row> newMappingContext(Row source) throws MappingException {
        return mapper.newMappingContext(source);
    }

    @Override
    public void mapTo(Row source, T target, MappingContext<Row> context) throws Exception {
        mapper.mapTo(source, target, context);
    }
}
