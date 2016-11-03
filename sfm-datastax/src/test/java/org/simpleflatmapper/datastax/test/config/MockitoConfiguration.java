package org.simpleflatmapper.datastax.test.config;

import org.mockito.configuration.DefaultMockitoConfiguration;

public class MockitoConfiguration extends DefaultMockitoConfiguration {
    @Override
    public boolean enableClassCache() {
        return false;
    }
}
