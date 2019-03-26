package org.simpleflatmapper.map.asm;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.reflect.Instantiator;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapperKey<K extends  FieldKey<K>> {

    private final K[] keys;
    private final Class<?>[] fieldMappers;
    private final Class<?>[] constructorFieldMappers;
    private final Class<?> instantiator;
    private final Class<?> target;
    private final Class<?> source;


    public MapperKey(K[] keys,
                     Class<?>[] fieldMappers,
                     Class<?>[] constructorFieldMappers,
                     BiInstantiator<?, ?, ?> instantiator,
                     Class<?> target, Class<?> source) {
        this.keys = keys;
        this.source = source;
        this.fieldMappers = fieldMappers;
        this.constructorFieldMappers = constructorFieldMappers;
        this.instantiator = instantiator != null ? instantiator.getClass() : null;
        this.target = target;
    }

    public static <T, S> MapperKey of(FieldKey<?>[] keys, FieldMapper<S, T>[] mappers, FieldMapper<S, T>[] constructorMappers, BiInstantiator<S, MappingContext<? super S>, T> instantiator, Class<T> target, Class<? super S> source) {
        Class<?>[] mappersClass = getClassArray(mappers);
        Class<?>[] constructorClass = getClassArray(constructorMappers);
        return new MapperKey(keys, mappersClass, constructorClass, instantiator, target, source);
    }

    private static Class<?>[] getClassArray(Object[] objects) {
        List<Class<?>> classes = new ArrayList<Class<?>>();

        if(objects != null) {
            int i = 0;
            for(Object o : objects) {
                addClass(o, classes);
                i++;
            }
        }

        return classes.toArray(new Class[0]);
    }

    private static void addClass(Object o, List<Class<?>> classes) {
        if (o == null) return;
        
        classes.add(o.getClass());
        // add visible getter and setter
        try {
            for(String fieldName : new String[] {"getter", "setter"}) {
                Field field = o.getClass().getDeclaredField(fieldName);
                if (Modifier.isPublic(field.getModifiers())) {
                    Object g = field.get(o);
                    if (g != null) {
                        classes.add(g.getClass());
                    }
                }

            }
        } catch(Exception e) {
//            System.out.println("e = " + e);
            //
        }
        
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

    @Override
    public String toString() {
        return "MapperKey{" +
                "keys=" + Arrays.toString(keys) +
                ", fieldMappers=" + Arrays.toString(fieldMappers) +
                ", constructorFieldMappers=" + Arrays.toString(constructorFieldMappers) +
                ", instantiator=" + instantiator +
                ", target=" + target +
                ", source=" + source +
                '}';
    }
}
