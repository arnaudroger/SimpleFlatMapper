package org.simpleflatmapper.reflect.test.asm.sample;

import org.simpleflatmapper.reflect.test.BuilderInstantiatorDefinitionFactoryTest;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Instantiator;
import org.simpleflatmapper.reflect.primitive.IntGetter;

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
