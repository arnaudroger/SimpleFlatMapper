package org.simpleflatmapper.reflect.property;

public enum MapTypeProperty {
    KEY_VALUE, COLUMN_KEY, BOTH;
    
    
    public static boolean isKeyValueEnabled(Object[] properties) {
        for(int i = 0; i < properties.length; i++) {
            Object prop = properties[i];
            if (prop == KEY_VALUE || prop == BOTH)
                return true;
        }
        return false;
    }

    public static boolean isColumnKeyEnabled(Object[] properties) {
        for(int i = 0; i < properties.length; i++) {
            Object prop = properties[i];
            if (prop == KEY_VALUE)
                return false;
        }
        return true;
    }
}
