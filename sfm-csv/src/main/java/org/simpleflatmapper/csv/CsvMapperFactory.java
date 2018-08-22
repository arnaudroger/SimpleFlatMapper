package org.simpleflatmapper.csv;

import org.simpleflatmapper.csv.impl.CsvColumnDefinitionProviderImpl;
import org.simpleflatmapper.csv.mapper.CsvMappingContextFactoryBuilder;
import org.simpleflatmapper.csv.mapper.CsvRowGetterFactory;
import org.simpleflatmapper.csv.property.CustomReaderProperty;
import org.simpleflatmapper.lightningcsv.CsvReader;
import org.simpleflatmapper.lightningcsv.StringReader;
import org.simpleflatmapper.map.MapperBuildingException;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.Result;
import org.simpleflatmapper.map.ResultFieldMapperErrorHandler;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.mapper.AbstractColumnDefinitionProvider;
import org.simpleflatmapper.map.mapper.AbstractMapperFactory;
import org.simpleflatmapper.map.mapper.DynamicSetRowMapper;
import org.simpleflatmapper.map.mapper.MapperKey;
import org.simpleflatmapper.map.property.DefaultDateFormatProperty;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.reflect.ParameterizedTypeImpl;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.util.ConstantPredicate;
import org.simpleflatmapper.util.Function;
import org.simpleflatmapper.util.TypeReference;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.util.UnaryFactory;
import org.simpleflatmapper.util.UnaryFactoryWithException;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * CsvMapperFactory is not Thread-Safe but the mappers are.
 * It is strongly advised to instantiate one jdbcMapper per class for the life of your application.
 * <p>
 * You can instantiate dynamic jdbcMapper which will use the name line of the csv file
 * to figure out the list of the columns or a static one using a builder.
 * <p>
 * <code>
 *     // create a dynamic jdbcMapper targeting MyClass<br>
 *     CsvMapperFactory<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.newInstance()<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.newMapper(MyClass.class);<br>
 *     <br>
 *     // create a static jdbcMapper targeting MyClass<br>
 *     CsvMapperFactory<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.newInstance()<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.newBuilder(MyClass.class)<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.addMapping("id")<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.addMapping("field1")<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.addMapping("field2")<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.mapper();<br>
 *     <br>
 * </code>
 */
public final class CsvMapperFactory extends AbstractMapperFactory<CsvColumnKey, CsvMapperFactory> {


	private GetterFactory<CsvRow, CsvColumnKey> getterFactory = CsvRowGetterFactory.INSTANCE;

	/**
	 * instantiate a new JdbcMapperFactory
	 * @return a new JdbcMapperFactory
	 */
	public static CsvMapperFactory newInstance() {
		return new CsvMapperFactory();
	}

	private String defaultDateFormat = CsvMapperBuilder.DEFAULT_DATE_FORMAT;


	private CsvMapperFactory() {
		super(new CsvColumnDefinitionProviderImpl(), CsvColumnDefinition.identity());
	}
	
	private CsvMapperFactory(CsvMapperFactory parent)  {
		super(parent);
	}

	@Override
	public AbstractColumnDefinitionProvider<CsvColumnKey> enrichColumnDefinitions(AbstractColumnDefinitionProvider<CsvColumnKey> columnDefinitions) {
		AbstractColumnDefinitionProvider<CsvColumnKey> copy = columnDefinitions.copy();
		copy.addColumnProperty(ConstantPredicate.truePredicate(), new DefaultDateFormatProperty(defaultDateFormat));
		return copy;
	}

	public CsvMapperFactory defaultDateFormat(final String defaultDateFormat) {
		this.defaultDateFormat = defaultDateFormat;
		return this;
	}

	public CsvMapperFactory addCustomValueReader(String key, CellValueReader<?> cellValueReader) {
		return addColumnProperty(key, new CustomReaderProperty(cellValueReader));
	}

	public CsvMapperFactory addCustomValueReader(String key, StringReader<?> stringReader) {
		return addColumnProperty(key, new CustomReaderProperty(stringReader));
	}

