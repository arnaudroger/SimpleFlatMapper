package org.sfm.csv.impl;

import org.sfm.csv.*;
import org.sfm.csv.parser.CsvReader;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.MappingException;
import org.sfm.map.impl.ColumnsMapperKey;
import org.sfm.map.impl.MapperCache;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.utils.RowHandler;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/*IFJAVA8_START
IFJAVA8_END*/

public final class DynamicCsvMapper<T> implements CsvMapper<T> {
	
	private final ClassMeta<T> classMeta;
	private final Class<T> target;

	private final CsvParser csvParser;

	private final FieldMapperErrorHandler<CsvColumnKey> fieldMapperErrorHandler;

	private final MapperBuilderErrorHandler mapperBuilderErrorHandler;
	
	private final String defaultDateFormat;
	
	private MapperCache<ColumnsMapperKey, CsvMapperImpl<T>> mapperCache = new MapperCache<ColumnsMapperKey, CsvMapperImpl<T>>();
	private final Map<String, String> aliases;
	private final Map<String, CellValueReader<?>> customReaders;

	public DynamicCsvMapper(final Class<T> target, final ReflectionService reflectionService, 
			final FieldMapperErrorHandler<CsvColumnKey> fieldMapperErrorHandler, 
			final MapperBuilderErrorHandler mapperBuilderErrorHandler, String defaultDateFormat, 
			Map<String, String> aliases, Map<String, CellValueReader<?>> customReaders) {
		this.classMeta = reflectionService.getClassMeta(target);
		this.target = target;
		this.fieldMapperErrorHandler = fieldMapperErrorHandler;
		this.mapperBuilderErrorHandler = mapperBuilderErrorHandler;
		this.csvParser = new CsvParser();
		this.defaultDateFormat = defaultDateFormat;
		this.aliases = aliases;
		this.customReaders = customReaders;
	}

	@Override
	public <H extends RowHandler<T>> H forEach(Reader reader, H handle) throws IOException, MappingException {
		CsvReader csvReader = CsvParser.newCsvReader(reader);
		CsvMapperCellConsumer<T> mapperCellConsumer = getDelegateMapper(csvReader).newCellConsumer(handle);
		csvReader.parseAll(mapperCellConsumer);
		return handle;
	}

	@Override
	public <H extends RowHandler<T>> H forEach(Reader reader, H handle, int skip) throws IOException, MappingException {
		CsvReader csvReader = CsvParser.newCsvReader(reader);
		csvReader.skipLines(skip);
		CsvMapperCellConsumer<T> mapperCellConsumer = getDelegateMapper(csvReader).newCellConsumer(handle);
		csvReader.parseAll(mapperCellConsumer);
		return handle;
	}

	@Override
	public <H extends RowHandler<T>> H forEach(Reader reader, H handle, int skip, int limit) throws IOException, MappingException {
		CsvReader csvReader = CsvParser.newCsvReader(reader);
		csvReader.skipLines(skip);
		CsvMapperCellConsumer<T> mapperCellConsumer = getDelegateMapper(csvReader).newCellConsumer(handle);
		csvReader.parseLines(mapperCellConsumer, limit);
		return handle;
	}

	@Override
	public Iterator<T> iterate(Reader reader) throws IOException {
		CsvReader csvReader = CsvParser.newCsvReader(reader);
		CsvMapperImpl<T> mapper = getDelegateMapper(csvReader);
		return new CsvIterator<T>(csvReader, mapper);
	}

	@Override
	public Iterator<T> iterate(Reader reader, int skip) throws IOException {
		CsvReader csvReader = CsvParser.newCsvReader(reader);
		csvReader.skipLines(skip);
		CsvMapperImpl<T> mapper = getDelegateMapper(csvReader);
		return new CsvIterator<T>(csvReader, mapper);
	}

	private CsvMapperImpl<T> getDelegateMapper(CsvReader reader) throws IOException {
		ColumnsMapperKeyBuilderCellConsumer keyBuilderCellConsumer = new ColumnsMapperKeyBuilderCellConsumer();
		reader.parseLine(keyBuilderCellConsumer);
		return getCsvMapper(keyBuilderCellConsumer.getKey());
	}


	/*IFJAVA8_START
	@Override
	public Stream<T> stream(Reader reader) throws IOException {
		CsvReader csvReader = CsvParser.newCsvReader(reader);
		CsvMapperImpl<T> mapper = getDelegateMapper(csvReader);
		return StreamSupport.stream(mapper.new CsvSpliterator(csvReader), false);
	}

	@Override
	public Stream<T> stream(Reader reader, int skip) throws IOException {
		CsvReader csvReader = CsvParser.newCsvReader(reader);
		csvReader.skipLines(skip);
		CsvMapperImpl<T> mapper = getDelegateMapper(csvReader);
		return StreamSupport.stream(mapper.new CsvSpliterator(csvReader), false);
	}
	IFJAVA8_END*/

	protected CsvMapperImpl<T> getCsvMapper(ColumnsMapperKey key) {
		CsvMapperImpl<T> csvMapperImpl = mapperCache.get(key);
		if (csvMapperImpl == null) {
			csvMapperImpl = buildeMapper(key);
			mapperCache.add(key, csvMapperImpl);
		}
		return csvMapperImpl;
	}

	private CsvMapperImpl<T> buildeMapper(ColumnsMapperKey key) {
		CsvMapperBuilder<T> builder = new CsvMapperBuilder<T>(target, classMeta, aliases, customReaders);
		builder.fieldMapperErrorHandler(fieldMapperErrorHandler);
		builder.mapperBuilderErrorHandler(mapperBuilderErrorHandler);
		builder.setDefaultDateFormat(defaultDateFormat);
		for(String col : key.getColumns()) {
			builder.addMapping(col);
		}
		return (CsvMapperImpl<T>)builder.mapper();
	}

}
