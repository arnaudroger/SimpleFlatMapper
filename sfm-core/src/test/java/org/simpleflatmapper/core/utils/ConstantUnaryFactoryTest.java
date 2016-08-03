package org.simpleflatmapper.core.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConstantUnaryFactoryTest {

    @Test
    public void test() {
        assertEquals("hello", new ConstantUnaryFactory<String, String>("hello").newInstance("bye"));
    }

}