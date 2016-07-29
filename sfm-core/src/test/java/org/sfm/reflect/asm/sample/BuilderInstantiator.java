package org.sfm.reflect.asm.sample;

import org.sfm.jdbc.impl.getter.IntResultSetGetter;
import org.sfm.jdbc.impl.getter.StringResultSetGetter;
import org.sfm.reflect.BuilderInstantiatorDefinitionFactoryTest;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Instantiator;

import java.sql.ResultSet;
import java.util.Map;

public final class BuilderInstantiator implements Instantiator<ResultSet, BuilderInstantiatorDefinitionFactoryTest.ClassBuilderWithMethod> {

	final Instantiator<Void, BuilderInstantiatorDefinitionFactoryTest.ClassBuilderWithMethod.Builder> builderInstantiator;
	final IntResultSetGetter getter_id;
	final StringResultSetGetter getter_name;

	@SuppressWarnings("unchecked")
	public BuilderInstantiator(final Map<String, Getter<ResultSet, ?>> injections, Instantiator<Void, BuilderInstantiatorDefinitionFactoryTest.ClassBuilderWithMethod.Builder> builderInstantiator) {
		this.builderInstantiator = builderInstantiator;
		this.getter_id = (IntResultSetGetter) injections.get("id");
		this.getter_name = (StringResultSetGetter) injections.get("name");
	}
	
	@Override
	public BuilderInstantiatorDefinitionFactoryTest.ClassBuilderWithMethod newInstance(ResultSet source) throws Exception {
		BuilderInstantiatorDefinitionFactoryTest.ClassBuilderWithMethod.Builder builder = builderInstantiator.newInstance(null);
		builder = builder.id(getter_id.getInt(source));
		builder.name(getter_name.get(source));
		return builder.build();
	}
}
