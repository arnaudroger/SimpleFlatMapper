package org.sfm.jdbc;

import org.sfm.map.FieldMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.Mapper;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.MapperBuildingException;
import org.sfm.reflect.meta.PropertyMeta;

public interface MapperBuilder<S, T, K, M extends Mapper<S, T>, B extends MapperBuilder<S, T, K, M, B>> {
	B fieldMapperErrorHandler(FieldMapperErrorHandler<K> errorHandler);
	B mapperBuilderErrorHandler(MapperBuilderErrorHandler errorHandler);
	M mapper() throws MapperBuildingException;
	FieldMapper<S, T>[] fields();
	B addMapping(String property, K key);
	void addMapping(PropertyMeta<T, ?> property, K key);
}
