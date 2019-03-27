package org.simpleflatmapper.csv;

import org.simpleflatmapper.csv.mapper.CsvMappingContextFactoryBuilder;
import org.simpleflatmapper.csv.mapper.CsvRowEnumerableFactory;
import org.simpleflatmapper.csv.mapper.CsvRowGetterFactory;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.getter.ContextualGetterFactory;
import org.simpleflatmapper.map.mapper.AbstractColumnDefinitionProvider;
import org.simpleflatmapper.map.mapper.ColumnDefinitionProvider;
import org.simpleflatmapper.map.mapper.DefaultSetRowMapperBuilder;
import org.simpleflatmapper.map.mapper.KeyFactory;
import org.simpleflatmapper.map.mapper.MapperBuilder;
import org.simpleflatmapper.map.mapper.MapperSourceImpl;
import org.simpleflatmapper.map.property.DefaultDateFormatProperty;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.csv.impl.*;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.util.BiFunction;
import org.simpleflatmapper.util.ConstantPredicate;
import org.simpleflatmapper.util.Consumer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class CsvMapperBuilder<T> extends MapperBuilder<CsvRow, CsvRowSet, T, CsvColumnKey, IOException, SetRowMapper<CsvRow, CsvRowSet, T, IOException>, CsvMapper<T>, CsvMapperBuilder<T>> {

	
	public static String DEFAULT_DATE_FORMAT =  "yyyy-MM-dd HH:mm:ss";

	private static final MapperSourceImpl<CsvRow, CsvColumnKey> FIELD_MAPPER_SOURCE =
			new MapperSourceImpl<CsvRow, CsvColumnKey>(CsvRow.class,  CsvRowGetterFactory.INSTANCE);
	private static final KeyFactory<CsvColumnKey> KEY_FACTORY = new KeyFactory<CsvColumnKey>() {
		@Override
		public CsvColumnKey newKey(String name, int i) {
			return new CsvColumnKey(name, i);
		}
	};
	public static final CsvColumnKey[] EMPTY_KEYS = new CsvColumnKey[0];
	
	private static ColumnDefinitionProvider<CsvColumnKey> addDefaultDateFormat(AbstractColumnDefinitionProvider<CsvColumnKey> columnDefinitionProvider, String defaultDateFormat) {
		columnDefinitionProvider.addColumnProperty(ConstantPredicate.truePredicate(), new DefaultDateFormatProperty(defaultDateFormat));
		return columnDefinitionProvider;
	}


	private final ClassMeta<T> classMeta;

	public CsvMapperBuilder(final Type target) {
		this(target, ReflectionService.newInstance());
	}
	
	@SuppressWarnings("unchecked")
	public CsvMapperBuilder(final Type target, ReflectionService reflectionService) {
		this((ClassMeta<T>)reflectionService.getClassMeta(target));
	}

	public CsvMapperBuilder(final ClassMeta<T> classMeta) {
		this(classMeta, addDefaultDateFormat(new CsvColumnDefinitionProviderImpl(), DEFAULT_DATE_FORMAT));
	}

	public CsvMapperBuilder(final ClassMeta<T> classMeta, ColumnDefinitionProvider<CsvColumnKey> columnDefinitionProvider) {
		this(
				classMeta, 
				MapperConfig.<CsvColumnKey, CsvRow>config(columnDefinitionProvider),
				CsvRowGetterFactory.INSTANCE,
				new CsvMappingContextFactoryBuilder(!MapperConfig.<CsvColumnKey, CsvRow>config(columnDefinitionProvider).unorderedJoin())
				);
	}

	/**
	 * @param classMeta                  the meta for the target class.
	 * @param mapperConfig               the mapperConfig.
	 * @param getterFactory              the Getter factory.
	 * @param parentBuilder              the parent builder, null if none.
	 */
	public CsvMapperBuilder(
			final ClassMeta<T> classMeta,
			final MapperConfig<CsvColumnKey, CsvRow> mapperConfig,
			final ContextualGetterFactory<? super CsvRow, CsvColumnKey> getterFactory,
			final MappingContextFactoryBuilder<CsvRow, CsvColumnKey> parentBuilder) {

		super(KEY_FACTORY,
				new DefaultSetRowMapperBuilder<CsvRow, CsvRowSet, T, CsvColumnKey, IOException>(
						classMeta, parentBuilder, mapperConfig,
						FIELD_MAPPER_SOURCE.getterFactory(getterFactory), KEY_FACTORY, new CsvRowEnumerableFactory(),
						CsvRowKeySourceGetter.INSTANCE),
				new BiFunction<SetRowMapper<CsvRow, CsvRowSet, T, IOException>, List<CsvColumnKey>, CsvMapper<T>>() {
					@Override
					public CsvMapper<T> apply(SetRowMapper<CsvRow, CsvRowSet, T, IOException> setRowMapper, List<CsvColumnKey> keys) {
						return new CsvMapperImpl<T>(setRowMapper, keys.toArray(EMPTY_KEYS));
					}
				}, CsvColumnDefinition.COLUMN_DEFINITION_FACTORY, 0 );
		this.classMeta = classMeta;
	}

	public void addDefaultHeaders() {
		addDefaultHeaders(classMeta, "");
	}

	private <P> void addDefaultHeaders(final ClassMeta<P> classMeta, final String prefix) {
		classMeta.forEachProperties(new Consumer<PropertyMeta<P,?>>() {

			@Override
			public void accept(PropertyMeta<P, ?> propertyMeta) {

				String currentName = prefix +  propertyMeta.getPath();

				if (propertyMeta.isSubProperty()) {
					addDefaultHeaders(propertyMeta.getPropertyClassMeta(), currentName + "_");
				} else {
					addMapping(currentName);
				}

			}
		});
	}
}