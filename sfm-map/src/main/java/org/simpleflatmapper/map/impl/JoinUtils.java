package org.simpleflatmapper.map.impl;

import org.simpleflatmapper.reflect.meta.ArrayElementPropertyMeta;
import org.simpleflatmapper.reflect.meta.MapKeyValueElementPropertyMeta;
import org.simpleflatmapper.reflect.meta.PropertyMeta;

public class JoinUtils {
    public static boolean isArrayElement(PropertyMeta propertyMeta) {
        return propertyMeta instanceof ArrayElementPropertyMeta || propertyMeta instanceof MapKeyValueElementPropertyMeta;
    }
}
