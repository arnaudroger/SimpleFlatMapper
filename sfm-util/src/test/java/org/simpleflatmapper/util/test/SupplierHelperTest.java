package org.simpleflatmapper.util.test;

import org.junit.Test;
import org.simpleflatmapper.util.Supplier;
import org.simpleflatmapper.util.SupplierHelper;

import java.util.Date;

import static org.junit.Assert.*;

public class SupplierHelperTest {


    @Test
    public void testIsSupplierOf() {
        Supplier<String> specializedSupplier = new Supplier<String>() {
            @Override
            public String get() {
                return "";
            }
        };

        assertFalse(SupplierHelper.isSupplierOf(false, String.class));

        assertTrue(SupplierHelper.isSupplierOf(specializedSupplier, String.class));
        assertFalse(SupplierHelper.isSupplierOf(specializedSupplier, Date.class));
    }



}