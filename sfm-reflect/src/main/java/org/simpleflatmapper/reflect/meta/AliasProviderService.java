package org.simpleflatmapper.reflect.meta;


import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.ProducerServiceLoader;

import java.util.ArrayList;
import java.util.ServiceLoader;

public final class AliasProviderService {

	private AliasProviderService() { }

	private static final AliasProvider aliasProvider = findAliasProviders();


	private static AliasProvider findAliasProviders() {
		final ArrayList<AliasProvider> providers = new ArrayList<AliasProvider>();

		ProducerServiceLoader.produceFromServiceLoader(ServiceLoader.load(AliasProviderProducer.class), new Consumer<AliasProvider>() {
			@Override
			public void accept(AliasProvider t) {
				providers.add(t);
			}
		});
		return aggregateAliasProvider(providers.toArray(new AliasProvider[0]));
	}

	public static AliasProvider getAliasProvider() {
		return aliasProvider;
	}

	private static AliasProvider aggregateAliasProvider(AliasProvider[] registered) {
		if (registered.length == 0) {
			return new DefaultAliasProvider();
		} else if (registered.length == 1) {
			return registered[0];
		} else {
			return new ArrayAliasProvider(registered);
		}
	}

}