	/**
	 * 
	 * @param target the targeted class for the jdbcMapper
     * @param <T> the targeted type
	 * @return a jdbc jdbcMapper that will map to the targeted class.
	 * @throws MapperBuildingException if an error occurs building the jdbcMapper
	 */
	public <T> CsvMapper<T> newMapper(final Class<T> target) throws MapperBuildingException {
		return newMapper((Type)target);
	}

    public <T> CsvMapper<T> newMapper(final TypeReference<T> target) throws MapperBuildingException {
        return newMapper(target.getType());
    }

    public <T> CsvMapper<T> newMapper(final Type target) throws MapperBuildingException {
		final ClassMeta<T> classMeta = getClassMeta(target);
		return newMapper(classMeta);
	}

	public <T> CsvMapper<T> newMapper(final ClassMeta<T> classMeta) throws MapperBuildingException {
		return new DynamicCsvSetRowMapper<T>(
				new SetRowMapperFactory<T>(classMeta),  new CsvRowMapperKeyFactory(),  new CsvRowSetMapperKeyFactory()
					);
	}

	public <T> CsvMapper<Result<T,CsvColumnKey>> newErrorCollectingMapper(final Class<T> target) throws MapperBuildingException {
		return newErrorCollectingMapper((Type)target);
	}

	public <T> CsvMapper<Result<T,CsvColumnKey>> newErrorCollectingMapper(final TypeReference<T> target) throws MapperBuildingException {
		return newErrorCollectingMapper(target.getType());
	}

	public <T> CsvMapper<Result<T,CsvColumnKey>> newErrorCollectingMapper(final Type target) throws MapperBuildingException {
		CsvMapper<Result.ResultBuilder<T, CsvColumnKey>> csvMapper = 
				new CsvMapperFactory(this)
						.fieldMapperErrorHandler(new ResultFieldMapperErrorHandler<CsvColumnKey>())
				.newMapper(new ParameterizedTypeImpl(Result.ResultBuilder.class, target, CsvColumnKey.class))
				;
		Function<Result.ResultBuilder<T, CsvColumnKey>, Result<T, CsvColumnKey>> resultBuilderResultFunction = Result.buildingFunction();
		//return new TransformCsvMapper<Result.ResultBuilder<T, CsvColumnKey>, Result<T, CsvColumnKey>>(csvMapper, resultBuilderResultFunction);
		return null;
		
	}

	/**
	 * Will create a newInstance of ResultSetMapperBuilder
	 * @param target the target class of the jdbcMapper
     * @param <T> the targeted type
	 * @return a builder ready to instantiate a jdbcMapper or to be customized
     * @throws MapperBuildingException if an error occurs building the jdbcMapper
	 */
	public <T> CsvMapperBuilder<T> newBuilder(final Class<T> target) {
		return newBuilder((Type)target);
	}

    public <T> CsvMapperBuilder<T> newBuilder(final TypeReference<T> target) {
        return newBuilder(target.getType());
    }

    public <T> CsvMapperBuilder<T> newBuilder(final Type target) {
		final ClassMeta<T> classMeta = getClassMeta(target);
		return newBuilder(classMeta);
	}

	public <T> CsvMapperBuilder<T> newBuilder(final ClassMeta<T> classMeta) {
		CsvMapperBuilder<T> builder =
				new CsvMapperBuilder<T>(classMeta, mapperConfig(), getterFactory, new CsvMappingContextFactoryBuilder());
		return builder;
	}

	private static class CsvRowSetMapperKeyFactory implements UnaryFactoryWithException<CsvRowSet, MapperKey<CsvColumnKey>, IOException> {
		@Override
		public MapperKey<CsvColumnKey> newInstance(CsvRowSet csvRowSet) throws IOException {
			return new MapperKey<CsvColumnKey>(csvRowSet.getKeys());
		}
	}
	private static class CsvRowMapperKeyFactory implements UnaryFactoryWithException<CsvRow, MapperKey<CsvColumnKey>, IOException> {
		@Override
		public MapperKey<CsvColumnKey> newInstance(CsvRow csvRow) throws IOException {
			return new MapperKey<CsvColumnKey>(csvRow.getKeys());
		}
	}

