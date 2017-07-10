package org.simpleflatmapper.csv.impl;

import org.simpleflatmapper.csv.*;
import org.simpleflatmapper.csv.parser.CellConsumer;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.mapper.ColumnDefinitionProvider;
import org.simpleflatmapper.map.mapper.MapperCache;
import org.simpleflatmapper.map.mapper.MapperKey;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.util.CheckedConsumer;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Iterator;

//IFJAVA8_START
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
//IFJAVA8_END

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public final class DynamicCsvMapper<T> implements CsvMapper<T> {

	private final ClassMeta<T> classMeta;

	private final Type target;

	private final String defaultDateFormat;

	private final CellValueReaderFactory cellValueReaderFactory;

	private final MapperConfig<CsvColumnKey, CsvColumnDefinition> mapperConfig;

	private final MapperCache<CsvColumnKey, CsvMapperImpl<T>> mapperCache =
			new MapperCache<CsvColumnKey, CsvMapperImpl<T>>(CsvColumnKeyMapperKeyComparator.INSTANCE);

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
	public <H extends CheckedConsumer<? super T>> H forEach(Reader reader, H handle) throws IOException, MappingException {
		return forEach(CsvParser.reader(reader), handle);
	}

	@Override
	public <H extends CheckedConsumer<? super T>> H forEach(CsvReader csvReader, H handle) throws IOException {
		CellConsumer mapperCellConsumer = getDelegateMapper(csvReader).newCellConsumer(handle);
		csvReader.parseAll(mapperCellConsumer);
		return handle;
	}

	@Override
	public <H extends CheckedConsumer<? super T>> H forEach(Reader reader, H handle, int skip) throws IOException, MappingException {
		return forEach(CsvParser.skip(skip).reader(reader), handle);
	}

	@Override
	public <H extends CheckedConsumer<? super T>> H forEach(CsvReader csvReader, H handle, int limit) throws IOException {
		CellConsumer mapperCellConsumer = getDelegateMapper(csvReader).newCellConsumer(handle);
		csvReader.parseRows(mapperCellConsumer, limit);
		return handle;
	}

	@Override
	public <H extends CheckedConsumer<? super T>> H forEach(Reader reader, H handle, int skip, int limit) throws IOException, MappingException {
		return forEach(CsvParser.skip(skip).reader(reader), handle, limit);
	}

	@Override
    public Iterator<T> iterator(Reader reader) throws IOException {
		CsvReader csvReader = CsvParser.reader(reader);
		return iterator(csvReader);
	}

	@Override
	public Iterator<T> iterator(CsvReader csvReader) throws IOException {
		CsvMapperImpl<T> mapper = getDelegateMapper(csvReader);
		return new CsvMapperIterator<T>(csvReader, mapper);
	}

	@Override
	public Iterator<T> iterator(Reader reader, int skip) throws IOException {
		return iterator(CsvParser.skip(skip).reader(reader));
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

	protected CsvMapperImpl<T> getCsvMapper(MapperKey<CsvColumnKey> key) {
		CsvMapperImpl<T> csvMapperImpl = mapperCache.get(key);
		if (csvMapperImpl == null) {
			csvMapperImpl = buildMapper(key);
			mapperCache.add(key, csvMapperImpl);
		}
		return csvMapperImpl;
	}

	private CsvMapperImpl<T> buildMapper(MapperKey<CsvColumnKey> key) {
		CsvMapperBuilder<T> builder = new CsvMapperBuilder<T>(target, classMeta, 0,  cellValueReaderFactory, mapperConfig);
		builder.setDefaultDateFormat(defaultDateFormat);
		for(CsvColumnKey col : key.getColumns()) {
			builder.addMapping(col, CsvColumnDefinition.identity());
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
