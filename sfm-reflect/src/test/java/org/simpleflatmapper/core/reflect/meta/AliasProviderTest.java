package org.simpleflatmapper.core.reflect.meta;

import org.junit.Test;


import static org.junit.Assert.assertNull;

public class AliasProviderTest {

    @Test
    public void testDefaultAliasProvider() {
        DefaultAliasProvider p = new DefaultAliasProvider();
        assertNull(p.getAliasForField(null));
        assertNull(p.getAliasForMethod(null));
    }

    public class TestClass {
        public String foo;
        public String getFoo() {
            return null;
        }
    }
}
