package org.simpleflatmapper.util;

import org.junit.Assert;
import org.junit.Test;

public class ConstantUnaryFactoryTest {

    @Test
    public void test() {
        Assert.assertEquals("hello", ConstantUnaryFactory.<String, String>of("hello").newInstance("bye"));
    }

}