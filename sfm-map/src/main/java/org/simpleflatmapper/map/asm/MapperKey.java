package org.simpleflatmapper.map.asm;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.reflect.Instantiator;

import java.util.Arrays;

public class MapperKey<K extends  FieldKey<K>> {

    private final K[] keys;
    private final Class<?>[] fieldMappers;
    private final Class<?>[] constructorFieldMappers;
    private final Class<?> instantiator;
    private final Class<?> target;
    private final Class<?> source;


    public MapperKey(K[] keys,
                     FieldMapper<?, ?>[] fieldMappers,
                     FieldMapper<?, ?>[] constructorFieldMappers,
                     BiInstantiator<?, ?, ?> instantiator,
                     Class<?> target, Class<?> source) {
        this.keys = keys;
        this.source = source;
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

        MapperKey<?> mapperKey = (MapperKey<?>) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(keys, mapperKey.keys)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(fieldMappers, mapperKey.fieldMappers)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(constructorFieldMappers, mapperKey.constructorFieldMappers)) return false;
        if (!instantiator.equals(mapperKey.instantiator)) return false;
        if (!target.equals(mapperKey.target)) return false;
        return source.equals(mapperKey.source);

    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(keys);
        result = 31 * result + Arrays.hashCode(fieldMappers);
        result = 31 * result + Arrays.hashCode(constructorFieldMappers);
        result = 31 * result + instantiator.hashCode();
        result = 31 * result + target.hashCode();
        result = 31 * result + source.hashCode();
        return result;
    }
}
