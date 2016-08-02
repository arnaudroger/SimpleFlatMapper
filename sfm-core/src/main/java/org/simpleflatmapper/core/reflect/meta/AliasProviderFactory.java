package org.simpleflatmapper.core.reflect.meta;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class AliasProviderFactory {

	private static AtomicReference<CurrentAliasProvider> currentAliasProvider =
			new AtomicReference<CurrentAliasProvider>(
					new CurrentAliasProvider(
							new DefaultAliasProvider(), new AliasProvider[0]));

	private static AliasProvider[] registered = new AliasProvider[0];

	public static AliasProvider getAliasProvider() {
		return currentAliasProvider.get().aliasProvider;
	}

	public static void register(AliasProvider aliasProvider) {
		CurrentAliasProvider cap;
		CurrentAliasProvider newCap;
		do {
			cap = currentAliasProvider.get();

			AliasProvider[] newRegistered = Arrays.copyOf(registered, registered.length + 1);
			newRegistered[newRegistered.length - 1]  = aliasProvider;

			AliasProvider aggAliasProvider = aggAliasProvider(newRegistered);

			newCap = new CurrentAliasProvider(aggAliasProvider, newRegistered);
		} while(! currentAliasProvider.compareAndSet(cap, newCap));
	}

	private static AliasProvider aggAliasProvider(AliasProvider[] registered) {
		if (registered.length == 0) {
			return new DefaultAliasProvider();
		} else if (registered.length == 1) {
			return registered[0];
		} else {
			return new ArrayAliasProvider(registered);
		}
	}

	private static class CurrentAliasProvider {
		AliasProvider aliasProvider;
		AliasProvider[] registered;

		public CurrentAliasProvider(AliasProvider aliasProvider, AliasProvider[] registered) {
			this.aliasProvider = aliasProvider;
			this.registered = registered;
		}
	}
}
