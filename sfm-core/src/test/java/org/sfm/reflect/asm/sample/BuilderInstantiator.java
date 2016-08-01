package org.sfm.reflect.asm.sample;

import org.sfm.reflect.BuilderInstantiatorDefinitionFactoryTest;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.primitive.IntGetter;

import java.io.InputStream;
import java.util.Map;

public final class BuilderInstantiator implements Instantiator<InputStream, BuilderInstantiatorDefinitionFactoryTest.ClassBuilderWithMethod> {

	final Instantiator<Void, BuilderInstantiatorDefinitionFactoryTest.ClassBuilderWithMethod.Builder> builderInstantiator;
	final IntGetter<InputStream> getter_id;
	final Getter<InputStream, String> getter_name;

	@SuppressWarnings("unchecked")
	public BuilderInstantiator(final Map<String, Getter<InputStream, ?>> injections, Instantiator<Void, BuilderInstantiatorDefinitionFactoryTest.ClassBuilderWithMethod.Builder> builderInstantiator) {
		this.builderInstantiator = builderInstantiator;
		this.getter_id = (IntGetter<InputStream>) injections.get("id");
		this.getter_name = (Getter<InputStream, String>) injections.get("name");
	}
	
	@Override
	public BuilderInstantiatorDefinitionFactoryTest.ClassBuilderWithMethod newInstance(InputStream source) throws Exception {
		BuilderInstantiatorDefinitionFactoryTest.ClassBuilderWithMethod.Builder builder = builderInstantiator.newInstance(null);
		builder = builder.id(getter_id.getInt(source));
		builder.name(getter_name.get(source));
		return builder.build();
	}
}
