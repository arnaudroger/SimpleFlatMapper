package org.sfm.reflect.asm;

import org.sfm.map.FieldKey;
import org.sfm.map.FieldMapper;
import org.sfm.reflect.Instantiator;

import java.util.Arrays;

public class MapperKey<K extends  FieldKey<K>> {

    private final K[] keys;
    private final Class<?>[] fieldMappers;
    private final Class<?>[] constructorFieldMappers;
    private final Class<?> instantiator;
    private final Class<?> target;


    public MapperKey(K[] keys,
                     FieldMapper<?, ?>[] fieldMappers,
                     FieldMapper<?, ?>[] constructorFieldMappers,
                     Instantiator<?, ?> instantiator,
                     Class<?> target) {
        this.keys = keys;
        this.fieldMappers = getClassArray(fieldMappers);
        this.constructorFieldMappers = getClassArray(constructorFieldMappers);
        this.instantiator = getClass(instantiator);
        this.target = target;
    }

    private Class<?>[] getClassArray(Object[] objects) {
        Class<?>[] classes = new Class[objects != null ? objects.length : 0];

        if(objects != null) {
            int i = 0;
            for(Object o : objects) {
                classes[i] = getClass(o);
                i++;
            }
        }

        return classes;
    }

    private Class<?> getClass(Object o) {
        if (o != null) return o.getClass();
        return null;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapperKey that = (MapperKey) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(keys, that.keys)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(fieldMappers, that.fieldMappers)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(constructorFieldMappers, that.constructorFieldMappers)) return false;
        if (instantiator != null ? !instantiator.equals(that.instantiator) : that.instantiator != null) return false;
        return !(target != null ? !target.equals(that.target) : that.target != null);

    }

    @Override
    public int hashCode() {
        int result = keys != null ? Arrays.hashCode(keys) : 0;
        result = 31 * result + (fieldMappers != null ? Arrays.hashCode(fieldMappers) : 0);
        result = 31 * result + (constructorFieldMappers != null ? Arrays.hashCode(constructorFieldMappers) : 0);
        result = 31 * result + (instantiator != null ? instantiator.hashCode() : 0);
        result = 31 * result + (target != null ? target.hashCode() : 0);
        return result;
    }
}