	private class SetRowMapperFactory<T> implements UnaryFactory<MapperKey<CsvColumnKey>, SetRowMapper<CsvRow, CsvRowSet, T,IOException>> {
		private final ClassMeta<T> classMeta;

		public SetRowMapperFactory(ClassMeta<T> classMeta) {
			this.classMeta = classMeta;
		}

		@Override
		public SetRowMapper<CsvRow, CsvRowSet,T,IOException> newInstance(MapperKey<CsvColumnKey> mapperKey) {
			final CsvMapperBuilder<T> builder = newBuilder(classMeta);
			for(CsvColumnKey key : mapperKey.getColumns()) {
				builder.addMapping(key);
			}
			return builder.mapper();
		}
	}


	public static class DynamicCsvSetRowMapper<T>
			extends DynamicSetRowMapper<CsvRow, CsvRowSet, T, IOException, CsvColumnKey>
			implements CsvMapper<T> {

		public DynamicCsvSetRowMapper(
				UnaryFactory<MapperKey<CsvColumnKey>, SetRowMapper<CsvRow, CsvRowSet, T, IOException>> mapperFactory,
				UnaryFactoryWithException<CsvRow, MapperKey<CsvColumnKey>, IOException> mapperKeyFromRow,
				UnaryFactoryWithException<CsvRowSet, MapperKey<CsvColumnKey>, IOException> mapperKeyFromSet) {
			super(mapperFactory, mapperKeyFromRow, mapperKeyFromSet, CsvColumnKeyMapperKeyComparator.INSTANCE);
		}

		@Override
		public String toString() {
			return "DynamicCsvSetRowMapper{}";
		}

		@Override
		public <H extends CheckedConsumer<? super T>> H forEach(Reader reader, H handle) throws IOException, MappingException {
			forEach(toCsvRowSet(reader, 0, -1), handle);
			return handle;
		}

		@Override
		public <H extends CheckedConsumer<? super T>> H forEach(CsvReader reader, H handle) throws IOException, MappingException {
			forEach(toCsvRowSet(reader, 0 , -1), handle);
			return handle;
		}

		@Override
		public <H extends CheckedConsumer<? super T>> H forEach(Reader reader, H handle, int skip) throws IOException, MappingException {
			forEach(toCsvRowSet(reader, skip, -1), handle);
			return handle;
		}

		@Override
		public <H extends CheckedConsumer<? super T>> H forEach(Reader reader, H handle, int skip, int limit) throws IOException, MappingException {
			forEach(toCsvRowSet(reader, skip, limit), handle);
			return handle;
		}

		@Override
		public <H extends CheckedConsumer<? super T>> H forEach(CsvReader reader, H handle, int limit) throws IOException, MappingException {
			forEach(toCsvRowSet(reader, 0, limit), handle);
			return handle;
		}

		@Override
		public Iterator<T> iterator(Reader reader) throws IOException {
			return iterator(toCsvRowSet(reader, 0 , -1));
		}

		@Override
		public Iterator<T> iterator(CsvReader reader) throws IOException {
			return iterator(toCsvRowSet(reader, 0 , -1));
		}

		@Override
		public Iterator<T> iterator(Reader reader, int skip) throws IOException {
			return iterator(toCsvRowSet(reader, skip , -1));
		}

		@Override
		public Stream<T> stream(Reader reader) throws IOException {
			return stream(toCsvRowSet(reader, 0 , -1));
		}

		@Override
		public Stream<T> stream(CsvReader reader) throws IOException {
			return stream(toCsvRowSet(reader, 0 , -1));
		}

		@Override
		public Stream<T> stream(Reader reader, int skip) throws IOException {
			return stream(toCsvRowSet(reader, skip , -1));
		}

		private CsvRowSet toCsvRowSet(Reader reader, int skip, int limit) throws IOException {
			return toCsvRowSet(CsvParser.reader(reader), skip, limit);
		}

		private CsvRowSet toCsvRowSet(CsvReader reader, int skip, int limit) throws IOException {
			reader.skipRows(skip);
			return new CsvRowSet(reader, limit);
		}
	}
}
