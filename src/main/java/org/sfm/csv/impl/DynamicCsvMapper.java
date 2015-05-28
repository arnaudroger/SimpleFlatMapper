package org.sfm.csv.impl;

import org.sfm.csv.*;
import org.sfm.csv.parser.CellConsumer;
import org.sfm.map.*;
import org.sfm.map.impl.*;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.PropertyNameMatcherFactory;
import org.sfm.utils.RowHandler;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Iterator;

//IFJAVA8_START
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
//IFJAVA8_END

import static org.sfm.utils.Asserts.requireNonNull;

public final class DynamicCsvMapper<T> implements CsvMapper<T> {
	
	private final ClassMeta<T> classMeta;

	private final Type target;

	private final String defaultDateFormat;

	private final CellValueReaderFactory cellValueReaderFactory;

	private final MapperConfig<CsvColumnKey, CsvColumnDefinition> mapperConfig;

	private final MapperCache<ColumnsMapperKey, CsvMapperImpl<T>> mapperCache = new MapperCache<ColumnsMapperKey, CsvMapperImpl<T>>();

	public DynamicCsvMapper(final Type target,
							final ClassMeta<T> classMeta,
							String defaultDateFormat,
							CellValueReaderFactory cellValueReaderFactory,
							MapperConfig<CsvColumnKey, CsvColumnDefinition> mapperConfig
    ) {
		this.classMeta = requireNonNull("classMeta", classMeta);
		this.target = requireNonNull("target", target);
		this.defaultDateFormat = defaultDateFormat;
		this.mapperConfig = mapperConfig;
		this.cellValueReaderFactory = cellValueReaderFactory;
	}

	public DynamicCsvMapper(Type target, ClassMeta<T> classMeta, ColumnDefinitionProvider<CsvColumnDefinition, CsvColumnKey> columnDefinitionProvider) {
		this(target, classMeta,  "yyyy-MM-dd HH:mm:ss", new CellValueReaderFactoryImpl(), MapperConfig.<CsvColumnKey, CsvColumnDefinition>config(columnDefinitionProvider));
	}

	@Override
	public <H extends RowHandler<? super T>> H forEach(Reader reader, H handle) throws IOException, MappingException {
		return forEach(CsvParser.reader(reader), handle);
	}

	@Override
	public <H extends RowHandler<? super T>> H forEach(CsvReader csvReader, H handle) throws IOException {
		CellConsumer mapperCellConsumer = getDelegateMapper(csvReader).newCellConsumer(handle);
		csvReader.parseAll(mapperCellConsumer);
		return handle;
	}

	@Override
	public <H extends RowHandler<? super T>> H forEach(Reader reader, H handle, int skip) throws IOException, MappingException {
		return forEach(CsvParser.skip(skip).reader(reader), handle);
	}

	@Override
	public <H extends RowHandler<? super T>> H forEach(CsvReader csvReader, H handle, int limit) throws IOException {
		CellConsumer mapperCellConsumer = getDelegateMapper(csvReader).newCellConsumer(handle);
		csvReader.parseRows(mapperCellConsumer, limit);
		return handle;
	}

	@Override
	public <H extends RowHandler<? super T>> H forEach(Reader reader, H handle, int skip, int limit) throws IOException, MappingException {
		return forEach(CsvParser.skip(skip).reader(reader), handle, limit);
	}

	@Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public Iterator<T> iterate(Reader reader) throws IOException {
		CsvReader csvReader = CsvParser.reader(reader);
		return iterate(csvReader);
	}

	@Override
    @Deprecated
    @SuppressWarnings("deprecation")
	public Iterator<T> iterate(CsvReader csvReader) throws IOException {
		CsvMapperImpl<T> mapper = getDelegateMapper(csvReader);
		return new CsvMapperIterator<T>(csvReader, mapper);
	}

	@Override
    @Deprecated
    @SuppressWarnings("deprecation")
	public Iterator<T> iterate(Reader reader, int skip) throws IOException {
		return iterate(CsvParser.skip(skip).reader(reader));
	}

	@SuppressWarnings("deprecation")
    @Override
	public Iterator<T> iterator(Reader reader) throws IOException {
		return iterate(reader);
	}

    @SuppressWarnings("deprecation")
	@Override
	public Iterator<T> iterator(CsvReader csvReader) throws IOException {
		return iterate(csvReader);
	}

    @SuppressWarnings("deprecation")
	@Override
	public Iterator<T> iterator(Reader reader, int skip) throws IOException {
		return iterate(reader, skip);
	}

	private CsvMapperImpl<T> getDelegateMapper(CsvReader reader) throws IOException {
		ColumnsMapperKeyBuilderCellConsumer keyBuilderCellConsumer = new ColumnsMapperKeyBuilderCellConsumer();
		reader.parseRow(keyBuilderCellConsumer);
		return getCsvMapper(keyBuilderCellConsumer.getKey());
	}


	//IFJAVA8_START
	@Override
	public Stream<T> stream(Reader reader) throws IOException {
		CsvReader csvReader = CsvParser.reader(reader);
		return stream(csvReader);
	}

	@Override
	public Stream<T> stream(CsvReader csvReader) throws IOException {
		CsvMapperImpl<T> mapper = getDelegateMapper(csvReader);
		return StreamSupport.stream(mapper.new CsvSpliterator(csvReader), false);
	}

	@Override
	public Stream<T> stream(Reader reader, int skip) throws IOException {
		return stream(CsvParser.skip(skip).reader(reader));
	}

	//IFJAVA8_END

	protected CsvMapperImpl<T> getCsvMapper(ColumnsMapperKey key) {
		CsvMapperImpl<T> csvMapperImpl = mapperCache.get(key);
		if (csvMapperImpl == null) {
			csvMapperImpl = buildMapper(key);
			mapperCache.add(key, csvMapperImpl);
		}
		return csvMapperImpl;
	}

	private CsvMapperImpl<T> buildMapper(ColumnsMapperKey key) {
		CsvMapperBuilder<T> builder = new CsvMapperBuilder<T>(target, classMeta, 0,  cellValueReaderFactory, mapperConfig);
		builder.setDefaultDateFormat(defaultDateFormat);
		for(String col : key.getColumns()) {
			builder.addMapping(col);
		}
		return (CsvMapperImpl<T>)builder.mapper();
	}

    @Override
    public String toString() {
        return "DynamicCsvMapper{" +
                "target=" + target +
                ", mapperCache=" + mapperCache +
                '}';
    }
}
