package org.sfm.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.sfm.csv.cell.StringCellValueReader;
import org.sfm.csv.parser.CharsCellHandler;
import org.sfm.csv.parser.CsvParser;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.MapperCache;
import org.sfm.map.MapperKey;
import org.sfm.map.MappingException;
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
					MapperKey key = new MapperKey(columns.toArray(new String[columns.size()]));
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
			cellHandler.end();
		}
	}

	private final ClassMeta<T> classMeta;
	private final Class<T> target;

	private final CsvParser csvParser;

	private final FieldMapperErrorHandler<Integer> fieldMapperErrorHandler;

	private final  MapperBuilderErrorHandler mapperBuilderErrorHandler;
	private MapperCache<CsvMapperImpl<T>> mapperCache = new MapperCache<CsvMapperImpl<T>>();

	public DynamicCsvMapper(final Class<T> target, final ReflectionService reflectionService, 
			final FieldMapperErrorHandler<Integer> fieldMapperErrorHandler, 
			final MapperBuilderErrorHandler mapperBuilderErrorHandler) {
		this.classMeta = reflectionService.getClassMeta(target);
		this.target = target;
		this.fieldMapperErrorHandler = fieldMapperErrorHandler;
		this.mapperBuilderErrorHandler = mapperBuilderErrorHandler;
		this.csvParser = new CsvParser();

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
	
	private CsvMapperImpl<T> buildeMapper(MapperKey key) {
		CsvMapperBuilder<T> builder = new CsvMapperBuilder<T>(target, classMeta);
		builder.fieldMapperErrorHandler(fieldMapperErrorHandler);
		builder.mapperBuilderErrorHandler(mapperBuilderErrorHandler);
		for(String col : key.getColumns()) {
			builder.addMapping(col);
		}
		return (CsvMapperImpl<T>)builder.mapper();
	}


	@Override
	public <H extends RowHandler<T>> H forEach(InputStream is, H handle)
			throws IOException, MappingException {
		return forEach(new InputStreamReader(is), handle);
	}
}
