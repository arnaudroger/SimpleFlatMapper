package org.simpleflatmapper.csv;

import org.simpleflatmapper.csv.mapper.CsvMappingContextFactoryBuilder;
import org.simpleflatmapper.csv.mapper.CsvRowEnumerableFactory;
import org.simpleflatmapper.csv.mapper.CsvRowGetterFactory;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.mapper.ColumnDefinitionProvider;
import org.simpleflatmapper.map.mapper.DefaultSetRowMapperBuilder;
import org.simpleflatmapper.map.mapper.KeyFactory;
import org.simpleflatmapper.map.mapper.MapperBuilder;
import org.simpleflatmapper.map.mapper.MapperSourceImpl;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.csv.impl.*;
import org.simpleflatmapper.util.Function;

import java.io.IOException;
import java.lang.reflect.Type;

public class CsvMapperBuilder<T> extends MapperBuilder<CsvRow, CsvRowSet, T, CsvColumnKey, IOException, SetRowMapper<CsvRow, CsvRowSet, T, IOException>, CsvMapper<T>, CsvMapperBuilder<T>> {

	private static final MapperSourceImpl<CsvRow, CsvColumnKey> FIELD_MAPPER_SOURCE =
			new MapperSourceImpl<CsvRow, CsvColumnKey>(CsvRow.class,  CsvRowGetterFactory.INSTANCE);
	private static final KeyFactory<CsvColumnKey> KEY_FACTORY = new KeyFactory<CsvColumnKey>() {
		@Override
		public CsvColumnKey newKey(String name, int i) {
			return new CsvColumnKey(name, i);
		}
	};

	public CsvMapperBuilder(final Type target) {
		this(target, ReflectionService.newInstance());
	}
	
	@SuppressWarnings("unchecked")
	public CsvMapperBuilder(final Type target, ReflectionService reflectionService) {
		this(target, (ClassMeta<T>)reflectionService.getClassMeta(target));
	}

	public CsvMapperBuilder(final Type target, final ClassMeta<T> classMeta) {
		this(target, classMeta, new IdentityCsvColumnDefinitionProvider());
	}

	public CsvMapperBuilder(final Type target, final ClassMeta<T> classMeta, ColumnDefinitionProvider<CsvColumnKey> columnDefinitionProvider) {
		this(
				classMeta, 
				MapperConfig.<CsvColumnKey>config(columnDefinitionProvider),
				CsvRowGetterFactory.INSTANCE,
				new CsvMappingContextFactoryBuilder()
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
			final MapperConfig<CsvColumnKey> mapperConfig,
			final GetterFactory<CsvRow, CsvColumnKey> getterFactory,
			final MappingContextFactoryBuilder<CsvRow, CsvColumnKey> parentBuilder) {

		super(KEY_FACTORY,
				new DefaultSetRowMapperBuilder<CsvRow, CsvRowSet, T, CsvColumnKey, IOException>(
						classMeta, parentBuilder, mapperConfig,
						FIELD_MAPPER_SOURCE.getterFactory(getterFactory), KEY_FACTORY, new CsvRowEnumerableFactory()
				),
				new Function<SetRowMapper<CsvRow, CsvRowSet, T, IOException>, CsvMapper<T>>() {
					@Override
					public CsvMapper<T> apply(SetRowMapper<CsvRow, CsvRowSet, T, IOException> setRowMapper) {
						return new CsvMapperImpl<T>(setRowMapper);
					}
				},0 );
	}
	


}