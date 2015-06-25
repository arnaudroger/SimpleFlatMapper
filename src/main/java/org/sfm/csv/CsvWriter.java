package org.sfm.csv;

import org.sfm.csv.impl.writer.CellWriter;
import org.sfm.csv.impl.writer.CsvCellWriter;
import org.sfm.map.Mapper;
import org.sfm.map.MappingContext;
import org.sfm.map.impl.ContextualMapper;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.utils.ErrorHelper;

import java.io.IOException;

public class CsvWriter<T>  {

    private final Mapper<T, Appendable> mapper;
    private final Appendable appendable;
    private final MappingContext<T> mappingContext;

    public CsvWriter(Mapper<T, Appendable> mapper, Appendable appendable, MappingContext<T> mappingContext) {
        this.mapper = mapper;
        this.appendable = appendable;
        this.mappingContext = mappingContext;
    }

    public void write(T value) {
        try {
            mapper.mapTo(value, appendable, mappingContext);
        } catch(Exception e) {
            ErrorHelper.rethrow(e);
        }
    }

    public static <T> MapDSL<T> map(Class<T> clazz) {

        ClassMeta<T> classMeta = ReflectionService.newInstance().getClassMeta(clazz);

        CellWriter cellWriter = CsvCellWriter.DEFAULT_WRITER;

        ContextualMapper<T, Appendable> mapper =
                CsvWriterBuilder
                        .newBuilder(classMeta, cellWriter)
                        .defaultHeaders()
                        .mapper();

        return new MapDSL<T>(classMeta.generateHeaders(),cellWriter, mapper);
    }

    public static class MapDSL<T> {

        private final String[] headers;
        private final ContextualMapper<T, Appendable> mapper;
        private final CellWriter cellWriter;

        public MapDSL(String[] headers, CellWriter cellWriter, ContextualMapper<T, Appendable> mapper) {
            this.headers = headers;
            this.mapper = mapper;
            this.cellWriter = cellWriter;
        }

        public CsvWriter<T> writeTo(Appendable appendable) throws IOException {
            for(int i = 0; i < headers.length; i++) {
                if (i != 0) {
                    cellWriter.nextCell(appendable);
                }
                cellWriter.writeValue(headers[i], appendable);

            }
            cellWriter.endOfRow(appendable);

            return new CsvWriter<T>(mapper, appendable, mapper.newMappingContext());
        }
    }

}
