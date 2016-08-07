package org.simpleflatmapper.util;



public class SupplierHelper {
    public static boolean isSupplierOf(Object potentialSupplier, Class<?> supplyClass) {
        if (potentialSupplier.getClass() == null || supplyClass == null) {
            return false;
        }

        if (!Supplier.class.isInstance(potentialSupplier)) {
            return false;
        }

        Object supply = ((Supplier)potentialSupplier).get();

        if (supply == null) return false;

        return supplyClass.isInstance(supply);
    }

}
