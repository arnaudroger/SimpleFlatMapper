package org.sfm.reflect.meta;

import javax.persistence.Column;

public class AliasProviderFactory {
	private static boolean _isJpaPresent() {
		try {
			return Column.class != null;
		} catch(Throwable e) {
			return false;
		}
	}
	
	private static final AliasProvider aliasProvider;
	
	static {
		if (_isJpaPresent()) {
			aliasProvider = new JpaAliasProvider();
		} else {
			aliasProvider = new DefaultAliasProvider();
		}
	}

	
	public static AliasProvider getAliasProvider() {
		return aliasProvider;
	}
}
