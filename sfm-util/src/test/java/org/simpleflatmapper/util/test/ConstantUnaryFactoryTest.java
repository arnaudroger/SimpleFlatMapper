package org.simpleflatmapper.util.test;

import org.junit.Assert;
import org.junit.Test;
import org.simpleflatmapper.util.ConstantUnaryFactory;

public class ConstantUnaryFactoryTest {

    @Test
    public void test() {
        Assert.assertEquals("hello", ConstantUnaryFactory.<String, String>of("hello").newInstance("bye"));
    }

}