package org.simpleflatmapper.util.test;

import org.junit.Test;
import org.simpleflatmapper.util.ConstantBiFunction;

import static org.junit.Assert.*;

public class ConstantBiFunctionTest {
    @Test
    public void apply() throws Exception {

        assertEquals("sss", new ConstantBiFunction<Object, Object, String>("sss").apply(null, null));

    }

}