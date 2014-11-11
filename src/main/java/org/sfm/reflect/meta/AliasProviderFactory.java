package org.sfm.reflect.meta;

public class AliasProviderFactory {
	private static boolean _isJpaPresent() {
		try {
			Class.forName("javax.persistence.Column");
			return true;
		} catch(Exception e) {
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
