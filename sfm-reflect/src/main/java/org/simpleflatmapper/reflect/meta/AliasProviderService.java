package org.simpleflatmapper.reflect.meta;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.ServiceLoader;

public final class AliasProviderService {

	private AliasProviderService() { }

	private static final AliasProvider aliasProvider = findAliasProviders();

	private static AliasProvider findAliasProviders() {
		return findAliasProviders(ServiceLoader.load(AliasProviderFactory.class));
	}

	private static AliasProvider findAliasProviders(ServiceLoader<AliasProviderFactory> serviceLoader) {
		ArrayList<AliasProvider> providers = new ArrayList<AliasProvider>();

		Iterator<AliasProviderFactory> iterator = serviceLoader.iterator();

		while(iterator.hasNext()) {
			try {
				AliasProviderFactory factory = iterator.next();
				if (factory.isActive()) {
					providers.add(factory.newProvider());
				}
			} catch (Throwable e) {
				System.err.println("Unexpected error on listing ConverterFactoryProducer, prop classloader visibility " + e);
			}
		}

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
