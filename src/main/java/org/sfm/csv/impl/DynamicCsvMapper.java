package org.sfm.csv.impl;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map;
//IFJAVA8_START
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
//IFJAVA8_END


import org.sfm.csv.CellValueReader;
import org.sfm.csv.CsvColumnKey;
import org.sfm.csv.CsvMapper;
import org.sfm.csv.CsvMapperBuilder;
import org.sfm.csv.CsvParser;
import org.sfm.csv.parser.CharsCellHandler;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.MappingException;
import org.sfm.map.impl.ColumnsMapperKey;
import org.sfm.map.impl.MapperCache;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.utils.RowHandler;

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
	public <H extends RowHandler<T>> H forEach(final Reader reader, final H handler) throws IOException, MappingException {
		return forEach(reader, handler, 0, -1);
	}
	
	@Override
	public <H extends RowHandler<T>> H forEach(final Reader reader, final H handler, final int rowStart) throws IOException, MappingException {
		return forEach(reader, handler, rowStart, -1);
	}
	
	@Override
	public <H extends RowHandler<T>> H forEach(final Reader reader, final H handler, final int rowStart, final int limit) throws IOException, MappingException {
		csvParser.parse(reader, new DynamicPushCellHandler<T>(this, handler, rowStart, limit));
		return handler;
	}
	
	protected CharsCellHandler newPullCellHandler(RowHandler<T> handler, int rowStart) {
		return new DynamicPullCellHandler<T>(this, handler, rowStart);
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

	@Override
	public Iterator<T> iterate(Reader reader) throws IOException {
		return iterate(reader, -1);
	}
	
	@Override
	public Iterator<T> iterate(Reader reader, int rowStart) throws IOException {
		return new CsvIterator<T>(reader, this, rowStart);
	}

	//IFJAVA8_START
	@Override
	public Stream<T> stream(Reader reader) throws IOException {
		return stream(reader, -1);
	}
	
	@Override
	public Stream<T> stream(Reader reader, int rowStart) throws IOException {
		Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(iterate(reader, rowStart), Spliterator.DISTINCT | Spliterator.ORDERED);
		return StreamSupport.stream(spliterator, false);
	}
	//IFJAVA8_END

	public CsvMapperImpl<T> getCsvMapper(ColumnsMapperKey key) {
		CsvMapperImpl<T> csvMapperImpl = mapperCache.get(key);
		if (csvMapperImpl == null) {
			csvMapperImpl = buildeMapper(key);
			mapperCache.add(key, csvMapperImpl);
		}
		return csvMapperImpl;
	}

}
