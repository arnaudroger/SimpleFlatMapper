package org.sfm.csv.impl;

import org.sfm.csv.*;
import org.sfm.csv.CsvReader;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.MappingException;
import org.sfm.map.impl.ColumnsMapperKey;
import org.sfm.map.impl.MapperCache;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.PropertyNameMatcherFactory;
import org.sfm.utils.RowHandler;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
//IFJAVA8_START
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
//IFJAVA8_END

public final class DynamicCsvMapper<T> implements CsvMapper<T> {
	
	private final ClassMeta<T> classMeta;
	private final Type target;

	private final FieldMapperErrorHandler<CsvColumnKey> fieldMapperErrorHandler;

	private final MapperBuilderErrorHandler mapperBuilderErrorHandler;
	
	private final String defaultDateFormat;
	private final PropertyNameMatcherFactory propertyNameMatcherFactory;

	private MapperCache<ColumnsMapperKey, CsvMapperImpl<T>> mapperCache = new MapperCache<ColumnsMapperKey, CsvMapperImpl<T>>();
	private final Map<String, String> aliases;
	private final Map<String, CellValueReader<?>> customReaders;

	public DynamicCsvMapper(final Type target, final ClassMeta<T> classMeta,
							final FieldMapperErrorHandler<CsvColumnKey> fieldMapperErrorHandler,
							final MapperBuilderErrorHandler mapperBuilderErrorHandler, String defaultDateFormat,
							Map<String, String> aliases, Map<String, CellValueReader<?>> customReaders,
							PropertyNameMatcherFactory propertyNameMatcherFactory) {
		this.classMeta = classMeta;
		this.target = target;
		this.fieldMapperErrorHandler = fieldMapperErrorHandler;
		this.mapperBuilderErrorHandler = mapperBuilderErrorHandler;
		this.defaultDateFormat = defaultDateFormat;
		this.aliases = aliases;
		this.customReaders = customReaders;
		this.propertyNameMatcherFactory = propertyNameMatcherFactory;
	}

	@Override
	public <H extends RowHandler<T>> H forEach(Reader reader, H handle) throws IOException, MappingException {
		return forEach(CsvParser.reader(reader), handle);
	}

	@Override
	public <H extends RowHandler<T>> H forEach(CsvReader csvReader, H handle) throws IOException {
		CsvMapperCellConsumer<T> mapperCellConsumer = getDelegateMapper(csvReader).newCellConsumer(handle);
		csvReader.parseAll(mapperCellConsumer);
		return handle;
	}

	@Override
	public <H extends RowHandler<T>> H forEach(Reader reader, H handle, int skip) throws IOException, MappingException {
		return forEach(CsvParser.skip(skip).reader(reader), handle);
	}

	@Override
	public <H extends RowHandler<T>> H forEach(CsvReader csvReader, H handle, int limit) throws IOException {
		CsvMapperCellConsumer<T> mapperCellConsumer = getDelegateMapper(csvReader).newCellConsumer(handle);
		csvReader.parseRows(mapperCellConsumer, limit);
		return handle;
	}

	@Override
	public <H extends RowHandler<T>> H forEach(Reader reader, H handle, int skip, int limit) throws IOException, MappingException {
		return forEach(CsvParser.skip(skip).reader(reader), handle, limit);
	}

	@Override
	public Iterator<T> iterate(Reader reader) throws IOException {
		CsvReader csvReader = CsvParser.reader(reader);
		return iterate(csvReader);
	}

	@Override
	public Iterator<T> iterate(CsvReader csvReader) throws IOException {
		CsvMapperImpl<T> mapper = getDelegateMapper(csvReader);
		return new CsvMapperIterator<T>(csvReader, mapper);
	}

	@Override
	public Iterator<T> iterate(Reader reader, int skip) throws IOException {
		return iterate(CsvParser.skip(skip).reader(reader));
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
			csvMapperImpl = buildeMapper(key);
			mapperCache.add(key, csvMapperImpl);
		}
		return csvMapperImpl;
	}

	private CsvMapperImpl<T> buildeMapper(ColumnsMapperKey key) {
		CsvMapperBuilder<T> builder = new CsvMapperBuilder<T>(target, classMeta, aliases, customReaders, propertyNameMatcherFactory);
		builder.fieldMapperErrorHandler(fieldMapperErrorHandler);
		builder.mapperBuilderErrorHandler(mapperBuilderErrorHandler);
		builder.setDefaultDateFormat(defaultDateFormat);
		for(String col : key.getColumns()) {
			builder.addMapping(col);
		}
		return (CsvMapperImpl<T>)builder.mapper();
	}

}
