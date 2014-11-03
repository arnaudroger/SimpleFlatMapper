package org.sfm.csv.impl;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.CsvColumnKey;
import org.sfm.csv.CsvMapper;
import org.sfm.csv.CsvMapperBuilder;
import org.sfm.csv.CsvParser;
import org.sfm.csv.impl.cellreader.StringCellValueReader;
import org.sfm.csv.parser.CharsCellHandler;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.MappingException;
import org.sfm.map.impl.ColumnsMapperKey;
import org.sfm.map.impl.MapperCache;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.utils.RowHandler;

public class DynamicCsvMapper<T> implements CsvMapper<T> {
	
	public final class DynamicCellHandler implements CharsCellHandler {
		private final RowHandler<T> handle;
		private final int rowStart;
		private final int limit;

		private CsvMapperCellHandler<T> cellHandler;
		private List<String> columns = new ArrayList<String>();
		private int currentRow;
		
		public DynamicCellHandler(RowHandler<T> handle, int rowStart, int limit) {
			this.handle = handle;
			this.rowStart = rowStart;
			this.limit = limit;
		}

		@Override
		public boolean endOfRow() {
			if (rowStart == -1 || currentRow >= rowStart) {
				if (cellHandler == null) {
					ColumnsMapperKey key = new ColumnsMapperKey(columns.toArray(new String[columns.size()]));
					CsvMapperImpl<T> csvMapperImpl = mapperCache.get(key);
					if (csvMapperImpl == null) {
						csvMapperImpl = buildeMapper(key);
						mapperCache.add(key, csvMapperImpl);
					}
					cellHandler = csvMapperImpl.newCellHandler(handle);
				} else {
					cellHandler.endOfRow();
				}
			}
			
			return continueProcessing();
		}

		private boolean continueProcessing() {
			boolean continueProcessing =  limit == -1 || (currentRow - rowStart) < limit;
			currentRow++;
			return continueProcessing;
		}

		@Override
		public void newCell(char[] chars, int offset, int length) {
			if (rowStart == -1 || currentRow >= rowStart) {
				if (cellHandler == null) {
					columns.add(StringCellValueReader.readString(chars, offset, length));
				} else {
					cellHandler.newCell(chars, offset, length);
				}
			}
		}

		@Override
		public void end() {
			if (cellHandler != null) {
				cellHandler.end();
			}
		}
	}

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
		csvParser.parse(reader, new DynamicCellHandler(handler, rowStart, limit));
		return handler;
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
