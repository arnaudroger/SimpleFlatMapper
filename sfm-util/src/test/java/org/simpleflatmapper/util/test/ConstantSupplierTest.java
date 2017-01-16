package org.simpleflatmapper.util.test;

import org.junit.Test;
import org.simpleflatmapper.util.ConstantSupplier;

import static org.junit.Assert.*;

public class ConstantSupplierTest {

    @Test
    public void test() {
        ConstantSupplier<String> supplier = new ConstantSupplier<String>("hello!");
        assertEquals("hello!", supplier.get());
        assertEquals("hello!", supplier.get());
    }

}