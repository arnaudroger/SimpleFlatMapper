package org.sfm.osgi;

import org.junit.Test;
import org.osgi.framework.BundleContext;

import java.util.Dictionary;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SfmBundleActivatorTest {

    @Test
    public void testStart() throws Exception {

        BundleContext context = mock(BundleContext.class);


        SfmBundleActivator act = new SfmBundleActivator();

        act.start(context);

        verify(context).registerService(eq(JdbcMapperService.class), any(JdbcMapperServiceImpl.class), (Dictionary<String, ?>) isNull());
    }

    @Test
    public void testStop() throws Exception {
        SfmBundleActivator act = new SfmBundleActivator();
        act.stop(null);
    }
}