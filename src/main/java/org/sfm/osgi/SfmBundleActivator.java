package org.sfm.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class SfmBundleActivator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		context.registerService(JdbcMapperService.class, new JdbcMapperServiceImpl(), null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	}

}
