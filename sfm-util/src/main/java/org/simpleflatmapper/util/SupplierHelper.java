package org.simpleflatmapper.util;



public final class SupplierHelper {

    private SupplierHelper() {}

    public static boolean isSupplierOf(Object potentialSupplier, Class<?> supplyClass) {
        if (!Supplier.class.isInstance(potentialSupplier)) {
            return false;
        }

        Object supply = ((Supplier)potentialSupplier).get();

        if (supply == null) return false;

        return supplyClass.isInstance(supply);
    }

}
